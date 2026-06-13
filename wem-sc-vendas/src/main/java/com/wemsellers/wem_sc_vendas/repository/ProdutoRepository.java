package com.wemsellers.wem_sc_vendas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wemsellers.wem_sc_vendas.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, String>{

}
