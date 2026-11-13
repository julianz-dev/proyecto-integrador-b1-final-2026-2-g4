package com.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.io.ByteArrayInputStream;

import com.example.service.FacturaPdfService;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;

@PageTitle("Facturate - Sistema De Recaudo")
@Route("")
public class MainView extends VerticalLayout {

    private final CrudService crudService;
    private final FacturaPdfService facturaPdfService;

    public MainView(CrudService crudService, FacturaPdfService facturaPdfService) {
        this.crudService = crudService;
        this.facturaPdfService = facturaPdfService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        tabs.add("Productos", crearSeccionProductos());
        tabs.add("Clientes", crearSeccionClientes());
        tabs.add("Detalles por venta", crearSeccionDetallesVenta());
        tabs.add("Ventas", crearSeccionVentas());

        add(new H2("CRUD - Facturate"), tabs);
    }

    // ==========================================
    // 1. PRODUCTOS
    // ==========================================
    private Component crearSeccionProductos() {
        VerticalLayout layout = new VerticalLayout();
        TextField productoIdField = new TextField("Producto ID");
        productoIdField.setReadOnly(true);
        BigDecimalField negocioIdField = new BigDecimalField("Negocio ID");
        BigDecimalField categoriaIdField = new BigDecimalField("Categoría ID");
        TextField codigoProductoField = new TextField("Código Producto");
        TextField nombreProductoField = new TextField("Nombre Producto");
        TextArea descripcionField = new TextArea("Descripción");
        descripcionField.setMinRows(3);
        TextField precioVentaField = new TextField("Precio Venta");
        BigDecimalField tipoItemField = new BigDecimalField("Tipo Item ID");
        Select<Boolean> manejaInventarioField = new Select<>();
        manejaInventarioField.setLabel("Maneja Inventario");
        manejaInventarioField.setItems(true, false);
        manejaInventarioField.setItemLabelGenerator(valor -> valor ? "SI" : "NO");
        manejaInventarioField.setRequiredIndicatorVisible(true);
        Select<Boolean> activoField = new Select<>();
        activoField.setLabel("Activo");
        activoField.setItems(true, false);
        activoField.setItemLabelGenerator(valor -> valor ? "SI" : "NO");
        activoField.setRequiredIndicatorVisible(true);

        FormLayout form = new FormLayout(productoIdField, negocioIdField, categoriaIdField, codigoProductoField,
                nombreProductoField, descripcionField, precioVentaField, tipoItemField, manejaInventarioField,
                activoField);

        Grid<Productos> grid = new Grid<>(Productos.class, false);
        grid.addColumn(Productos::getProductoId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Productos::getNegocioId).setHeader("Negocio").setAutoWidth(true);
        grid.addColumn(Productos::getCategoriaId).setHeader("Categoría").setAutoWidth(true);
        grid.addColumn(Productos::getCodigoProducto).setHeader("Código").setAutoWidth(true);
        grid.addColumn(Productos::getNombreProducto).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Productos::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        grid.addColumn(Productos::getPrecioVenta).setHeader("Precio Venta").setAutoWidth(true);
        grid.addColumn(Productos::getTipoItemId).setHeader("Tipo Item").setAutoWidth(true);
        grid.addColumn(producto -> producto.isManejaInventario() ? "SI" : "NO")
                .setHeader("Maneja Inventario")
                .setAutoWidth(true);
        grid.addColumn(producto -> producto.isActivo() ? "SI" : "NO")
                .setHeader("Activo")
                .setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Productos.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Productos p = e.getValue();
            if (p != null) {
                productoIdField.setValue(p.getProductoId() != null ? p.getProductoId().toString() : "");
                negocioIdField.setValue(p.getNegocioId() != null ? BigDecimal.valueOf(p.getNegocioId()) : null);
                categoriaIdField.setValue(p.getCategoriaId() != null ? BigDecimal.valueOf(p.getCategoriaId()) : null);
                codigoProductoField.setValue(p.getCodigoProducto() != null ? p.getCodigoProducto() : "");
                nombreProductoField.setValue(p.getNombreProducto() != null ? p.getNombreProducto() : "");
                descripcionField.setValue(p.getDescripcion() != null ? p.getDescripcion() : "");
                precioVentaField.setValue(p.getPrecioVenta() != null ? p.getPrecioVenta().toString() : "");
                tipoItemField.setValue(p.getTipoItemId() != null ? BigDecimal.valueOf(p.getTipoItemId()) : null);
                manejaInventarioField.setValue(p.isManejaInventario());
                activoField.setValue(p.isActivo());
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            if (manejaInventarioField.getValue() == null || activoField.getValue() == null) {
                Notification.show("Selecciona SI o NO en los campos obligatorios.");
                return;
            }
            Productos p = new Productos(
                    parseId(negocioIdField),
                    parseId(categoriaIdField),
                    codigoProductoField.getValue(),
                    nombreProductoField.getValue(),
                    descripcionField.getValue(),
                    BigDecimal.valueOf(parseDouble(precioVentaField.getValue())),
                    parseId(tipoItemField),
                    manejaInventarioField.getValue(),
                    activoField.getValue());
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
            if (manejaInventarioField.getValue() == null || activoField.getValue() == null) {
                Notification.show("Selecciona SI o NO en los campos obligatorios.");
                return;
            }
            Productos p = crudService.buscarPorId(Productos.class, parseId(productoIdField));
            if (p != null) {
                p.setNegocioId(parseId(negocioIdField));
                p.setCategoriaId(parseId(categoriaIdField));
                p.setCodigoProducto(codigoProductoField.getValue());
                p.setNombreProducto(nombreProductoField.getValue());
                p.setDescripcion(descripcionField.getValue());
                p.setPrecioVenta(BigDecimal.valueOf(parseDouble(precioVentaField.getValue())));
                p.setTipoItemId(parseId(tipoItemField));
                p.setManejaInventario(manejaInventarioField.getValue());
                p.setActivo(activoField.getValue());
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
            limpiar(productoIdField, negocioIdField, categoriaIdField, codigoProductoField,
                    nombreProductoField, descripcionField, precioVentaField, tipoItemField);
            manejaInventarioField.clear();
            activoField.clear();
            grid.asSingleSelect().clear();
            Notification.show("Producto eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(productoIdField, negocioIdField, categoriaIdField, codigoProductoField,
                    nombreProductoField, descripcionField, precioVentaField, tipoItemField);
            manejaInventarioField.clear();
            activoField.clear();
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
        TextField clienteIdField = new TextField("Cliente ID");
        clienteIdField.setReadOnly(true);
        BigDecimalField negocioIdField = new BigDecimalField("Negocio ID");
        Select<String> tipoDocumentoField = new Select<>();
        tipoDocumentoField.setLabel("Tipo Documento");
        tipoDocumentoField.setItems("CC", "CE", "NIT", "PAS");
        tipoDocumentoField.setItemLabelGenerator(tipo -> switch (tipo) {
            case "CC" -> "Cédula de ciudadanía";
            case "CE" -> "Cédula de extranjería";
            case "NIT" -> "NIT";
            case "PAS" -> "Pasaporte";
            default -> tipo;
        });
        tipoDocumentoField.setRequiredIndicatorVisible(true);
        TextField numeroDocumentoField = new TextField("Número Documento");
        TextField nombreRazonSocialField = new TextField("Nombre o Razón Social");
        Select<Boolean> activoField = new Select<>();
        activoField.setLabel("Activo");
        activoField.setItems(true, false);
        activoField.setItemLabelGenerator(valor -> valor ? "SI" : "NO");
        activoField.setRequiredIndicatorVisible(true);
        Select<Boolean> esGenericoField = new Select<>();
        esGenericoField.setLabel("Es Genérico");
        esGenericoField.setItems(true, false);
        esGenericoField.setItemLabelGenerator(valor -> valor ? "SI" : "NO");
        esGenericoField.setRequiredIndicatorVisible(true);

        FormLayout form = new FormLayout(clienteIdField, negocioIdField, tipoDocumentoField, numeroDocumentoField,
                nombreRazonSocialField, activoField, esGenericoField);

        Grid<Clientes> grid = new Grid<>(Clientes.class, false);
        grid.addColumn(Clientes::getClienteId).setHeader("Cliente").setAutoWidth(true);
        grid.addColumn(Clientes::getNegocioId).setHeader("Negocio").setAutoWidth(true);
        grid.addColumn(Clientes::getTipoDocumento).setHeader("Tipo Documento").setAutoWidth(true);
        grid.addColumn(Clientes::getNumeroDocumento).setHeader("Número Documento").setAutoWidth(true);
        grid.addColumn(Clientes::getNombreRazonSocial).setHeader("Razón Social").setAutoWidth(true);
        grid.addColumn(cliente -> cliente.getActivo() ? "SI" : "NO")
                .setHeader("Activo")
                .setAutoWidth(true);
        grid.addColumn(cliente -> cliente.getEsGenerico() ? "SI" : "NO")
                .setHeader("Es Genérico")
                .setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Clientes.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Clientes c = e.getValue();
            if (c != null) {
                clienteIdField.setValue(String.valueOf(c.getClienteId()));
                negocioIdField.setValue(BigDecimal.valueOf(c.getNegocioId()));
                tipoDocumentoField.setValue(c.getTipoDocumento());
                numeroDocumentoField.setValue(c.getNumeroDocumento() != null ? c.getNumeroDocumento() : "");
                nombreRazonSocialField.setValue(c.getNombreRazonSocial() != null ? c.getNombreRazonSocial() : "");
                activoField.setValue(c.getActivo());
                esGenericoField.setValue(c.getEsGenerico());
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            if (activoField.getValue() == null || esGenericoField.getValue() == null) {
                Notification.show("Selecciona SI o NO en los campos obligatorios.");
                return;
            }
            Clientes c = new Clientes(
                    parseId(negocioIdField),
                    tipoDocumentoField.getValue(),
                    numeroDocumentoField.getValue(),
                    nombreRazonSocialField.getValue(),
                    activoField.getValue(),
                    esGenericoField.getValue());
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
            if (activoField.getValue() == null || esGenericoField.getValue() == null) {
                Notification.show("Selecciona SI o NO en los campos obligatorios.");
                return;
            }
            Clientes c = crudService.buscarPorId(Clientes.class, parseId(clienteIdField));
            if (c != null) {
                c.setNegocioId(parseId(negocioIdField));
                c.setTipoDocumento(tipoDocumentoField.getValue());
                c.setNumeroDocumento(numeroDocumentoField.getValue());
                c.setNombreRazonSocial(nombreRazonSocialField.getValue());
                c.setActivo(activoField.getValue());
                c.setEsGenerico(esGenericoField.getValue());
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
            limpiar(clienteIdField, negocioIdField, tipoDocumentoField,
                    numeroDocumentoField, nombreRazonSocialField);
            activoField.clear();
            esGenericoField.clear();

            Notification.show("Cliente eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(clienteIdField, negocioIdField, tipoDocumentoField,
                    numeroDocumentoField, nombreRazonSocialField);
            activoField.clear();
            esGenericoField.clear();
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
        TextField detalleVentaIdField = new TextField("Detalle ID");
        detalleVentaIdField.setReadOnly(true);
        BigDecimalField ventaIdField = new BigDecimalField("Venta ID");
        BigDecimalField productoIdField = new BigDecimalField("Producto ID");
        TextField cantidadField = new TextField("Cantidad");
        TextField precioUnitarioField = new TextField("Precio Unitario");
        TextField descripcionProductoField = new TextField("Descripción Producto");
        TextField subtotalField = new TextField("Subtotal");

        FormLayout form = new FormLayout(detalleVentaIdField, ventaIdField, productoIdField, cantidadField,
                precioUnitarioField, descripcionProductoField, subtotalField);

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
                ventaIdField.setValue(d.getVentaId() != null ? BigDecimal.valueOf(d.getVentaId()) : null);
                productoIdField.setValue(d.getProductoId() != null ? BigDecimal.valueOf(d.getProductoId()) : null);
                cantidadField.setValue(String.valueOf(d.getCantidad()));
                precioUnitarioField.setValue(String.valueOf(d.getPrecioUnitario()));
                descripcionProductoField.setValue(d.getDescripcionProducto() != null ? d.getDescripcionProducto() : "");
                subtotalField.setValue(String.valueOf(d.getSubtotal()));
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            DetallesVenta d = new DetallesVenta();
            d.setVentaId(parseId(ventaIdField));
            d.setProductoId(parseId(productoIdField));
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
                d.setVentaId(parseId(ventaIdField));
                d.setProductoId(parseId(productoIdField));
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
            limpiar(detalleVentaIdField, ventaIdField, productoIdField, cantidadField, precioUnitarioField,
                    descripcionProductoField, subtotalField);
            Notification.show("Detalle de Venta eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(detalleVentaIdField, ventaIdField, productoIdField, cantidadField, precioUnitarioField,
                    descripcionProductoField, subtotalField);
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
        TextField ventaIdField = new TextField("Venta ID");
        ventaIdField.setReadOnly(true);
        TextField uuidLocalField = new TextField("UUID Local");
        uuidLocalField.setReadOnly(true);
        BigDecimalField negocioIdField = new BigDecimalField("Negocio ID");
        BigDecimalField clienteIdField = new BigDecimalField("Cliente ID");
        BigDecimalField usuarioIdField = new BigDecimalField("Usuario ID");
        TextField fechaVentaField = new TextField("Fecha Venta");
        fechaVentaField.setReadOnly(true);
        Select<String> estadoVentaField = new Select<>();
        estadoVentaField.setLabel("Estado Venta");
        estadoVentaField.setItems("PAGADA", "PENDIENTE");
        estadoVentaField.setRequiredIndicatorVisible(true);
        BigDecimalField subtotalField = new BigDecimalField("Subtotal");
        BigDecimalField descuentoTotalField = new BigDecimalField("Descuento Total");
        BigDecimalField impuestoTotalField = new BigDecimalField("Impuesto Total");
        BigDecimalField totalVentaField = new BigDecimalField("Total Venta");
        BigDecimalField origenIdField = new BigDecimalField("Origen ID");

        FormLayout form = new FormLayout(ventaIdField, uuidLocalField, fechaVentaField,
                negocioIdField, clienteIdField, usuarioIdField, estadoVentaField,
                subtotalField, descuentoTotalField, impuestoTotalField, totalVentaField, origenIdField);

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
                uuidLocalField.setValue(v.getUuidLocal() != null ? v.getUuidLocal().toString() : "");
                negocioIdField.setValue(v.getNegocioId() != null ? BigDecimal.valueOf(v.getNegocioId()) : null);
                clienteIdField.setValue(v.getClienteId() != null ? BigDecimal.valueOf(v.getClienteId()) : null);
                usuarioIdField.setValue(v.getUsuarioId() != null ? BigDecimal.valueOf(v.getUsuarioId()) : null);
                fechaVentaField.setValue(v.getFechaVenta() != null ? v.getFechaVenta().toString() : "");
                estadoVentaField.setValue(v.getEstadoVenta());
                subtotalField.setValue(v.getSubtotal());
                descuentoTotalField.setValue(v.getDescuentoTotal());
                impuestoTotalField.setValue(v.getImpuestoTotal());
                totalVentaField.setValue(v.getTotalVenta());
                origenIdField.setValue(v.getOrigenId() != null ? BigDecimal.valueOf(v.getOrigenId()) : null);
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            if (estadoVentaField.getValue() == null
                    || subtotalField.getValue() == null
                    || descuentoTotalField.getValue() == null
                    || impuestoTotalField.getValue() == null
                    || totalVentaField.getValue() == null) {

                Notification.show("Completa el estado y todos los importes de la venta.");
                return;
            }
            Ventas v = new Ventas();
            v.setUuidLocal(UUID.randomUUID());
            v.setFechaVenta(LocalDateTime.now());
            v.setNegocioId(parseId(negocioIdField));
            v.setClienteId(parseId(clienteIdField));
            v.setUsuarioId(parseId(usuarioIdField));
            v.setEstadoVenta(estadoVentaField.getValue());
            v.setSubtotal(subtotalField.getValue());
            v.setDescuentoTotal(descuentoTotalField.getValue());
            v.setImpuestoTotal(impuestoTotalField.getValue());
            v.setTotalVenta(totalVentaField.getValue());
            v.setOrigenId(parseId(origenIdField));
            crudService.guardar(v);
            refrescar.run();
            uuidLocalField.setValue(v.getUuidLocal() != null ? v.getUuidLocal().toString() : "");
            fechaVentaField.setValue(v.getFechaVenta() != null ? v.getFechaVenta().toString() : "");
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
                v.setNegocioId(parseId(negocioIdField));
                v.setClienteId(parseId(clienteIdField));
                v.setUsuarioId(parseId(usuarioIdField));
                v.setEstadoVenta(estadoVentaField.getValue());
                v.setSubtotal(subtotalField.getValue());
                v.setDescuentoTotal(descuentoTotalField.getValue());
                v.setImpuestoTotal(impuestoTotalField.getValue());
                v.setTotalVenta(totalVentaField.getValue());
                v.setOrigenId(parseId(origenIdField));
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
            limpiar(ventaIdField, uuidLocalField, fechaVentaField, negocioIdField,
                    clienteIdField, usuarioIdField, estadoVentaField, subtotalField,
                    descuentoTotalField, impuestoTotalField, totalVentaField, origenIdField);
            grid.asSingleSelect().clear();
            Notification.show("Venta eliminada");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(ventaIdField, uuidLocalField, fechaVentaField, negocioIdField,
                    clienteIdField, usuarioIdField, estadoVentaField, subtotalField,
                    descuentoTotalField, impuestoTotalField, totalVentaField, origenIdField);
            grid.asSingleSelect().clear();
        });

        Anchor descargarFacturaLink = new Anchor(
                DownloadHandler.fromInputStream(event -> {

                    Long ventaId = parseId(ventaIdField);

                    if (ventaId == null) {
                        return DownloadResponse.error(400);
                    }

                    try {
                        byte[] pdf = facturaPdfService.generarFactura(ventaId);

                        return new DownloadResponse(
                                new ByteArrayInputStream(pdf),
                                "factura-" + ventaId + ".pdf",
                                "application/pdf",
                                pdf.length);

                    } catch (IllegalArgumentException ex) {
                        return DownloadResponse.error(404);

                    } catch (Exception ex) {
                        return DownloadResponse.error(500);
                    }

                }),
                "Descargar factura PDF");

        HorizontalLayout acciones = crearAcciones(
                btnCrear,
                btnConsultar,
                btnActualizar,
                btnEliminar,
                btnLimpiar);

        acciones.add(descargarFacturaLink);

        layout.add(form, acciones, grid);

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

    private Long parseId(BigDecimalField field) {
        try {
            return field.getValue() != null ? field.getValue().longValueExact() : null;
        } catch (ArithmeticException e) {
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

    private void limpiar(HasValue<?, ?>... campos) {
        for (HasValue<?, ?> campo : campos) {
            campo.clear();
        }
    }

    private HorizontalLayout crearAcciones(Button... botones) {
        HorizontalLayout layout = new HorizontalLayout(botones);
        layout.getStyle().set("flex-wrap", "wrap");
        return layout;
    }
}