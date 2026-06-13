package com.wemsellers.wem_sc_vendas.dto;

public class ItemCarrinhoDTO {
    private String nome;
    private int quantidade;
    private double precoUnitario;

    public ItemCarrinhoDTO(String nome, int quantidade, double precoUnitario) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }
    
    // Adicione este atributo na classe ItemCarrinhoDTO.java
    private String formaPagamento;

    // E adicione os métodos getters e setters para ele:
    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
