package com.application.agenda.data.service;

import com.application.agenda.data.entity.Pessoa;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

}