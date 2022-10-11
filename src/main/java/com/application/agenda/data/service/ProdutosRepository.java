package com.application.agenda.data.service;

import com.application.agenda.data.entity.Produtos;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutosRepository extends JpaRepository<Produtos, UUID> {

}