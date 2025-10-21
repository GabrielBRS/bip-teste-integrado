//package com.example.backend;
//
//import jakarta.persistence.*;
//import java.math.BigDecimal;
//
//@Entity
//@Table(name = "BENEFICIO")
//public class Beneficio {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String nome;
//
//    private String descricao;
//
//    @Column(nullable = false)
//    private BigDecimal valor;
//
//    private Boolean ativo = true;
//
//    @Version
//    private Long version;
//
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getNome() { return nome; }
//    public void setNome(String nome) { this.nome = nome; }
//    public String getDescricao() { return descricao; }
//    public void setDescricao(String descricao) { this.descricao = descricao; }
//    public BigDecimal getValor() { return valor; }
//    public void setValor(BigDecimal valor) { this.valor = valor; }
//    public Boolean getAtivo() { return ativo; }
//    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
//    public Long getVersion() { return version; }
//    public void setVersion(Long version) { this.version = version; }
//
//}
