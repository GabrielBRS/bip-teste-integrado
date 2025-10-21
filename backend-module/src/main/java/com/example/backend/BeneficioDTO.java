package com.example.backend;

import java.math.BigDecimal;
import com.example.ejb.Beneficio;

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