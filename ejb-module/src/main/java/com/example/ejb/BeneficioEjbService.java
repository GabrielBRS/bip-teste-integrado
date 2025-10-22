// Define o pacote. Este EJB (Enterprise JavaBean) está na camada de lógica de negócios.
package com.example.ejb;

// Importa as anotações principais do EJB (agora parte do Jakarta EE).
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
// Importa as classes do JPA (Jakarta Persistence API) para interação com o BD.
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Stateless: Define esta classe como um EJB "Stateless Session Bean".
 * Esta é uma anotação central do EJB, similar ao @Service do Spring.
 * O container de EJB (ex: WildFly, GlassFish, OpenLiberty) gerenciará um pool
 * destas instâncias. Elas não guardam estado do cliente entre chamadas,
 * o que as torna eficientes e altamente escaláveis.
 */
@Stateless
public class BeneficioEjbService implements BeneficioTransferRemote {
    // A implementação da interface 'BeneficioTransferRemote' sugere que este EJB
    // pode ser chamado remotamente (de outra máquina/JVM), um recurso clássico de EJBs.

    /**
     * @PersistenceContext injeta o EntityManager, a principal interface do JPA.
     * Esta é a forma padrão do Jakarta EE de fazer injeção de dependência do JPA.
     * É o equivalente a injetar o 'BeneficioRepository' no Spring, mas aqui
     * estamos injetando o "motor" do JPA diretamente, em vez de uma abstração (Repository).
     *
     * O 'unitName = "default"' refere-se à unidade de persistência configurada
     * no arquivo 'persistence.xml' do projeto.
     */
    @PersistenceContext(unitName = "default")
    private EntityManager em; // 'em' (EntityManager) é o objeto usado para todas as operações de BD.

    /**
     * @TransactionAttribute: Define o comportamento transacional do método.
     * TransactionAttributeType.REQUIRED: (Este é o padrão para EJBs)
     * 1. Se o cliente que chama já tiver uma transação, este método se junta a ela.
     * 2. Se não houver transação, o container EJB *cria* uma nova antes de executar o método.
     *
     * Isso garante que a transferência seja ATÔMICA (ou tudo funciona, ou nada é salvo).
     * É o equivalente EJB ao '@Transactional' do Spring.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // A lógica de transferência (buscar, debitar, creditar) seria implementada aqui
        // e estaria protegida pela transação gerenciada pelo container.
    }

    /**
     * @TransactionAttribute(TransactionAttributeType.SUPPORTS):
     * Esta é uma otimização para métodos de leitura (read-only).
     * 1. Se uma transação existir, o método se junta a ela.
     * 2. Se não existir, o método é executado *sem* uma transação.
     * Como uma consulta (SELECT) não modifica dados, criar uma transação
     * para ela seria um overhead desnecessário.
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Beneficio> listarTodos() {
        // Usa o EntityManager para criar uma query JPQL (Java Persistence Query Language).
        // "SELECT b FROM Beneficio b" é similar ao SQL "SELECT * FROM beneficio".
        // .getResultList() executa a query e retorna a lista de entidades 'Beneficio'.
        // Isso é o equivalente a 'repository.findAll()' do Spring Data JPA.
        return em.createQuery("SELECT b FROM Beneficio b", Beneficio.class)
                .getResultList();
    }

    /**
     * Novamente, SUPPORTS é usado para uma operação de leitura.
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Beneficio buscarPorId(Long id) {
        // 'em.find' é o método JPA padrão para buscar uma entidade pela sua chave primária (ID).
        // É o equivalente a 'repository.findById(id).orElse(null)' do Spring Data.
        // Se o ID não for encontrado, ele retorna 'null'.
        return em.find(Beneficio.class, id);
    }

    /**
     * REQUIRED é usado pois esta é uma operação de escrita (write).
     * Deve ser transacional.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Beneficio criar(Beneficio beneficio) {
        // Definir o ID como nulo é uma boa prática para garantir que o JPA
        // entenda que esta é uma entidade *nova* e deve fazer um INSERT.
        beneficio.setId(null);

        // 'em.persist' é o comando JPA para salvar uma *nova* entidade no banco de dados.
        // A entidade 'beneficio' se torna "gerenciada" (managed) pelo JPA.
        // O ID (se for autogerado) será populado no objeto 'beneficio' após o commit.
        em.persist(beneficio);

        // Retorna a entidade gerenciada.
        return beneficio;
    }

    /**
     * REQUIRED é usado pois esta é uma operação de escrita (write).
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Beneficio atualizar(Beneficio beneficio) {
        // 'em.merge' é o comando JPA para atualizar uma entidade.
        // Ele "mescla" o estado do objeto 'beneficio' (que pode estar "desanexado",
        // vindo de uma requisição web, por exemplo) com uma entidade gerenciada no
        // contexto de persistência.
        // Resumindo: Ele encontra o Beneficio pelo ID e atualiza seus campos no banco.
        // É o equivalente a 'repository.save(beneficio)' do Spring Data para uma
        // entidade que *já possui* um ID.
        return em.merge(beneficio);
    }

    /**
     * REQUIRED é usado pois esta é uma operação de escrita (delete).
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deletar(Long id) {
        // 'em.getReference' é uma otimização. Ele obtém uma "referência" (proxy) para a
        // entidade sem necessariamente carregá-la inteira do banco de dados.
        // É útil quando você só precisa do objeto para passá-lo para outro método.
        // Alternativamente, 'em.find(Beneficio.class, id)' também funcionaria.
        Beneficio beneficio = em.getReference(Beneficio.class, id);

        // Se a referência for válida (encontrou o proxy)
        if (beneficio != null) {
            // 'em.remove' marca a entidade para ser removida do banco de dados
            // quando a transação for commitada.
            // É o equivalente a 'repository.deleteById(id)' do Spring Data.
            em.remove(beneficio);
        }
    }

}