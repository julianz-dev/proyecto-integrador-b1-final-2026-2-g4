package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "ventas")
public class Ventas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ventaId;
    private UUID uuidLocal;
    private Long negocioId;
    private Long clienteId;
    private Long usuarioId;
    private LocalDateTime fechaVenta;
    private String estadoVenta;
    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal impuestoTotal;
    private BigDecimal totalVenta;
    private Long origenId;

    public Ventas() {
    }

    public Ventas(UUID uuidLocal, Long negocioId, Long clienteId, Long usuarioId, LocalDateTime fechaVenta, String estadoVenta, BigDecimal subtotal, BigDecimal descuentoTotal, BigDecimal impuestoTotal, BigDecimal totalVenta, Long origenId) {
        this.uuidLocal = uuidLocal;
        this.negocioId = negocioId;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.fechaVenta = fechaVenta;
        this.estadoVenta = estadoVenta;
        this.subtotal = subtotal;
        this.descuentoTotal = descuentoTotal;
        this.impuestoTotal = impuestoTotal;
        this.totalVenta = totalVenta;
        this.origenId = origenId;
    }

    public Ventas(Long ventaId, UUID uuidLocal, Long negocioId, Long clienteId, Long usuarioId, LocalDateTime fechaVenta, String estadoVenta, BigDecimal subtotal, BigDecimal descuentoTotal, BigDecimal impuestoTotal, BigDecimal totalVenta, Long origenId) {
        this.ventaId = ventaId;
        this.uuidLocal = uuidLocal;
        this.negocioId = negocioId;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.fechaVenta = fechaVenta;
        this.estadoVenta = estadoVenta;
        this.subtotal = subtotal;
        this.descuentoTotal = descuentoTotal;
        this.impuestoTotal = impuestoTotal;
        this.totalVenta = totalVenta;
        this.origenId = origenId;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public UUID getUuidLocal() {
        return uuidLocal;
    }

    public void setUuidLocal(UUID uuidLocal) {
        this.uuidLocal = uuidLocal;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getEstadoVenta() {
        return estadoVenta;
    }

    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(BigDecimal descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public BigDecimal getImpuestoTotal() {
        return impuestoTotal;
    }

    public void setImpuestoTotal(BigDecimal impuestoTotal) {
        this.impuestoTotal = impuestoTotal;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public Long getOrigenId() {
        return origenId;
    }

    public void setOrigenId(Long origenId) {
        this.origenId = origenId;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "ventaId=" + ventaId +
                ", uuidLocal=" + uuidLocal +
                ", negocioId=" + negocioId +
                ", clienteId=" + clienteId +
                ", usuarioId=" + usuarioId +
                ", fechaVenta=" + fechaVenta +
                ", estadoVenta='" + estadoVenta + '\'' +
                ", subtotal=" + subtotal +
                ", descuentoTotal=" + descuentoTotal +
                ", impuestoTotal=" + impuestoTotal +
                ", totalVenta=" + totalVenta +
                ", origenId=" + origenId +
                '}';
    }
}
