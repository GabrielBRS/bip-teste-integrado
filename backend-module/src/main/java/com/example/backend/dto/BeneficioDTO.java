package com.example.backend.dto;

import com.example.backend.entity.Beneficio;

import java.math.BigDecimal;

public record BeneficioDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal valor,
        Boolean ativo
        // Long version
) {


    public BeneficioDTO(Beneficio beneficio) {
        this(
                beneficio.getId(),
                beneficio.getNome(),
                beneficio.getDescricao(),
                beneficio.getValor(),
                beneficio.getAtivo()
                // beneficio.getVersion()
        );
    }

    public Beneficio toEntity() {
        Beneficio beneficio = new Beneficio();
        beneficio.setId(this.id);
        beneficio.setNome(this.nome);
        beneficio.setDescricao(this.descricao);
        beneficio.setValor(this.valor);
        beneficio.setAtivo(this.ativo);
        // beneficio.setVersion(this.version);
        return beneficio;
    }

}