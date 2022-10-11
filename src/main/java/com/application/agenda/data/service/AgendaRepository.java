package com.application.agenda.data.service;

import com.application.agenda.data.entity.Agenda;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, UUID> {

}