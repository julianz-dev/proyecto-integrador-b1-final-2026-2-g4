package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clienteId;
    private long negocioId;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombreRazonSocial;
    private boolean activo;
    private boolean esGenerico;


    public Cliente() {
    }

    public Cliente(long negocioId, String tipoDocumento, String numeroDocumento, String nombreRazonSocial, boolean activo, boolean esGenerico) {
        this.negocioId = negocioId;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombreRazonSocial = nombreRazonSocial;
        this.activo = activo;
        this.esGenerico = esGenerico;
    }

    public Cliente(long clienteId, long negocioId, String tipoDocumento, String numeroDocumento, String nombreRazonSocial, boolean activo, boolean esGenerico) {
        this.clienteId = clienteId;
        this.negocioId = negocioId;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombreRazonSocial = nombreRazonSocial;
        this.activo = activo;
        this.esGenerico = esGenerico;
    }

    public long getClienteId() {
        return clienteId;
    }

    public void setClienteId(long clienteId) {
        this.clienteId = clienteId;
    }

    public long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(long negocioId) {
        this.negocioId = negocioId;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isEsGenerico() {
        return esGenerico;
    }

    public void setEsGenerico(boolean esGenerico) {
        this.esGenerico = esGenerico;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "clienteId=" + clienteId +
                ", negocioId=" + negocioId +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", nombreRazonSocial='" + nombreRazonSocial + '\'' +
                ", activo=" + activo +
                ", esGenerico=" + esGenerico +
                '}';
    }
}
