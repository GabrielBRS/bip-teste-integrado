package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.List;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {

    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Beneficio> listarTodos() {
        return em.createQuery("SELECT b FROM Beneficio b", Beneficio.class)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Beneficio buscarPorId(Long id) {
        return em.find(Beneficio.class, id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Beneficio criar(Beneficio beneficio) {
        beneficio.setId(null);
        em.persist(beneficio);
        return beneficio;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Beneficio atualizar(Beneficio beneficio) {
        return em.merge(beneficio);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deletar(Long id) {
        Beneficio beneficio = em.getReference(Beneficio.class, id);
        if (beneficio != null) {
            em.remove(beneficio);
        }
    }

}