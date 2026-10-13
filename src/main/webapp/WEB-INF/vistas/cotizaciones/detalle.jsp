<%-- Detalle de una cotizacion, con sus lineas y sus totales. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="titulo" value="Cotizacion ${cotizacion.numero}" scope="request" />
<c:set var="seccion" value="cotizaciones" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<div class="cabecera-seccion">
    <div>
        <h1>Cotizaci&oacute;n <c:out value="${cotizacion.numero}" /></h1>
        <p class="subtitulo">
            <fmt:formatDate value="${cotizacion.fechaCreacion}" pattern="dd 'de' MMMM 'de' yyyy, HH:mm" />
            &middot; Elaborada por <c:out value="${cotizacion.usuarioNombre}" />
        </p>
    </div>
    <a class="boton tenue" href="${ruta}/cotizaciones">Volver al listado</a>
</div>

<div class="panel">
    <div class="rejilla-dos">
        <div>
            <h2>Cliente</h2>
            <p class="dato-grande"><c:out value="${cotizacion.clienteNombre}" /></p>
        </div>
        <div>
            <h2>Estado</h2>
            <p>
                <span class="estado ${cotizacion.estado} grande">${cotizacion.estado}</span>
            </p>
        </div>
    </div>

    <c:if test="${not empty cotizacion.observaciones}">
        <h2>Observaciones</h2>
        <p><c:out value="${cotizacion.observaciones}" /></p>
    </c:if>
</div>

<table class="tabla">
    <thead>
        <tr>
            <th class="centro">#</th>
            <th>C&oacute;digo</th>
            <th>Producto</th>
            <th class="centro">Cantidad</th>
            <th class="derecha">Precio unitario</th>
            <th class="derecha">Subtotal</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="detalle" items="${cotizacion.detalles}" varStatus="fila">
            <tr>
                <td class="centro">${fila.count}</td>
                <td><code><c:out value="${detalle.productoCodigo}" /></code></td>
                <td><c:out value="${detalle.productoNombre}" /></td>
                <td class="centro">${detalle.cantidad}</td>
                <td class="derecha">
                    <fmt:formatNumber value="${detalle.precioUnitario}" type="currency"
                                      currencySymbol="$ " maxFractionDigits="0" />
                </td>
                <td class="derecha">
                    <fmt:formatNumber value="${detalle.subtotal}" type="currency"
                                      currencySymbol="$ " maxFractionDigits="0" />
                </td>
            </tr>
        </c:forEach>
    </tbody>
    <tfoot>
        <tr>
            <td colspan="5" class="derecha">Subtotal</td>
            <td class="derecha">
                <fmt:formatNumber value="${cotizacion.subtotal}" type="currency"
                                  currencySymbol="$ " maxFractionDigits="0" />
            </td>
        </tr>
        <tr>
            <td colspan="5" class="derecha">IVA (19 %)</td>
            <td class="derecha">
                <fmt:formatNumber value="${cotizacion.iva}" type="currency"
                                  currencySymbol="$ " maxFractionDigits="0" />
            </td>
        </tr>
        <tr class="total">
            <td colspan="5" class="derecha"><strong>Total</strong></td>
            <td class="derecha">
                <strong>
                    <fmt:formatNumber value="${cotizacion.total}" type="currency"
                                      currencySymbol="$ " maxFractionDigits="0" />
                </strong>
            </td>
        </tr>
    </tfoot>
</table>

<%-- Cambios de estado: cada boton es un formulario POST independiente. --%>
<c:if test="${cotizacion.estado eq 'borrador' or cotizacion.estado eq 'enviada'}">
    <div class="panel acciones-estado">
        <h2>Cambiar estado</h2>

        <c:if test="${cotizacion.estado eq 'borrador'}">
            <form method="post" action="${ruta}/cotizaciones" class="en-linea">
                <input type="hidden" name="accion" value="estado">
                <input type="hidden" name="id" value="${cotizacion.id}">
                <input type="hidden" name="nuevoEstado" value="enviada">
                <button type="submit" class="boton principal">Enviar al cliente</button>
            </form>
            <span class="nota">
                Al enviarla, la cotizaci&oacute;n queda en firme y el cliente puede aprobarla
                o rechazarla.
            </span>
        </c:if>

        <c:if test="${cotizacion.estado eq 'enviada'}">
            <form method="post" action="${ruta}/cotizaciones" class="en-linea">
                <input type="hidden" name="accion" value="estado">
                <input type="hidden" name="id" value="${cotizacion.id}">
                <input type="hidden" name="nuevoEstado" value="aprobada">
                <button type="submit" class="boton principal">Marcar como aprobada</button>
            </form>

            <form method="post" action="${ruta}/cotizaciones" class="en-linea">
                <input type="hidden" name="accion" value="estado">
                <input type="hidden" name="id" value="${cotizacion.id}">
                <input type="hidden" name="nuevoEstado" value="rechazada">
                <button type="submit" class="boton peligro">Marcar como rechazada</button>
            </form>
        </c:if>
    </div>
</c:if>

<c:if test="${cotizacion.estado eq 'aprobada' or cotizacion.estado eq 'rechazada'}">
    <p class="nota">
        Esta cotizaci&oacute;n est&aacute; en un estado final y ya no admite cambios de
        estado.
    </p>
</c:if>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
