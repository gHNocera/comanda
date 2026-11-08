package com.ghartmann.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Prato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagemUrl;
    private String categoria;
    private boolean disponivel = true;

    public Prato() {}

    public Prato(String nome, String descricao, BigDecimal preco, String imagemUrl, String categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public String getImagemUrl() { return imagemUrl; }
    public String getCategoria() { return categoria; }
    public boolean isDisponivel() { return disponivel; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
