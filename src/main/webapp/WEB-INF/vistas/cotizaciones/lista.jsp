<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="titulo" value="Cotizaciones" scope="request" />
<c:set var="seccion" value="cotizaciones" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<div class="cabecera-seccion">
    <div>
        <h1>Cotizaciones</h1>
        <p class="subtitulo">${fn:length(cotizaciones)} documento(s) en el listado</p>
    </div>
    <a class="boton principal" href="${ruta}/cotizaciones?accion=nueva">Nueva cotizaci&oacute;n</a>
</div>

<form class="barra-filtros" method="get" action="${ruta}/cotizaciones">
    <select name="estado">
        <option value="">Todos los estados</option>
        <option value="borrador"  ${estadoSeleccionado eq 'borrador'  ? 'selected' : ''}>Borrador</option>
        <option value="enviada"   ${estadoSeleccionado eq 'enviada'   ? 'selected' : ''}>Enviada</option>
        <option value="aprobada"  ${estadoSeleccionado eq 'aprobada'  ? 'selected' : ''}>Aprobada</option>
        <option value="rechazada" ${estadoSeleccionado eq 'rechazada' ? 'selected' : ''}>Rechazada</option>
    </select>
    <button type="submit" class="boton">Filtrar</button>
    <c:if test="${not empty estadoSeleccionado}">
        <a class="boton tenue" href="${ruta}/cotizaciones">Limpiar</a>
    </c:if>
</form>

<c:choose>
    <c:when test="${empty cotizaciones}">
        <p class="vacio">
            <%-- El mensaje cambia segun haya o no un filtro aplicado: decirle
                 "no coincide con el filtro" a quien no filtro nada confunde. --%>
            <c:choose>
                <c:when test="${not empty estadoSeleccionado}">
                    Ninguna cotizaci&oacute;n est&aacute; en estado
                    &laquo;<c:out value="${estadoSeleccionado}" />&raquo;.
                </c:when>
                <c:otherwise>
                    A&uacute;n no hay cotizaciones registradas.
                    <a href="${ruta}/cotizaciones?accion=nueva">Cree la primera</a>.
                </c:otherwise>
            </c:choose>
        </p>
    </c:when>

    <c:otherwise>
        <table class="tabla">
            <thead>
                <tr>
                    <th>N&uacute;mero</th>
                    <th>Cliente</th>
                    <th>Asesor</th>
                    <th>Fecha</th>
                    <th class="derecha">Total</th>
                    <th class="centro">Estado</th>
                    <th class="derecha">Detalle</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="cotizacion" items="${cotizaciones}">
                    <tr>
                        <td><code><c:out value="${cotizacion.numero}" /></code></td>
                        <td><strong><c:out value="${cotizacion.clienteNombre}" /></strong></td>
                        <td><c:out value="${cotizacion.usuarioNombre}" /></td>
                        <td>
                            <%-- fmt:formatDate da formato a la fecha en el
                                 servidor, sin construir la cadena a mano. --%>
                            <fmt:formatDate value="${cotizacion.fechaCreacion}"
                                            pattern="dd/MM/yyyy HH:mm" />
                        </td>
                        <td class="derecha">
                            <fmt:formatNumber value="${cotizacion.total}" type="currency"
                                              currencySymbol="$ " maxFractionDigits="0" />
                        </td>
                        <td class="centro">
                            <span class="estado ${cotizacion.estado}">${cotizacion.estado}</span>
                        </td>
                        <td class="derecha">
                            <a class="boton pequeno"
                               href="${ruta}/cotizaciones?accion=detalle&amp;id=${cotizacion.id}">Ver</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
