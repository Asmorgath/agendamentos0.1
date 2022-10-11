package com.application.agenda.data.entity;

import java.time.LocalDate;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class Pessoa extends AbstractEntity {

    @Nonnull
    private String primeiroNome;
    @Nonnull
    private String ultimoNome;
    @Email
    @Nonnull
    private String email;
    @Nonnull
    private String celular;
    private LocalDate dataNascimento;
    private boolean status;
    @Nonnull
    private String tipoPessoa;
    @Nonnull
    private UUID idPessoa;
    @Nonnull
    private Integer codigo;

    public String getPrimeiroNome() {
        return primeiroNome;
    }
    public void setPrimeiroNome(String primeiroNome) {
        this.primeiroNome = primeiroNome;
    }
    public String getUltimoNome() {
        return ultimoNome;
    }
    public void setUltimoNome(String ultimoNome) {
        this.ultimoNome = ultimoNome;
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
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getTipoPessoa() {
        return tipoPessoa;
    }
    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }
    public UUID getIdPessoa() {
        return idPessoa;
    }
    public void setIdPessoa(UUID idPessoa) {
        this.idPessoa = idPessoa;
    }
    public Integer getCodigo() {
        return codigo;
    }
    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

}
