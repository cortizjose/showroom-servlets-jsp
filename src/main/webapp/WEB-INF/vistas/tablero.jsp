<%-- Tablero de indicadores. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="titulo" value="Tablero" scope="request" />
<c:set var="seccion" value="tablero" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<div class="cabecera-seccion">
    <div>
        <h1>Tablero</h1>
        <p class="subtitulo">
            Bienvenido, <c:out value="${sessionScope.usuario.nombre}" />. Estado actual del m&oacute;dulo comercial.
        </p>
    </div>
</div>

<c:if test="${not baseDatosActiva}">
    <p class="aviso error">
        La base de datos no responde. Verifique que MySQL est&eacute; iniciado
        en el panel de control de XAMPP.
    </p>
</c:if>

<div class="indicadores">

    <div class="indicador">
        <span class="etiqueta">Clientes activos</span>
        <span class="valor">${totalClientes}</span>
        <a href="${pageContext.request.contextPath}/clientes">Ver clientes</a>
    </div>

    <div class="indicador">
        <span class="etiqueta">Productos en cat&aacute;logo</span>
        <span class="valor">${totalProductos}</span>
        <a href="${pageContext.request.contextPath}/productos">Ver cat&aacute;logo</a>
    </div>

    <div class="indicador ${productosStockBajo > 0 ? 'alerta' : ''}">
        <span class="etiqueta">Productos por reponer</span>
        <span class="valor">${productosStockBajo}</span>
        <a href="${pageContext.request.contextPath}/productos">Revisar existencias</a>
    </div>

    <div class="indicador">
        <span class="etiqueta">Cotizaciones emitidas</span>
        <span class="valor">${totalCotizaciones}</span>
        <a href="${pageContext.request.contextPath}/cotizaciones">Ver cotizaciones</a>
    </div>

    <div class="indicador ancho-doble">
        <span class="etiqueta">Monto aprobado</span>
        <span class="valor">
            <%-- fmt:formatNumber da formato de moneda del lado del servidor. --%>
            <fmt:formatNumber value="${montoAprobado}" type="currency"
                              currencySymbol="$ " maxFractionDigits="0" />
        </span>
        <span class="nota">Suma de las cotizaciones en estado aprobada</span>
    </div>

</div>

<div class="panel">
    <h2>Qu&eacute; hace este m&oacute;dulo</h2>
    <p>
        Administra la operaci&oacute;n comercial del showroom: los clientes que lo
        visitan, el cat&aacute;logo que se exhibe y las cotizaciones que se les
        entregan. Est&aacute; construido con <strong>Java Servlets</strong> para la
        l&oacute;gica y <strong>JSP</strong> para las vistas, sobre Apache Tomcat y
        MySQL.
    </p>

    <table class="tabla compacta">
        <thead>
            <tr>
                <th>Ruta</th>
                <th>Servlet</th>
                <th>M&eacute;todos</th>
                <th>Funci&oacute;n</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><code>/login</code></td>
                <td>LoginServlet</td>
                <td><span class="verbo get">GET</span> <span class="verbo post">POST</span></td>
                <td>Muestra el formulario y autentica al usuario</td>
            </tr>
            <tr>
                <td><code>/tablero</code></td>
                <td>TableroServlet</td>
                <td><span class="verbo get">GET</span></td>
                <td>Indicadores del m&oacute;dulo</td>
            </tr>
            <tr>
                <td><code>/clientes</code></td>
                <td>ClienteServlet</td>
                <td><span class="verbo get">GET</span> <span class="verbo post">POST</span></td>
                <td>Listar, crear, editar y desactivar clientes</td>
            </tr>
            <tr>
                <td><code>/productos</code></td>
                <td>ProductoServlet</td>
                <td><span class="verbo get">GET</span> <span class="verbo post">POST</span></td>
                <td>Cat&aacute;logo; la escritura exige rol administrador</td>
            </tr>
            <tr>
                <td><code>/cotizaciones</code></td>
                <td>CotizacionServlet</td>
                <td><span class="verbo get">GET</span> <span class="verbo post">POST</span></td>
                <td>Cotizaciones con varias l&iacute;neas y c&aacute;lculo de IVA</td>
            </tr>
            <tr>
                <td><code>/cerrar-sesion</code></td>
                <td>CerrarSesionServlet</td>
                <td><span class="verbo get">GET</span></td>
                <td>Invalida la sesi&oacute;n</td>
            </tr>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
