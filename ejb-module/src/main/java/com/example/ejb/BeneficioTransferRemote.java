// Define o pacote. Este pacote contém componentes EJB (Enterprise JavaBeans).
package com.example.ejb;

// Importa a anotação @Remote do Jakarta EE (anteriormente Java EE).
// Esta é a anotação crucial para definir uma interface de negócios remota.
import jakarta.ejb.Remote;

// Importa BigDecimal, a classe padrão do Java para lidar com
// valores monetários e cálculos financeiros de precisão,
// garantindo que não haja erros de arredondamento.
import java.math.BigDecimal;

/**
 * @Remote: Esta é a anotação principal deste arquivo. Ela "marca" esta
 * interface como uma "Remote Business Interface" (Interface de Negócios Remota) de um EJB.
 *
 * O que isso significa?
 * 1.  **Acesso Distribuído (Fora da JVM):** Esta anotação informa ao container EJB
 * (ex: WildFly, GlassFish) que o EJB que implementar esta interface
 * (como 'BeneficioEjbService') deve ser acessível por clientes que estão
 * em *outras JVMs* (Java Virtual Machines).
 * Isso pode ser outra aplicação em outro servidor, um cliente desktop, etc.
 *
 * 2.  **Contrato de Visão:** A interface define um "contrato" estrito.
 * Clientes remotos *só* poderão ver e chamar os métodos definidos aqui.
 * O EJB pode ter dezenas de outros métodos públicos, mas se eles não
 * estiverem nesta interface, eles são invisíveis para o cliente remoto.
 *
 * 3.  **Passagem por Valor (Pass-by-Value):** Como o cliente e o servidor estão em
 * JVMs diferentes, os objetos não podem ser passados por referência de memória.
 * O container EJB automaticamente *serializa* (converte em bytes)
 * todos os parâmetros (fromId, toId, amount) no cliente, envia-os pela
 * rede, e *desserializa* (reconstrói os objetos) no servidor.
 * Isso é chamado de "passagem por valor".
 *
 * 4.  **A Alternativa (@Local):** A alternativa seria a anotação '@Local',
 * que restringe o acesso ao EJB apenas a clientes *dentro* da mesma JVM,
 * permitindo a passagem por referência (muito mais rápido, pois não há rede).
 */
@Remote
public interface BeneficioTransferRemote {

    /**
     * Define a assinatura do método 'transfer' que será exposto remotamente.
     *
     * Qualquer EJB (como 'BeneficioEjbService') que declarar 'implements BeneficioTransferRemote'
     * é *obrigado* a fornecer uma implementação concreta (a lógica) para este método.
     *
     * O container EJB garantirá que, quando o cliente remoto chamar este método,
     * a implementação no EJB seja executada, gerenciando automaticamente a
     * conexão de rede, a serialização dos dados e, crucialmente, o
     * contexto de transação (como vimos no '@TransactionAttribute' do EJB).
     *
     * @param fromId O ID da conta/benefício de origem. (Long é serializável)
     * @param toId   O ID da conta/benefício de destino. (Long é serializável)
     * @param amount O valor (em BigDecimal) a ser transferido. (BigDecimal é serializável)
     */
    void transfer(Long fromId, Long toId, BigDecimal amount);

} // Fim da interface