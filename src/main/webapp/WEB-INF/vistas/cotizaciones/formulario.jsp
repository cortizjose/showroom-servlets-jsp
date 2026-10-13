<%-- Formulario de nueva cotizacion. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="titulo" value="Nueva cotizacion" scope="request" />
<c:set var="seccion" value="cotizaciones" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<div class="cabecera-seccion">
    <div>
        <h1>Nueva cotizaci&oacute;n</h1>
        <p class="subtitulo">
            Seleccione el cliente e indique las cantidades. El IVA y el total los
            calcula el servidor.
        </p>
    </div>
    <a class="boton tenue" href="${ruta}/cotizaciones">Volver al listado</a>
</div>

<c:if test="${not empty errores}">
    <div class="aviso error">
        <strong>No se pudo generar la cotizaci&oacute;n:</strong>
        <ul>
            <c:forEach var="entrada" items="${errores}">
                <li><c:out value="${entrada.value}" /></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<c:choose>
    <c:when test="${empty clientes}">
        <p class="aviso error">
            No hay clientes activos. <a href="${ruta}/clientes?accion=nuevo">Registre un cliente</a>
            antes de cotizar.
        </p>
    </c:when>
    <c:when test="${empty productos}">
        <p class="aviso error">
            No hay productos disponibles con existencias para cotizar.
        </p>
    </c:when>

    <c:otherwise>
        <form method="post" action="${ruta}/cotizaciones" class="formulario">

            <div class="rejilla-dos">
                <div class="campo ${not empty errores['cliente'] ? 'con-error' : ''}">
                    <label for="clienteId">Cliente *</label>
                    <select id="clienteId" name="clienteId" required>
                        <option value="">Seleccione el cliente...</option>
                        <c:forEach var="cliente" items="${clientes}">
                            <option value="${cliente.id}"
                                    ${clienteSeleccionado eq cliente.id ? 'selected' : ''}>
                                <c:out value="${cliente.nombre}" /> &mdash; <c:out value="${cliente.documento}" />
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty errores['cliente']}">
                        <span class="mensaje-error">${errores['cliente']}</span>
                    </c:if>
                </div>

                <div class="campo">
                    <label for="observaciones">Observaciones</label>
                    <input type="text" id="observaciones" name="observaciones"
                           value="<c:out value='${observaciones}' />" maxlength="400"
                           placeholder="Condiciones de entrega, descuentos acordados...">
                </div>
            </div>

            <h2 class="titulo-bloque">Productos</h2>
            <p class="nota">
                Escriba la cantidad solo en los productos que desea cotizar. Las filas
                en cero se ignoran.
            </p>

            <table class="tabla">
                <thead>
                    <tr>
                        <th>C&oacute;digo</th>
                        <th>Producto</th>
                        <th class="derecha">Precio unitario</th>
                        <th class="centro">Disponible</th>
                        <th class="centro">Cantidad</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="producto" items="${productos}">
                        <tr>
                            <td><code><c:out value="${producto.codigo}" /></code></td>
                            <td><c:out value="${producto.nombre}" /></td>
                            <td class="derecha">
                                <fmt:formatNumber value="${producto.precio}" type="currency"
                                                  currencySymbol="$ " maxFractionDigits="0" />
                            </td>
                            <td class="centro">${producto.stock}</td>
                            <td class="centro">
                                <%-- Los dos campos con el mismo nombre en cada fila: asi se forman los arreglos paralelos. --%>
                                <input type="hidden" name="productoId" value="${producto.id}">
                                <input type="number" name="cantidad" value="0"
                                       min="0" max="${producto.stock}" class="cantidad">
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="acciones-formulario">
                <button type="submit" class="boton principal">Generar cotizaci&oacute;n</button>
                <a class="boton tenue" href="${ruta}/cotizaciones">Cancelar</a>
            </div>

            <p class="nota">
                La cotizaci&oacute;n se crea en estado <strong>borrador</strong> y tiene una
                vigencia de 15 d&iacute;as. El IVA aplicado es del 19 %.
            </p>
        </form>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
