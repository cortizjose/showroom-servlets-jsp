<%-- Listado de clientes. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="titulo" value="Clientes" scope="request" />
<c:set var="seccion" value="clientes" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<div class="cabecera-seccion">
    <div>
        <h1>Clientes</h1>
        <p class="subtitulo">${fn:length(clientes)} cliente(s) en el listado</p>
    </div>
    <a class="boton principal" href="${ruta}/clientes?accion=nuevo">Nuevo cliente</a>
</div>

<%-- Formulario de busqueda: GET. --%>
<form class="barra-filtros" method="get" action="${ruta}/clientes">
    <input type="text" name="busqueda" value="<c:out value='${busqueda}' />"
           placeholder="Buscar por nombre o documento" maxlength="60">
    <button type="submit" class="boton">Buscar</button>
    <c:if test="${not empty busqueda}">
        <a class="boton tenue" href="${ruta}/clientes">Limpiar</a>
    </c:if>
</form>

<c:choose>
    <c:when test="${empty clientes}">
        <p class="vacio">
            <c:choose>
                <c:when test="${not empty busqueda}">
                    Ning&uacute;n cliente coincide con &laquo;<c:out value="${busqueda}" />&raquo;.
                </c:when>
                <c:otherwise>
                    A&uacute;n no hay clientes registrados.
                </c:otherwise>
            </c:choose>
        </p>
    </c:when>

    <c:otherwise>
        <table class="tabla">
            <thead>
                <tr>
                    <th>Documento</th>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Tel&eacute;fono</th>
                    <th>Ciudad</th>
                    <th>Estado</th>
                    <th class="derecha">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <%-- c:forEach recorre la lista que dejo el servlet. --%>
                <c:forEach var="cliente" items="${clientes}" varStatus="fila">
                    <tr class="${cliente.activo ? '' : 'inactivo'}">
                        <td><code><c:out value="${cliente.documento}" /></code></td>
                        <td><strong><c:out value="${cliente.nombre}" /></strong></td>
                        <td><c:out value="${cliente.correo}" /></td>
                        <td><c:out value="${cliente.telefono}" /></td>
                        <td><c:out value="${cliente.ciudad}" /></td>
                        <td>
                            <c:choose>
                                <c:when test="${cliente.activo}">
                                    <span class="estado activo">Activo</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="estado neutro">Inactivo</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="derecha acciones">
                            <a class="boton pequeno" href="${ruta}/clientes?accion=editar&amp;id=${cliente.id}">
                                Editar
                            </a>

                            <c:if test="${cliente.activo}">
                                <%-- POST para una operacion que modifica datos. --%>
                                <form method="post" action="${ruta}/clientes" class="en-linea"
                                      onsubmit="return confirm('Desea desactivar este cliente?');">
                                    <input type="hidden" name="accion" value="desactivar">
                                    <input type="hidden" name="id" value="${cliente.id}">
                                    <button type="submit" class="boton pequeno peligro">Desactivar</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <p class="nota">
            Las bajas son l&oacute;gicas: un cliente inactivo no admite
            cotizaciones nuevas, pero sus documentos anteriores siguen consultables.
        </p>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
