package com.wemsellers.wem_sc_vendas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wemsellers.wem_sc_vendas.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    Optional<Produto> findByCodigoBarras(String codigoBarras);
}
