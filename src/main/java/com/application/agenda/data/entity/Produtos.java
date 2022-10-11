package com.application.agenda.data.entity;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class Produtos extends AbstractEntity {

    @Nonnull
    private Integer codigo;
    @Nonnull
    private String descricao;
    @Nonnull
    private Integer valor;
    @Email
    @Nonnull
    private String tempoMedio;
    @Nonnull
    private Integer tipo;
    private boolean statusProduto;
    @Nonnull
    private UUID idProduto;

    public Integer getCodigo() {
        return codigo;
    }
    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public Integer getValor() {
        return valor;
    }
    public void setValor(Integer valor) {
        this.valor = valor;
    }
    public String getTempoMedio() {
        return tempoMedio;
    }
    public void setTempoMedio(String tempoMedio) {
        this.tempoMedio = tempoMedio;
    }
    public Integer getTipo() {
        return tipo;
    }
    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }
    public boolean isStatusProduto() {
        return statusProduto;
    }
    public void setStatusProduto(boolean statusProduto) {
        this.statusProduto = statusProduto;
    }
    public UUID getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(UUID idProduto) {
        this.idProduto = idProduto;
    }

}
