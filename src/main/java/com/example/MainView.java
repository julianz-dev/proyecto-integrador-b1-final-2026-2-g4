package com.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.swing.plaf.metal.MetalBorders.TextFieldBorder;

import com.example.model.Clientes;
import com.example.model.Productos;
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
        tabs.add("Clientes", crearSeccionCategorias());
        tabs.add("DetallesVenta", crearSeccionProveedores());
        tabs.add("Ventas", crearSeccionUsuarios());

        add(new H2("Sistema de Inventario - CRUD"), tabs);
    }

    // ==========================================
    // 1. PRODUCTOS
    // ==========================================
    private Component crearSeccionProductos() {
        VerticalLayout layout = new VerticalLayout();
        TextField productoIdField = new TextField("Producto ID");
        TextField negocioIdField = new TextField("Negocio ID");
        TextField categoriaIdField = new TextField("categoria ID");
        TextField codigoProductoField = new TextField("Código Producto");
        TextField nombreProductoField = new TextField("Nombre Producto");
        TextField descripcionField = new TextField("Descripción");
        TextField precioVentaField = new TextField("Precio Venta");
        TextField tipoItemField = new TextField("Tipo Item ID");
        TextField manejaInventarioField = new TextField("Maneja Inventario");
        TextField activoField = new TextField("Activo");

        FormLayout form = new FormLayout(productoIdField, negocioIdField, categoriaIdField, codigoProductoField, nombreProductoField, descripcionField, precioVentaField, tipoItemField, manejaInventarioField, activoField);

        Grid<Productos> grid = new Grid<>(Productos.class, false);
        grid.addColumn(Productos::getProductoId).setHeader("Id").setAutoWidth(true);
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
                negocioIdField.setValue(p.getNegocioId() != null ? p.getNegocioId() .toString() : "");
                categoriaIdField.setValue(p.getCategoriaId() != null ? p.getCategoriaId() .toString(): "");
                codigoProductoField.setValue(p.getCodigoProducto() != null ? p.getCodigoProducto() : "");
                nombreProductoField.setValue(p.getNombreProducto() != null ? p.getNombreProducto() : "");
                descripcionField.setValue(p.getDescripcion() != null ? p.getDescripcion() : "");
                precioVentaField.setValue(p.getPrecioVenta() != null ? p.getPrecioVenta().toString() : "");
                tipoItemField.setValue(p.getTipoItemId() != null ? p.getTipoItemId().toString() : "");
                manejaInventarioField.setValue(p.isManejaInventario() ? String.valueOf(p.isManejaInventario()) : "");
                activoField.setValue(p.isActivo() ? String.valueOf(p.isActivo()) : "");
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
            limpiar(productoIdField, codigoProductoField, nombreProductoField, categoriaIdField, precioVentaField, manejaInventarioField, activoField);
            Notification.show("Producto eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(productoIdField, codigoProductoField, nombreProductoField, categoriaIdField, precioVentaField, manejaInventarioField, activoField);
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
        TextField NegocioIdField = new TextField("Nombre");
        TextField TipoDocumentoField = new TextField("Descripción");
        TextField numeroDocumentoField = new TextField("Documento");
        TextField nombreRazonSocialField = new TextField("Razon social");
        TextField ActivoField = new TextField("Activo");
        TextField EsGenericoField = new TextField("Es generico");
        FormLayout form = new FormLayout(clienteIdField, NegocioIdField, TipoDocumentoField, TipoDocumentoField, numeroDocumentoField, nombreRazonSocialField, ActivoField);

        Grid<Clientes> grid = new Grid<>(Clientes.class, false);
        grid.addColumn(Clientes::getClienteId).setHeader("Cliente").setAutoWidth(true);
        grid.addColumn(Clientes::getNegocioId).setHeader("negocio").setAutoWidth(true);
        grid.addColumn(Clientes::getTipoDocumento).setHeader("Documento").setAutoWidth(true);
        grid.addColumn(Clientes::getNumeroDocumento).setHeader("Numero Documento").setAutoWidth(true);
        grid.addColumn(Clientes::getNombreRazonSocial).setHeader("Razon social").setAutoWidth(true);
        grid.addColumn(Clientes::getActivo).setHeader("Activo").setAutoWidth(true);
        grid.addColumn(Clientes::getEsGenerico).setHeader("Es generico").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Clientes.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Clientes c = e.getValue();
            if (c != null) {
                clienteIdField.setValue(c.getClienteId() != null ? c.getClienteId().toString() : "");
                NegocioIdField.setValue(c.getNegocioId() != null ? c.getNegocioId().toString() : "");
                TipoDocumentoField.setValue(c.getTipoDocumento() != null ? c.getDescripcion() : "");
                numeroDocumentoField.setValue(c.getNumeroDocumento() !=null ? c.getNumeroDocumento(): "");
                nombreRazonSocialField.setValue(c.getNombreRazonSocial() != null ? c.getNombreRazonSocial() : "");
                ActivoField.setValue(c.getActivo() ? String.valueOf (c.getActivo()) : "");
                EsGenericoField.setValue(c.getEsGenerico() ? String.valueOf(c.getEsGenerico()) : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Categoria c = new Categoria(nombreField.getValue(), descripcionField.getValue());
            crudService.guardar(c);
            refrescar.run();
            idField.setValue(c.getId() != null ? c.getId().toString() : "");
            Notification.show("Categoría guardada con ID: " + c.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Categoria c = crudService.buscarPorId(Categoria.class, parseId(idField));
            if (c != null) {
                grid.asSingleSelect().setValue(c);
                Notification.show("Encontrada: " + c.getNombre());
            } else {
                Notification.show("No encontrada");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Categoria c = crudService.buscarPorId(Categoria.class, parseId(idField));
            if (c != null) {
                c.setNombre(nombreField.getValue());
                c.setDescripcion(descripcionField.getValue());
                crudService.actualizar(c);
                refrescar.run();
                Notification.show("Categoría actualizada");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Categoria.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, descripcionField);
            Notification.show("Categoría eliminada");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, descripcionField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 3. PROVEEDORES
    // ==========================================
    private Component crearSeccionProveedores() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField contactoField = new TextField("Contacto");
        TextField telefonoField = new TextField("Teléfono");
        TextField emailField = new TextField("Email");
        TextField direccionField = new TextField("Dirección");

        FormLayout form = new FormLayout(idField, nombreField, contactoField, telefonoField, emailField, direccionField);

        Grid<Proveedor> grid = new Grid<>(Proveedor.class, false);
        grid.addColumn(Proveedor::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Proveedor::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Proveedor::getContacto).setHeader("Contacto").setAutoWidth(true);
        grid.addColumn(Proveedor::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Proveedor::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Proveedor::getDireccion).setHeader("Dirección").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Proveedor.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Proveedor p = e.getValue();
            if (p != null) {
                idField.setValue(p.getId() != null ? p.getId().toString() : "");
                nombreField.setValue(p.getNombre() != null ? p.getNombre() : "");
                contactoField.setValue(p.getContacto() != null ? p.getContacto() : "");
                telefonoField.setValue(p.getTelefono() != null ? p.getTelefono() : "");
                emailField.setValue(p.getEmail() != null ? p.getEmail() : "");
                direccionField.setValue(p.getDireccion() != null ? p.getDireccion() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Proveedor p = new Proveedor(
                nombreField.getValue(),
                contactoField.getValue(),
                telefonoField.getValue(),
                emailField.getValue(),
                direccionField.getValue()
            );
            crudService.guardar(p);
            refrescar.run();
            idField.setValue(p.getId() != null ? p.getId().toString() : "");
            Notification.show("Proveedor guardado con ID: " + p.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Proveedor p = crudService.buscarPorId(Proveedor.class, parseId(idField));
            if (p != null) {
                grid.asSingleSelect().setValue(p);
                Notification.show("Encontrado: " + p.getNombre());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Proveedor p = crudService.buscarPorId(Proveedor.class, parseId(idField));
            if (p != null) {
                p.setNombre(nombreField.getValue());
                p.setContacto(contactoField.getValue());
                p.setTelefono(telefonoField.getValue());
                p.setEmail(emailField.getValue());
                p.setDireccion(direccionField.getValue());
                crudService.actualizar(p);
                refrescar.run();
                Notification.show("Proveedor actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Proveedor.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, contactoField, telefonoField, emailField, direccionField);
            Notification.show("Proveedor eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, contactoField, telefonoField, emailField, direccionField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 4. USUARIOS
    // ==========================================
    private Component crearSeccionUsuarios() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField apellidoField = new TextField("Apellido");
        TextField emailField = new TextField("Email");
        TextField telefonoField = new TextField("Teléfono");
        TextField direccionField = new TextField("Dirección");
        TextField rolField = new TextField("Rol");
        TextField estadoField = new TextField("Estado");

        FormLayout form = new FormLayout(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);

        Grid<Usuario> grid = new Grid<>(Usuario.class, false);
        grid.addColumn(Usuario::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Usuario::getApellido).setHeader("Apellido").setAutoWidth(true);
        grid.addColumn(Usuario::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Usuario::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Usuario::getRol).setHeader("Rol").setAutoWidth(true);
        grid.addColumn(Usuario::getEstado).setHeader("Estado").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Usuario.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Usuario u = e.getValue();
            if (u != null) {
                idField.setValue(u.getId() != null ? u.getId().toString() : "");
                nombreField.setValue(u.getNombre() != null ? u.getNombre() : "");
                apellidoField.setValue(u.getApellido() != null ? u.getApellido() : "");
                emailField.setValue(u.getEmail() != null ? u.getEmail() : "");
                telefonoField.setValue(u.getTelefono() != null ? u.getTelefono() : "");
                direccionField.setValue(u.getDireccion() != null ? u.getDireccion() : "");
                rolField.setValue(u.getRol() != null ? u.getRol() : "");
                estadoField.setValue(u.getEstado() != null ? u.getEstado() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Usuario u = new Usuario(
                nombreField.getValue(),
                apellidoField.getValue(),
                emailField.getValue(),
                telefonoField.getValue(),
                direccionField.getValue(),
                rolField.getValue(),
                estadoField.getValue(),
                LocalDateTime.now()
            );
            crudService.guardar(u);
            refrescar.run();
            idField.setValue(u.getId() != null ? u.getId().toString() : "");
            Notification.show("Usuario guardado con ID: " + u.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Usuario u = crudService.buscarPorId(Usuario.class, parseId(idField));
            if (u != null) {
                grid.asSingleSelect().setValue(u);
                Notification.show("Encontrado: " + u.getNombre() + " " + u.getApellido());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Usuario u = crudService.buscarPorId(Usuario.class, parseId(idField));
            if (u != null) {
                u.setNombre(nombreField.getValue());
                u.setApellido(apellidoField.getValue());
                u.setEmail(emailField.getValue());
                u.setTelefono(telefonoField.getValue());
                u.setDireccion(direccionField.getValue());
                u.setRol(rolField.getValue());
                u.setEstado(estadoField.getValue());
                crudService.actualizar(u);
                refrescar.run();
                Notification.show("Usuario actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Usuario.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);
            Notification.show("Usuario eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);
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
