package com.ghartmann.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PedidoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    private Prato prato;

    private int quantidade;
    private String observacao;

    public PedidoItem() {}

    public PedidoItem(Prato prato, int quantidade, String observacao) {
        this.prato = prato;
        this.quantidade = quantidade;
        this.observacao = observacao;
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Prato getPrato() { return prato; }
    public int getQuantidade() { return quantidade; }
    public String getObservacao() { return observacao; }
}
