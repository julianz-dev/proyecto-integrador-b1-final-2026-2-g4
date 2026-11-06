package com.example.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productoId;
    private Long negocioId;
    private Long categoriaId;
    private String codigoProducto;
    private String nombreProducto;
    private String descripcion;
    private BigDecimal precioVenta;
    private Long tipoItemId;
    private boolean manejaInventario;
    private boolean activo;
	

    public Producto() {
    }

    public Producto(Long negocioId, Long categoriaId, String codigoProducto, String nombreProducto, String descripcion, BigDecimal precioVenta, Long tipoItemId, boolean manejaInventario, boolean activo) {
        this.negocioId = negocioId;
        this.categoriaId = categoriaId;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.tipoItemId = tipoItemId;
        this.manejaInventario = manejaInventario;
        this.activo = activo;
    }

    public Producto(Long productoId, Long negocioId, Long categoriaId, String codigoProducto, String nombreProducto, String descripcion, BigDecimal precioVenta, Long tipoItemId, boolean manejaInventario, boolean activo) {
        this.productoId = productoId;
        this.negocioId = negocioId;
        this.categoriaId = categoriaId;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.tipoItemId = tipoItemId;
        this.manejaInventario = manejaInventario;
        this.activo = activo;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Long getTipoItemId() {
        return tipoItemId;
    }

    public void setTipoItemId(Long tipoItemId) {
        this.tipoItemId = tipoItemId;
    }

    public boolean isManejaInventario() {
        return manejaInventario;
    }

    public void setManejaInventario(boolean manejaInventario) {
        this.manejaInventario = manejaInventario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "productoId=" + productoId +
                ", negocioId=" + negocioId +
                ", categoriaId=" + categoriaId +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precioVenta=" + precioVenta +
                ", tipoItemId=" + tipoItemId +
                ", manejaInventario=" + manejaInventario +
                ", activo=" + activo +
                '}';
    }
}
