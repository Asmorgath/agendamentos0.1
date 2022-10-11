package com.application.agenda.data.service;

import com.application.agenda.data.entity.Produtos;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutosService {

    private final ProdutosRepository repository;

    @Autowired
    public ProdutosService(ProdutosRepository repository) {
        this.repository = repository;
    }

    public Optional<Produtos> get(UUID id) {
        return repository.findById(id);
    }

    public Produtos update(Produtos entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Produtos> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
