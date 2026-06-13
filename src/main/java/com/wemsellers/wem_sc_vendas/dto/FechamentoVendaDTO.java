package com.wemsellers.wem_sc_vendas.dto;

import java.util.List;

public class FechamentoVendaDTO {
    private List<ItemCarrinhoDTO> itens;
    private String formaPagamento;

    // Getters e Setters
    public List<ItemCarrinhoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinhoDTO> itens) {
        this.itens = itens;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
