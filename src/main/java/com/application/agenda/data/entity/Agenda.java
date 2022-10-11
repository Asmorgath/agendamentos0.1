package com.application.agenda.data.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class Agenda extends AbstractEntity {

    @Nonnull
    private String firstName;
    @Nonnull
    private String lastName;
    @Email
    @Nonnull
    private String email;
    @Nonnull
    private String celular;
    private LocalDate dataNascimento;
    private LocalDate dataAgendamento;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    @Nonnull
    private Integer idCliente;
    @Nonnull
    private String observacaoAgenda;
    @Nonnull
    private String valorTotal;
    @Nonnull
    private String statusAgenda;
    @Nonnull
    private UUID idAgenda;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCelular() {
        return celular;
    }
    public void setCelular(String celular) {
        this.celular = celular;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }
    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }
    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    public LocalDateTime getHoraFim() {
        return horaFim;
    }
    public void setHoraFim(LocalDateTime horaFim) {
        this.horaFim = horaFim;
    }
    public Integer getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
    public String getObservacaoAgenda() {
        return observacaoAgenda;
    }
    public void setObservacaoAgenda(String observacaoAgenda) {
        this.observacaoAgenda = observacaoAgenda;
    }
    public String getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }
    public String getStatusAgenda() {
        return statusAgenda;
    }
    public void setStatusAgenda(String statusAgenda) {
        this.statusAgenda = statusAgenda;
    }
    public UUID getIdAgenda() {
        return idAgenda;
    }
    public void setIdAgenda(UUID idAgenda) {
        this.idAgenda = idAgenda;
    }

}
