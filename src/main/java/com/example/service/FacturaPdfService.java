
package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Clientes;
import com.example.model.DetallesVenta;
import com.example.model.Ventas;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
@Transactional(readOnly = true)
public class FacturaPdfService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Locale LOCALE_CO = Locale.forLanguageTag("es-CO");

    /**
     * Genera el comprobante PDF correspondiente a una venta.
     */
    public byte[] generarFactura(Long ventaId) throws IOException {

        if (ventaId == null) {
            throw new IllegalArgumentException("El ID de la venta es obligatorio.");
        }

        // Buscar la venta.
        Ventas venta = entityManager.find(Ventas.class, ventaId);

        if (venta == null) {
            throw new IllegalArgumentException("No existe una venta con ID: " + ventaId);
        }

        // Buscar el cliente asociado.
        Clientes cliente = venta.getClienteId() != null
                ? entityManager.find(Clientes.class, venta.getClienteId())
                : null;

        // Buscar los detalles asociados a la venta.
        List<DetallesVenta> detalles = entityManager.createQuery(
                "SELECT d FROM DetallesVenta d WHERE d.ventaId = :ventaId ORDER BY d.detalleVentaId",
                DetallesVenta.class)
                .setParameter("ventaId", ventaId)
                .getResultList();

        // Construir el documento HTML.
        String html = construirHtml(venta, cliente, detalles);

        // Convertir HTML a PDF.
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFastMode();
        builder.useDefaultPageSize(
                8.5f,
                13f,
                PdfRendererBuilder.PageSizeUnits.INCHES);
        builder.withHtmlContent(html, "");
        builder.toStream(output);
        builder.run();

        return output.toByteArray();
    }

    /**
     * Construye la plantilla HTML del comprobante.
     */
    private String construirHtml(
            Ventas venta,
            Clientes cliente,
            List<DetallesVenta> detalles) {

        StringBuilder html = new StringBuilder();

        String nombreCliente = cliente != null
                ? cliente.getNombreRazonSocial()
                : "Cliente no disponible";

        String documentoCliente = cliente != null
                ? valor(cliente.getTipoDocumento()) + " " + valor(cliente.getNumeroDocumento())
                : "No disponible";

        String fechaVenta = venta.getFechaVenta() != null
                ? venta.getFechaVenta().format(FORMATO_FECHA)
                : "No registrada";

        html.append("""
                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml" lang="es">
                <head>
                    <meta charset="UTF-8" />
                    <title>Comprobante de venta</title>

                    <style type="text/css">
                        @page {
                            size: 8.5in 13in;
                            margin: 1.2cm;
                        }

                        body {
                            font-family: Arial, sans-serif;
                            color: #333333;
                            font-size: 11px;
                        }

                        .encabezado {
                            text-align: center;
                            border-bottom: 2px solid #245580;
                            padding-bottom: 15px;
                        }

                        .encabezado h1 {
                            color: #245580;
                            font-size: 25px;
                            margin-bottom: 5px;
                        }

                        .seccion {
                            margin-top: 22px;
                        }

                        .seccion h2 {
                            color: #245580;
                            font-size: 14px;
                            border-bottom: 1px solid #cccccc;
                            padding-bottom: 5px;
                        }

                        .datos {
                            width: 100%;
                            border-collapse: collapse;
                        }

                        .datos td {
                            padding: 6px;
                        }

                        .productos {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 12px;
                        }

                        .productos th {
                            background-color: #245580;
                            color: white;
                            padding: 9px;
                            text-align: left;
                        }

                        .productos td {
                            border-bottom: 1px solid #dddddd;
                            padding: 8px;
                        }

                        .numero {
                            text-align: right;
                        }

                        .totales {
                            width: 45%;
                            margin-left: auto;
                            margin-top: 18px;
                            border-collapse: collapse;
                        }

                        .totales td {
                            padding: 7px;
                        }

                        .total-final {
                            color: #245580;
                            font-weight: bold;
                            font-size: 15px;
                            border-top: 2px solid #245580;
                        }

                        .pie {
                            text-align: center;
                            margin-top: 40px;
                            color: #777777;
                            font-size: 9px;
                        }
                    </style>
                </head>

                <body>

                    <div class="encabezado">
                        <h1>FACTURATE</h1>
                        <h2>COMPROBANTE DE VENTA</h2>
                    </div>
                """);

        html.append("""
                <div class="seccion">
                    <h2>Información de la venta</h2>

                    <table class="datos">
                """);

        html.append("<tr><td><b>Número de venta:</b></td><td>")
                .append(venta.getVentaId())
                .append("</td></tr>");

        html.append("<tr><td><b>UUID:</b></td><td>")
                .append(valor(venta.getUuidLocal() != null
                        ? venta.getUuidLocal().toString()
                        : null))
                .append("</td></tr>");

        html.append("<tr><td><b>Fecha:</b></td><td>")
                .append(escapeHtml(fechaVenta))
                .append("</td></tr>");

        html.append("<tr><td><b>Estado:</b></td><td>")
                .append(escapeHtml(valor(venta.getEstadoVenta())))
                .append("</td></tr>");

        html.append("""
                    </table>
                </div>

                <div class="seccion">
                    <h2>Información del cliente</h2>

                    <table class="datos">
                """);

        html.append("<tr><td><b>Nombre / Razón social:</b></td><td>")
                .append(escapeHtml(nombreCliente))
                .append("</td></tr>");

        html.append("<tr><td><b>Documento:</b></td><td>")
                .append(escapeHtml(documentoCliente))
                .append("</td></tr>");

        html.append("""
                    </table>
                </div>

                <div class="seccion">
                    <h2>Detalle de productos</h2>

                    <table class="productos">
                        <thead>
                            <tr>
                                <th>Producto</th>
                                <th class="numero">Cantidad</th>
                                <th class="numero">Precio unitario</th>
                                <th class="numero">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                """);

        if (detalles.isEmpty()) {
            html.append("""
                    <tr>
                        <td colspan="4">No hay detalles asociados a esta venta.</td>
                    </tr>
                    """);
        } else {

            for (DetallesVenta detalle : detalles) {

                html.append("<tr><td>")
                        .append(escapeHtml(valor(detalle.getDescripcionProducto())))
                        .append("</td><td class=\"numero\">")
                        .append(detalle.getCantidad() != null
                                ? detalle.getCantidad()
                                : 0)
                        .append("</td><td class=\"numero\">")
                        .append(formatearDinero(detalle.getPrecioUnitario()))
                        .append("</td><td class=\"numero\">")
                        .append(formatearDinero(detalle.getSubtotal()))
                        .append("</td></tr>");
            }
        }

        html.append("""
                        </tbody>
                    </table>
                </div>

                <div class="seccion">
                    <table class="totales">
                """);

        html.append("<tr><td>Subtotal:</td><td class=\"numero\">")
                .append(formatearDinero(venta.getSubtotal()))
                .append("</td></tr>");

        html.append("<tr><td>Descuento:</td><td class=\"numero\">")
                .append(formatearDinero(venta.getDescuentoTotal()))
                .append("</td></tr>");

        html.append("<tr><td>Impuestos:</td><td class=\"numero\">")
                .append(formatearDinero(venta.getImpuestoTotal()))
                .append("</td></tr>");

        html.append("<tr class=\"total-final\"><td>TOTAL:</td><td class=\"numero\">")
                .append(formatearDinero(venta.getTotalVenta()))
                .append("</td></tr>");

        html.append(
                """
                            </table>
                        </div>

                        <div class="pie">
                            <p>Documento generado por Facturate.</p>
                            <p>Comprobante de demostración académica. No constituye una factura electrónica validada por la DIAN.</p>
                        </div>

                        </body>
                        </html>
                        """);

        return html.toString();
    }

    /**
     * Formatea los valores monetarios en pesos colombianos.
     */
    private String formatearDinero(BigDecimal valor) {

        NumberFormat formato = NumberFormat.getCurrencyInstance(LOCALE_CO);

        formato.setCurrency(Currency.getInstance("COP"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);

        return formato.format(valor != null ? valor : BigDecimal.ZERO);
    }

    /**
     * Devuelve un texto alternativo cuando el valor es nulo.
     */
    private String valor(String texto) {
        return texto != null ? texto : "No disponible";
    }

    /**
     * Evita que caracteres especiales rompan la estructura HTML.
     */
    private String escapeHtml(String texto) {

        if (texto == null) {
            return "";
        }

        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
