package com.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.model.Clientes;
import com.example.model.DetallesVenta;
import com.example.model.Productos;
import com.example.model.Ventas;
import com.example.service.CrudService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Gestión CRUD - Sistema de Inventario")
@Route("")
public class MainView extends VerticalLayout {

    private final CrudService crudService;

    public MainView(CrudService crudService) {
        this.crudService = crudService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        tabs.add("Productos", crearSeccionProductos());
        tabs.add("Clientes", crearSeccionClientes());
        tabs.add("DetallesVenta", crearSeccionDetallesVenta());
        tabs.add("Ventas", crearSeccionVentas());

        add(new H2("Sistema de Inventario - CRUD"), tabs);
    }

    // ==========================================
    // 1. PRODUCTOS
    // ==========================================
    private Component crearSeccionProductos() {
        VerticalLayout layout = new VerticalLayout();
        TextField productoIdField = new TextField("Producto ID");
        TextField negocioIdField = new TextField("Negocio ID");
        TextField categoriaIdField = new TextField("Categoría ID");
        TextField codigoProductoField = new TextField("Código Producto");
        TextField nombreProductoField = new TextField("Nombre Producto");
        TextField descripcionField = new TextField("Descripción");
        TextField precioVentaField = new TextField("Precio Venta");
        TextField tipoItemField = new TextField("Tipo Item ID");
        TextField manejaInventarioField = new TextField("Maneja Inventario (1/0)");
        TextField activoField = new TextField("Activo (1/0)");

        FormLayout form = new FormLayout(productoIdField, negocioIdField, categoriaIdField, codigoProductoField, nombreProductoField, descripcionField, precioVentaField, tipoItemField, manejaInventarioField, activoField);

        Grid<Productos> grid = new Grid<>(Productos.class, false);
        grid.addColumn(Productos::getProductoId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Productos::getNegocioId).setHeader("Negocio").setAutoWidth(true);
        grid.addColumn(Productos::getCategoriaId).setHeader("Categoría").setAutoWidth(true);
        grid.addColumn(Productos::getCodigoProducto).setHeader("Código").setAutoWidth(true);
        grid.addColumn(Productos::getNombreProducto).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Productos::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        grid.addColumn(Productos::getPrecioVenta).setHeader("Precio Venta").setAutoWidth(true);
        grid.addColumn(Productos::getTipoItemId).setHeader("Tipo Item").setAutoWidth(true);
        grid.addColumn(Productos::isManejaInventario).setHeader("Maneja Inventario").setAutoWidth(true);
        grid.addColumn(Productos::isActivo).setHeader("Activo").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Productos.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Productos p = e.getValue();
            if (p != null) {
                productoIdField.setValue(p.getProductoId() != null ? p.getProductoId().toString() : "");
                negocioIdField.setValue(p.getNegocioId() != null ? p.getNegocioId().toString() : "");
                categoriaIdField.setValue(p.getCategoriaId() != null ? p.getCategoriaId().toString() : "");
                codigoProductoField.setValue(p.getCodigoProducto() != null ? p.getCodigoProducto() : "");
                nombreProductoField.setValue(p.getNombreProducto() != null ? p.getNombreProducto() : "");
                descripcionField.setValue(p.getDescripcion() != null ? p.getDescripcion() : "");
                precioVentaField.setValue(p.getPrecioVenta() != null ? p.getPrecioVenta().toString() : "");
                tipoItemField.setValue(p.getTipoItemId() != null ? p.getTipoItemId().toString() : "");
                manejaInventarioField.setValue(p.isManejaInventario() ? "1" : "0");
                activoField.setValue(p.isActivo() ? "1" : "0");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Productos p = new Productos(
                parseId(negocioIdField),
                parseId(categoriaIdField),
                codigoProductoField.getValue(),
                nombreProductoField.getValue(),
                descripcionField.getValue(),
                BigDecimal.valueOf(parseDouble(precioVentaField.getValue())),
                parseId(tipoItemField),
                parseInt(manejaInventarioField.getValue()) == 1,
                parseInt(activoField.getValue()) == 1
            );
            crudService.guardar(p);
            refrescar.run();
            productoIdField.setValue(p.getProductoId() != null ? p.getProductoId().toString() : "");
            Notification.show("Producto guardado con ID: " + p.getProductoId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Productos p = crudService.buscarPorId(Productos.class, parseId(productoIdField));
            if (p != null) {
                grid.asSingleSelect().setValue(p);
                Notification.show("Encontrado: " + p.getNombreProducto());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Productos p = crudService.buscarPorId(Productos.class, parseId(productoIdField));
            if (p != null) {
                p.setNegocioId(parseId(negocioIdField));
                p.setCategoriaId(parseId(categoriaIdField));
                p.setCodigoProducto(codigoProductoField.getValue());
                p.setNombreProducto(nombreProductoField.getValue());
                p.setDescripcion(descripcionField.getValue());
                p.setPrecioVenta(BigDecimal.valueOf(parseDouble(precioVentaField.getValue())));
                p.setTipoItemId(parseId(tipoItemField));
                p.setManejaInventario(parseInt(manejaInventarioField.getValue()) == 1);
                p.setActivo(parseInt(activoField.getValue()) == 1);
                crudService.actualizar(p);
                refrescar.run();
                Notification.show("Producto actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Productos.class, parseId(productoIdField));
            refrescar.run();
            limpiar(productoIdField, negocioIdField, categoriaIdField, codigoProductoField, nombreProductoField, descripcionField, precioVentaField, tipoItemField, manejaInventarioField, activoField);
            Notification.show("Producto eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(productoIdField, negocioIdField, categoriaIdField, codigoProductoField, nombreProductoField, descripcionField, precioVentaField, tipoItemField, manejaInventarioField, activoField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 2. CLIENTES
    // ==========================================
    private Component crearSeccionClientes() {
        VerticalLayout layout = new VerticalLayout();
        TextField clienteIdField = new TextField("ID");
        TextField negocioIdField = new TextField("Negocio ID");
        TextField tipoDocumentoField = new TextField("Tipo Documento");
        TextField numeroDocumentoField = new TextField("Número Documento");
        TextField nombreRazonSocialField = new TextField("Razón Social");
        TextField activoField = new TextField("Activo (1/0)");
        TextField esGenericoField = new TextField("Es Genérico (1/0)");

        FormLayout form = new FormLayout(clienteIdField, negocioIdField, tipoDocumentoField, numeroDocumentoField, nombreRazonSocialField, activoField, esGenericoField);

        Grid<Clientes> grid = new Grid<>(Clientes.class, false);
        grid.addColumn(Clientes::getClienteId).setHeader("Cliente").setAutoWidth(true);
        grid.addColumn(Clientes::getNegocioId).setHeader("Negocio").setAutoWidth(true);
        grid.addColumn(Clientes::getTipoDocumento).setHeader("Tipo Documento").setAutoWidth(true);
        grid.addColumn(Clientes::getNumeroDocumento).setHeader("Número Documento").setAutoWidth(true);
        grid.addColumn(Clientes::getNombreRazonSocial).setHeader("Razón Social").setAutoWidth(true);
        grid.addColumn(Clientes::getActivo).setHeader("Activo").setAutoWidth(true);
        grid.addColumn(Clientes::getEsGenerico).setHeader("Es Genérico").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Clientes.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Clientes c = e.getValue();
            if (c != null) {
                clienteIdField.setValue(String.valueOf(c.getClienteId()));
                negocioIdField.setValue(String.valueOf(c.getNegocioId()));
                tipoDocumentoField.setValue(c.getTipoDocumento() != null ? c.getTipoDocumento() : "");
                numeroDocumentoField.setValue(c.getNumeroDocumento() != null ? c.getNumeroDocumento() : "");
                nombreRazonSocialField.setValue(c.getNombreRazonSocial() != null ? c.getNombreRazonSocial() : "");
                activoField.setValue(c.getActivo() ? "1" : "0");
                esGenericoField.setValue(c.getEsGenerico() ? "1" : "0");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Clientes c = new Clientes(
                parseId(negocioIdField),
                tipoDocumentoField.getValue(),
                numeroDocumentoField.getValue(),
                nombreRazonSocialField.getValue(),
                parseInt(activoField.getValue()) == 1,
                parseInt(esGenericoField.getValue()) == 1
            );
            crudService.guardar(c);
            refrescar.run();
            clienteIdField.setValue(String.valueOf(c.getClienteId()));
            Notification.show("Cliente guardado con ID: " + c.getClienteId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Clientes c = crudService.buscarPorId(Clientes.class, parseId(clienteIdField));
            if (c != null) {
                grid.asSingleSelect().setValue(c);
                Notification.show("Encontrado: " + c.getNombreRazonSocial());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Clientes c = crudService.buscarPorId(Clientes.class, parseId(clienteIdField));
            if (c != null) {
                c.setNegocioId(parseId(negocioIdField));
                c.setTipoDocumento(tipoDocumentoField.getValue());
                c.setNumeroDocumento(numeroDocumentoField.getValue());
                c.setNombreRazonSocial(nombreRazonSocialField.getValue());
                c.setActivo(parseInt(activoField.getValue()) == 1);
                c.setEsGenerico(parseInt(esGenericoField.getValue()) == 1);
                crudService.actualizar(c);
                refrescar.run();
                Notification.show("Cliente actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Clientes.class, parseId(clienteIdField));
            refrescar.run();
            limpiar(clienteIdField, negocioIdField, tipoDocumentoField, numeroDocumentoField, nombreRazonSocialField, activoField, esGenericoField);
            Notification.show("Cliente eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(clienteIdField, negocioIdField, tipoDocumentoField, numeroDocumentoField, nombreRazonSocialField, activoField, esGenericoField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 3. DETALLES VENTA
    // ==========================================
    private Component crearSeccionDetallesVenta() {
        VerticalLayout layout = new VerticalLayout();
        TextField detalleVentaIdField = new TextField("ID");
        TextField ventaIdField = new TextField("Venta ID");
        TextField productoIdField = new TextField("Producto ID");
        TextField cantidadField = new TextField("Cantidad");
        TextField precioUnitarioField = new TextField("Precio Unitario");
        TextField descripcionProductoField = new TextField("Descripción Producto");
        TextField subtotalField = new TextField("Subtotal");

        FormLayout form = new FormLayout(detalleVentaIdField, ventaIdField, productoIdField, cantidadField, precioUnitarioField, descripcionProductoField, subtotalField);

        Grid<DetallesVenta> grid = new Grid<>(DetallesVenta.class, false);
        grid.addColumn(DetallesVenta::getDetalleVentaId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(DetallesVenta::getVentaId).setHeader("Venta ID").setAutoWidth(true);
        grid.addColumn(DetallesVenta::getProductoId).setHeader("Producto ID").setAutoWidth(true);
        grid.addColumn(DetallesVenta::getCantidad).setHeader("Cantidad").setAutoWidth(true);
        grid.addColumn(DetallesVenta::getPrecioUnitario).setHeader("Precio Unitario").setAutoWidth(true);
        grid.addColumn(DetallesVenta::getDescripcionProducto).setHeader("Descripción Producto").setAutoWidth(true);
        grid.addColumn(DetallesVenta::getSubtotal).setHeader("Subtotal").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(DetallesVenta.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            DetallesVenta d = e.getValue();
            if (d != null) {
                detalleVentaIdField.setValue(String.valueOf(d.getDetalleVentaId()));
                ventaIdField.setValue(String.valueOf(d.getVentaId()));
                productoIdField.setValue(String.valueOf(d.getProductoId()));
                cantidadField.setValue(String.valueOf(d.getCantidad()));
                precioUnitarioField.setValue(String.valueOf(d.getPrecioUnitario()));
                descripcionProductoField.setValue(d.getDescripcionProducto() != null ? d.getDescripcionProducto() : "");
                subtotalField.setValue(String.valueOf(d.getSubtotal()));
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            DetallesVenta d = new DetallesVenta();
            d.setVentaId(Long.parseLong(ventaIdField.getValue()));
            d.setProductoId(Long.parseLong(productoIdField.getValue()));
            d.setCantidad(Integer.parseInt(cantidadField.getValue()));
            d.setPrecioUnitario(new BigDecimal(precioUnitarioField.getValue()));
            d.setDescripcionProducto(descripcionProductoField.getValue());
            d.setSubtotal(new BigDecimal(subtotalField.getValue()));
            crudService.guardar(d);
            refrescar.run();
            detalleVentaIdField.setValue(String.valueOf(d.getDetalleVentaId()));
            Notification.show("Detalle de Venta guardado con ID: " + d.getDetalleVentaId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            DetallesVenta d = crudService.buscarPorId(DetallesVenta.class, parseId(detalleVentaIdField));
            if (d != null) {
                grid.asSingleSelect().setValue(d);
                Notification.show("Encontrado: " + d.getDescripcionProducto());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            DetallesVenta d = crudService.buscarPorId(DetallesVenta.class, parseId(detalleVentaIdField));
            if (d != null) {
                d.setVentaId(Long.parseLong(ventaIdField.getValue()));
                d.setProductoId(Long.parseLong(productoIdField.getValue()));
                d.setCantidad(Integer.parseInt(cantidadField.getValue()));
                d.setPrecioUnitario(new BigDecimal(precioUnitarioField.getValue()));
                d.setDescripcionProducto(descripcionProductoField.getValue());
                d.setSubtotal(new BigDecimal(subtotalField.getValue()));
                crudService.actualizar(d);
                refrescar.run();
                Notification.show("Detalle de Venta actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(DetallesVenta.class, parseId(detalleVentaIdField));
            refrescar.run();
            limpiar(detalleVentaIdField, ventaIdField, productoIdField, cantidadField, precioUnitarioField, descripcionProductoField, subtotalField);
            Notification.show("Detalle de Venta eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(detalleVentaIdField, ventaIdField, productoIdField, cantidadField, precioUnitarioField, descripcionProductoField, subtotalField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 4. VENTAS
    // ==========================================
    private Component crearSeccionVentas() {
        VerticalLayout layout = new VerticalLayout();
        TextField ventaIdField = new TextField("ID");
        TextField negocioIdField = new TextField("Negocio ID");
        TextField clienteIdField = new TextField("Cliente ID");
        TextField usuarioIdField = new TextField("Usuario ID");
        TextField estadoVentaField = new TextField("Estado Venta");
        TextField subtotalField = new TextField("Subtotal");
        TextField descuentoTotalField = new TextField("Descuento Total");
        TextField impuestoTotalField = new TextField("Impuesto Total");
        TextField totalVentaField = new TextField("Total Venta");
        TextField origenIdField = new TextField("Origen ID");

        FormLayout form = new FormLayout(ventaIdField, negocioIdField, clienteIdField, usuarioIdField, estadoVentaField, subtotalField, descuentoTotalField, impuestoTotalField, totalVentaField, origenIdField);

        Grid<Ventas> grid = new Grid<>(Ventas.class, false);
        grid.addColumn(Ventas::getVentaId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Ventas::getUuidLocal).setHeader("UUID Local").setAutoWidth(true);
        grid.addColumn(Ventas::getNegocioId).setHeader("Negocio ID").setAutoWidth(true);
        grid.addColumn(Ventas::getClienteId).setHeader("Cliente ID").setAutoWidth(true);
        grid.addColumn(Ventas::getUsuarioId).setHeader("Usuario ID").setAutoWidth(true);
        grid.addColumn(Ventas::getFechaVenta).setHeader("Fecha Venta").setAutoWidth(true);
        grid.addColumn(Ventas::getEstadoVenta).setHeader("Estado Venta").setAutoWidth(true);
        grid.addColumn(Ventas::getSubtotal).setHeader("Subtotal").setAutoWidth(true);
        grid.addColumn(Ventas::getDescuentoTotal).setHeader("Descuento Total").setAutoWidth(true);
        grid.addColumn(Ventas::getImpuestoTotal).setHeader("Impuesto Total").setAutoWidth(true);
        grid.addColumn(Ventas::getTotalVenta).setHeader("Total Venta").setAutoWidth(true);
        grid.addColumn(Ventas::getOrigenId).setHeader("Origen ID").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Ventas.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Ventas v = e.getValue();
            if (v != null) {
                ventaIdField.setValue(v.getVentaId() != null ? v.getVentaId().toString() : "");
                negocioIdField.setValue(v.getNegocioId() != null ? v.getNegocioId().toString() : "");
                clienteIdField.setValue(v.getClienteId() != null ? v.getClienteId().toString() : "");
                usuarioIdField.setValue(v.getUsuarioId() != null ? v.getUsuarioId().toString() : "");
                estadoVentaField.setValue(v.getEstadoVenta() != null ? v.getEstadoVenta() : "");
                subtotalField.setValue(v.getSubtotal() != null ? v.getSubtotal().toString() : "");
                descuentoTotalField.setValue(v.getDescuentoTotal() != null ? v.getDescuentoTotal().toString() : "");
                impuestoTotalField.setValue(v.getImpuestoTotal() != null ? v.getImpuestoTotal().toString() : "");
                totalVentaField.setValue(v.getTotalVenta() != null ? v.getTotalVenta().toString() : "");
                origenIdField.setValue(v.getOrigenId() != null ? v.getOrigenId().toString() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Ventas v = new Ventas();
            v.setUuidLocal(UUID.randomUUID());
            v.setFechaVenta(LocalDateTime.now());
            v.setNegocioId(Long.parseLong(negocioIdField.getValue()));
            v.setClienteId(Long.parseLong(clienteIdField.getValue()));
            v.setUsuarioId(Long.parseLong(usuarioIdField.getValue()));
            v.setEstadoVenta(estadoVentaField.getValue());
            v.setSubtotal(new BigDecimal(subtotalField.getValue()));
            v.setDescuentoTotal(new BigDecimal(descuentoTotalField.getValue()));
            v.setImpuestoTotal(new BigDecimal(impuestoTotalField.getValue()));
            v.setTotalVenta(new BigDecimal(totalVentaField.getValue()));
            v.setOrigenId(Long.parseLong(origenIdField.getValue()));
            crudService.guardar(v);
            refrescar.run();
            ventaIdField.setValue(v.getVentaId() != null ? v.getVentaId().toString() : "");
            Notification.show("Venta guardada con ID: " + v.getVentaId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Ventas v = crudService.buscarPorId(Ventas.class, parseId(ventaIdField));
            if (v != null) {
                grid.asSingleSelect().setValue(v);
                Notification.show("Encontrado: venta " + v.getVentaId());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Ventas v = crudService.buscarPorId(Ventas.class, parseId(ventaIdField));
            if (v != null) {
                v.setNegocioId(Long.parseLong(negocioIdField.getValue()));
                v.setClienteId(Long.parseLong(clienteIdField.getValue()));
                v.setUsuarioId(Long.parseLong(usuarioIdField.getValue()));
                v.setEstadoVenta(estadoVentaField.getValue());
                v.setSubtotal(new BigDecimal(subtotalField.getValue()));
                v.setDescuentoTotal(new BigDecimal(descuentoTotalField.getValue()));
                v.setImpuestoTotal(new BigDecimal(impuestoTotalField.getValue()));
                v.setTotalVenta(new BigDecimal(totalVentaField.getValue()));
                v.setOrigenId(Long.parseLong(origenIdField.getValue()));
                crudService.actualizar(v);
                refrescar.run();
                Notification.show("Venta actualizada");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Ventas.class, parseId(ventaIdField));
            refrescar.run();
            limpiar(ventaIdField, negocioIdField, clienteIdField, usuarioIdField, estadoVentaField, subtotalField, descuentoTotalField, impuestoTotalField, totalVentaField, origenIdField);
            Notification.show("Venta eliminada");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(ventaIdField, negocioIdField, clienteIdField, usuarioIdField, estadoVentaField, subtotalField, descuentoTotalField, impuestoTotalField, totalVentaField, origenIdField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    private Long parseId(TextField field) {
        try {
            return Long.parseLong(field.getValue().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer parseInt(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void limpiar(TextField... campos) {
        for (TextField c : campos) {
            c.clear();
        }
    }

    private HorizontalLayout crearAcciones(Button... botones) {
        HorizontalLayout layout = new HorizontalLayout(botones);
        layout.getStyle().set("flex-wrap", "wrap");
        return layout;
    }
}