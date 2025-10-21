package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo.");
        }

        Beneficio from = em.find(Beneficio.class, fromId);
        Beneficio to   = em.find(Beneficio.class, toId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("Conta de origem ou destino não encontrada.");
        }

        if (from.getValor().compareTo(amount) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para a transferência.");
        }

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        try {
            em.merge(from);
            em.merge(to);

            em.flush();

        } catch (OptimisticLockException e) {
            throw new RuntimeException("A operação não pôde ser concluída devido a uma atualização concorrente. Por favor, tente novamente.", e);
        }
    }

}
