// Define o pacote. "integration" (integração) é um nome comum para "Adaptadores"
// no padrão "Ports & Adapters" (Hexagonal). Esta classe é um adaptador
// que implementa a lógica de transferência *localmente*.
package com.example.backend.integration;

// Importa a entidade de domínio.
import com.example.backend.entity.Beneficio;
// Importa as classes do JPA (Jakarta Persistence API).
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType; // 👈 Importante! Para controle de concorrência.
import jakarta.persistence.PersistenceContext;
// Importa o logging (SLF4J).
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Importa a anotação de configuração condicional do Spring Boot.
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Importa a anotação de Serviço do Spring.
import org.springframework.stereotype.Service;
// Importa a anotação de Transação do Spring.
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException; // Exceção para item não encontrado.

/**
 * @Service: Marca esta classe como um Bean gerenciado pelo Spring.
 * É o equivalente Spring ao @Stateless do EJB.
 */
@Service
/**
 * @ConditionalOnProperty: Esta é uma anotação poderosa do Spring Boot.
 * Ela instrui o Spring a *só* criar este Bean (esta implementação)
 * se a condição for atendida.
 *
 * prefix = "ejb.beneficio", name = "enabled", havingValue = "false":
 * O Spring irá procurar no 'application.properties' por 'ejb.beneficio.enabled=false'.
 *
 * matchIfMissing = true:
 * Esta é a parte crucial. Se a propriedade 'ejb.beneficio.enabled'
 * *nem sequer existir* no 'application.properties', a condição também será atendida.
 *
 * Resumo: Este Bean 'LocalBeneficioTransferService' será o "padrão"
 * (a implementação padrão da interface 'BeneficioTransferPort'),
 * a menos que você *ative explicitamente* a versão EJB no seu
 * arquivo de configuração (definindo ejb.beneficio.enabled=true).
 * Isso permite alternar entre uma implementação local e uma remota (EJB)
 * apenas mudando uma linha de configuração.
 */
@ConditionalOnProperty(prefix = "ejb.beneficio", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LocalBeneficioTransferService implements BeneficioTransferPort {

    /**
     * @PersistenceContext: Injeta o EntityManager (Gerenciador de Entidades) do JPA.
     * Esta é a forma padrão do Jakarta EE (e suportada pelo Spring) de
     * obter acesso direto ao "motor" do JPA, permitindo controle total
     * sobre operações, incluindo 'find', 'merge', 'persist' e 'flush',
     * e, mais importante, sobre o *travamento* (locking).
     */
    @PersistenceContext
    private EntityManager em;

    // Logger estático padrão para esta classe.
    private static final Logger log = LoggerFactory.getLogger(LocalBeneficioTransferService.class);

    /**
     * @Transactional: Esta é a anotação de transação do Spring (equivalente
     * ao @TransactionAttribute(REQUIRED) do EJB).
     * Ela garante que todo o método 'transfer' seja executado dentro de uma
     * única transação de banco de dados.
     *
     * Se qualquer exceção (ex: IllegalArgumentException, IllegalStateException)
     * for lançada, o Spring automaticamente executará um *ROLLBACK*,
     * desfazendo o débito e o crédito, garantindo a Atomicidade (o 'A' do ACID).
     */
    @Transactional
    @Override // Indica que este método está implementando um método da interface BeneficioTransferPort.
    public void transfer(Long fromId, Long toId, BigDecimal amount) {

        // --- 1. Bloco de Validação (Fail-Fast) ---
        // Verifica as entradas (parâmetros) antes de tocar no banco de dados.
        // Se a entrada for inválida, falha rapidamente com uma exceção.
        if (fromId == null || toId == null) throw new IllegalArgumentException("IDs devem ser fornecidos");
        if (fromId.equals(toId)) throw new IllegalArgumentException("fromId and toId devem ser diferentes");

        // .signum() retorna 0 (para zero), 1 (para positivo) ou -1 (para negativo).
        // A transferência só pode ocorrer com um valor estritamente positivo.
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("O valor deve ser positivo");

        log.debug("Iniciando transferência local: from={} to={} amount={}", fromId, toId, amount);

        // --- 2. Busca com Lock Otimista (Controle de Concorrência) ---

        // Busca a entidade de origem (from).
        // LockModeType.OPTIMISTIC_FORCE_INCREMENT:
        // Esta é uma estratégia de "Lock Otimista". Ela presume que conflitos
        // de concorrência são raros, mas se prepara para eles.
        // Para funcionar, a entidade 'Beneficio' precisa de uma coluna de versão
        // (ex: @Version private Long version;).
        // Ao ler ('em.find'), o JPA verifica a versão. Ao final da transação
        // (no 'em.flush' ou 'commit'), ele tentará fazer o UPDATE *e*
        // incrementar o número da versão, mas *somente se* a versão no banco
        // ainda for a mesma que ele leu.
        // Se outra transação alterou a conta no meio tempo (mudando a versão),
        // esta transação falhará com uma 'OptimisticLockException'.
        // Isso previne "race conditions" (condições de corrida), onde duas
        // transferências poderiam debitar da mesma conta ao mesmo tempo
        // baseadas em um saldo desatualizado.
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        // Busca a entidade de destino (to) aplicando o mesmo lock.
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        // --- 3. Bloco de Regras de Negócio ---
        if (from == null || to == null) {
            throw new NoSuchElementException("Beneficio não encontrado");
        }
        if (Boolean.FALSE.equals(from.getAtivo()) || Boolean.FALSE.equals(to.getAtivo())) {
            throw new IllegalStateException("Ambos os Benefícios devem estar ativos");
        }
        // .compareTo() retorna < 0 se 'from.getValor()' for *menor* que 'amount'.
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        // --- 4. Execução da Transação (Débito e Crédito) ---
        // Se todas as validações e regras de negócio passaram, a operação é executada.
        from.setValor(from.getValor().subtract(amount)); // Debita da origem
        to.setValor(to.getValor().add(amount));     // Credita no destino

        // em.merge() informa ao JPA para sincronizar estas mudanças com o banco.
        // Dentro de uma @Transactional, as entidades 'from' e 'to' já estão
        // "gerenciadas" (managed), então o JPA detectaria as mudanças
        // (dirty checking) e as salvaria no commit de qualquer forma.
        // A chamada 'merge' aqui é explícita e inofensiva.
        em.merge(from);
        em.merge(to);

        // em.flush(): Força o JPA a enviar os comandos SQL (os UPDATEs)
        // para o banco de dados *agora*, em vez de esperar o fim do método
        // (o commit da transação).
        // Isso é útil para capturar erros (como a OptimisticLockException
        // ou violações de constraints do BD) imediatamente, antes de
        // executar qualquer lógica adicional.
        em.flush();

        log.info("Transferência local concluída: from={} to={} amount={}", fromId, toId, amount);
    }
    // --- 5. Commit ---
    // Se o método terminar sem lançar nenhuma exceção, a anotação @Transactional
    // instrui o Spring a fazer o *COMMIT* da transação, tornando as
    // mudanças (débito e crédito) permanentes no banco de dados.
}