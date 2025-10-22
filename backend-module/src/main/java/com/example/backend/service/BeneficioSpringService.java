// Define o pacote da camada de serviço.
// Esta camada é responsável por conter a lógica de negócios da aplicação.
package com.example.backend.service;

// Importa a entidade de domínio. O serviço opera sobre estas entidades.
import com.example.backend.entity.Beneficio;
// Importa o "Port" de transferência. Isso sugere um padrão de design (Ports & Adapters)
// onde a lógica de transferência complexa é abstraída para fora deste serviço.
import com.example.backend.integration.BeneficioTransferPort;
// Importa o repositório, que é a interface de acesso aos dados (camada de persistência).
import com.example.backend.repository.BeneficioRepository;
// Anotação para injeção de dependência (opcional em construtores mais recentes, mas boa para clareza).
import org.springframework.beans.factory.annotation.Autowired;
// @Service marca esta classe como um componente de serviço gerenciado pelo Spring.
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
// Exceção padrão do Java usada quando um item não é encontrado.
import java.util.NoSuchElementException;

/**
 * @Service é uma anotação de estereótipo do Spring, uma especialização de @Component.
 * Ela indica que esta classe pertence à camada de lógica de negócios (Service Layer).
 * O Spring irá detectar esta classe, criar uma instância dela (um "Bean")
 * e gerenciá-la em seu contêiner de Injeção de Dependência (DI).
 */
@Service
public class BeneficioSpringService {

    // Dependência da camada de repositório (JPA).
    // É 'final' para garantir que seja inicializada no construtor (imutabilidade).
    private final BeneficioRepository repository;

    // Dependência do "Port" de transferência.
    // O serviço *orquestra* a transferência, mas *delega* a execução real
    // para este port, separando as responsabilidades (SRP).
    private final BeneficioTransferPort transferPort;

    /**
     * Construtor para Injeção de Dependência (Constructor Injection).
     * Esta é a forma *preferida* de injeção no Spring.
     * O Spring automaticamente fornecerá (injetará) as instâncias (Beans) de
     * 'BeneficioRepository' e 'BeneficioTransferPort' quando criar o 'BeneficioSpringService'.
     *
     * @param repository A implementação do repositório gerenciada pelo Spring.
     * @param transferPort A implementação do port de transferência gerenciada pelo Spring.
     */
    @Autowired // Opcional em construtores únicos, mas explícito.
    public BeneficioSpringService(BeneficioRepository repository, BeneficioTransferPort transferPort) {
        this.repository = repository;
        this.transferPort = transferPort;
    }

    /**
     * Busca todos os benefícios.
     * Simplesmente delega a chamada para o repositório.
     * * @return Uma lista de todas as entidades 'Beneficio'.
     */
    public List<Beneficio> listAll() {
        return repository.findAll();
    }

    /**
     * Busca um benefício específico pelo ID.
     *
     * @param id O ID a ser buscado.
     * @return A entidade 'Beneficio' encontrada.
     * @throws NoSuchElementException se o ID não for encontrado no banco de dados.
     */
    public Beneficio getById(Long id) {
        // repository.findById(id) retorna um 'Optional<Beneficio>'.
        // .orElseThrow() é a forma idiomática de lidar com o 'Optional' neste caso:
        // - Se o 'Optional' contiver um 'Beneficio' (presente), ele é retornado.
        // - Se o 'Optional' estiver vazio (ausente), ele lança a exceção fornecida.
        // A camada de Controller (com um @ControllerAdvice) deve capturar esta exceção
        // e traduzi-la para um status HTTP 404 Not Found.
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Beneficio não encontrado: " + id));
    }

    /**
     * Cria e persiste uma nova entidade 'Beneficio'.
     * Inclui lógica de negócios para definir valores padrão.
     * * @param b A entidade 'Beneficio' a ser criada (normalmente vinda de um DTO).
     * @return A entidade 'Beneficio' persistida (agora com um ID).
     */
    public Beneficio create(Beneficio b) {
        // Lógica de negócios: Garante que um benefício novo nunca tenha valor nulo.
        if (b.getValor() == null) {
            b.setValor(BigDecimal.ZERO);
        }
        // Lógica de negócios: Garante que um benefício novo seja 'ativo' por padrão.
        if (b.getAtivo() == null) {
            b.setAtivo(Boolean.TRUE);
        }
        // O método 'save' do Spring Data JPA é inteligente:
        // Se a entidade 'b' não tem ID (ou o ID é nulo), ele executa um INSERT.
        return repository.save(b);
    }

    /**
     * Atualiza uma entidade 'Beneficio' existente.
     * Esta é uma operação "read-then-write" (ler e depois escrever).
     *
     * @param id O ID do benefício a ser atualizado.
     * @param changes Uma entidade 'Beneficio' *desanexada* (detached) contendo as mudanças.
     * @return A entidade 'Beneficio' atualizada e persistida.
     *
     * Nota: Este método se beneficiaria muito da anotação @Transactional.
     * Com @Transactional, 'current' seria uma entidade *gerenciada* (managed) pelo
     * JPA. As alterações (setters) seriam rastreadas (dirty checking) e
     * persistidas no commit da transação, tornando a chamada 'repository.save(current)'
     * tecnicamente redundante, mas ainda assim inofensiva e explícita.
     */
    public Beneficio update(Long id, Beneficio changes) {
        // 1. Busca a entidade *atual* do banco. Isso garante que estamos
        //    atualizando um registro que realmente existe. 'current' é a entidade gerenciada.
        Beneficio current = getById(id);

        // 2. Aplica manualmente as mudanças da entidade 'changes' (vinda da requisição)
        //    para a entidade 'current' (vinda do banco).
        current.setNome(changes.getNome());
        current.setDescricao(changes.getDescricao());
        current.setValor(changes.getValor());
        current.setAtivo(changes.getAtivo());

        // 3. O método 'save' do Spring Data JPA, quando usado em uma entidade
        //    que *já tem* um ID e foi carregada do banco, executa um UPDATE.
        return repository.save(current);
    }

    /**
     * Deleta um benefício pelo seu ID.
     *
     * @param id O ID do benefício a ser deletado.
     */
    public void delete(Long id) {
        // Delega a operação diretamente para o repositório.
        // O Spring Data JPA lidará com a busca e exclusão.
        // Se o ID não existir, ele pode (dependendo da configuração)
        // lançar uma EmptyResultDataAccessException, que também deve ser
        // tratada pelo @ControllerAdvice.
        repository.deleteById(id);
    }

    /**
     * Orquestra uma operação de transferência de valores.
     *
     * @param fromId O ID da conta/benefício de origem.
     * @param toId O ID da conta/benefício de destino.
     * @param amount O valor a ser transferido.
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Delegação para o Port.
        // A lógica real de transferência (ex: verificar saldo, subtrair de um,
        // adicionar a outro, garantir atomicidade com @Transactional)
        // está encapsulada dentro da implementação do 'transferPort'.
        // Isso mantém o 'BeneficioSpringService' limpo e focado em orquestração.
        transferPort.transfer(fromId, toId, amount);
    }

}