package com.example.backend;

import java.math.BigDecimal;

public record BeneficioDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal valor,
        Boolean ativo
) {

    public BeneficioDTO(Beneficio beneficio) {
        this(
                beneficio.getId(),
                beneficio.getNome(),
                beneficio.getDescricao(),
                beneficio.getValor(),
                beneficio.getAtivo()
        );
    }

    public Beneficio toEntity() {
        Beneficio beneficio = new Beneficio();
        beneficio.setId(this.id);
        beneficio.setNome(this.nome);
        beneficio.setDescricao(this.descricao);
        beneficio.setValor(this.valor);
        beneficio.setAtivo(this.ativo);
        return beneficio;
    }

}
