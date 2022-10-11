package com.application.agenda.data.service;

import com.application.agenda.data.entity.Pessoa;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {

    private final PessoaRepository repository;

    @Autowired
    public PessoaService(PessoaRepository repository) {
        this.repository = repository;
    }

    public Optional<Pessoa> get(UUID id) {
        return repository.findById(id);
    }

    public Pessoa update(Pessoa entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Pessoa> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
