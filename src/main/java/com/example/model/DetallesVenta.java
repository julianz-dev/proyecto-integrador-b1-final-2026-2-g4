package com.example.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalles_venta")
public class DetallesVenta {
    private Long detalleVentaId;
    private Long ventaId;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private String descripcionProducto;
    private BigDecimal subtotal;

    public DetallesVenta() {
    }

    public DetallesVenta(Long ventaId, Long productoId, Integer cantidad, BigDecimal precioUnitario, String descripcionProducto, BigDecimal subtotal) {
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descripcionProducto = descripcionProducto;
        this.subtotal = subtotal;
    }

    public DetallesVenta(Long detalleVentaId, Long ventaId, Long productoId, Integer cantidad, BigDecimal precioUnitario, String descripcionProducto, BigDecimal subtotal) {
        this.detalleVentaId = detalleVentaId;
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descripcionProducto = descripcionProducto;
        this.subtotal = subtotal;
    }

    public Long getDetalleVentaId() {
        return detalleVentaId;
    }

    public void setDetalleVentaId(Long detalleVentaId) {
        this.detalleVentaId = detalleVentaId;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "DetallesVenta{" +
                "detalleVentaId=" + detalleVentaId +
                ", ventaId=" + ventaId +
                ", productoId=" + productoId +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", descripcionProducto='" + descripcionProducto + '\'' +
                ", subtotal=" + subtotal +
                '}';
    }
}
