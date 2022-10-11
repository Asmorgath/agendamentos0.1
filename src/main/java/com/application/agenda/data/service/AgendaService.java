package com.application.agenda.data.service;

import com.application.agenda.data.entity.Agenda;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AgendaService {

    private final AgendaRepository repository;

    @Autowired
    public AgendaService(AgendaRepository repository) {
        this.repository = repository;
    }

    public Optional<Agenda> get(UUID id) {
        return repository.findById(id);
    }

    public Agenda update(Agenda entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Agenda> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
