<%--
  Catalogo de productos.

  El formulario de filtros combina dos parametros (texto y categoria) en una
  sola peticion GET, y la vista recuerda lo seleccionado leyendo los atributos
  que dejo el servlet.

  Control de acceso en la vista: los botones de crear y editar solo se dibujan
  para el administrador. Eso es comodidad, no seguridad: quien escriba la URL a
  mano recibe un 403 porque ProductoServlet tambien lo comprueba en el
  servidor.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="titulo" value="Catalogo" scope="request" />
<c:set var="seccion" value="productos" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<div class="cabecera-seccion">
    <div>
        <h1>Cat&aacute;logo</h1>
        <p class="subtitulo">${fn:length(productos)} producto(s) en el listado</p>
    </div>
    <c:if test="${sessionScope.usuario.administrador}">
        <a class="boton principal" href="${ruta}/productos?accion=nuevo">Nuevo producto</a>
    </c:if>
</div>

<form class="barra-filtros" method="get" action="${ruta}/productos">
    <input type="text" name="busqueda" value="<c:out value='${busqueda}' />"
           placeholder="Buscar por nombre o c&oacute;digo" maxlength="60">

    <select name="categoria">
        <option value="">Todas las categor&iacute;as</option>
        <%-- Se recorre el Map de categorias. La clave es el id y el valor el
             nombre; se vuelve a marcar la opcion elegida. --%>
        <c:forEach var="categoria" items="${categorias}">
            <option value="${categoria.key}"
                    ${categoriaSeleccionada eq categoria.key ? 'selected' : ''}>
                <c:out value="${categoria.value}" />
            </option>
        </c:forEach>
    </select>

    <button type="submit" class="boton">Filtrar</button>
    <c:if test="${not empty busqueda or categoriaSeleccionada > 0}">
        <a class="boton tenue" href="${ruta}/productos">Limpiar</a>
    </c:if>
</form>

<c:choose>
    <c:when test="${empty productos}">
        <p class="vacio">Ning&uacute;n producto coincide con el filtro aplicado.</p>
    </c:when>

    <c:otherwise>
        <table class="tabla">
            <thead>
                <tr>
                    <th>C&oacute;digo</th>
                    <th>Producto</th>
                    <th>Categor&iacute;a</th>
                    <th class="derecha">Precio</th>
                    <th class="centro">Stock</th>
                    <th class="centro">Estado</th>
                    <c:if test="${sessionScope.usuario.administrador}">
                        <th class="derecha">Acciones</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="producto" items="${productos}">
                    <tr class="${producto.estado eq 'descontinuado' ? 'inactivo' : ''}">
                        <td><code><c:out value="${producto.codigo}" /></code></td>
                        <td>
                            <strong><c:out value="${producto.nombre}" /></strong>
                            <c:if test="${not empty producto.descripcion}">
                                <span class="descripcion"><c:out value="${producto.descripcion}" /></span>
                            </c:if>
                        </td>
                        <td><c:out value="${producto.categoriaNombre}" /></td>
                        <td class="derecha">
                            <fmt:formatNumber value="${producto.precio}" type="currency"
                                              currencySymbol="$ " maxFractionDigits="0" />
                        </td>
                        <td class="centro">
                            <%-- isStockBajo() se resolvio en la clase Producto.
                                 La vista solo pregunta, no calcula. --%>
                            <c:choose>
                                <c:when test="${producto.stockBajo}">
                                    <span class="estado alerta" title="Por debajo del minimo (${producto.stockMinimo})">
                                        ${producto.stock}
                                    </span>
                                </c:when>
                                <c:otherwise>${producto.stock}</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="centro">
                            <c:choose>
                                <c:when test="${producto.estado eq 'activo'}">
                                    <span class="estado activo">Activo</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="estado neutro">Descontinuado</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${sessionScope.usuario.administrador}">
                            <td class="derecha">
                                <a class="boton pequeno"
                                   href="${ruta}/productos?accion=editar&amp;id=${producto.id}">Editar</a>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${not sessionScope.usuario.administrador}">
            <p class="nota">
                Su rol es <strong>${sessionScope.usuario.rol}</strong>: puede consultar el
                cat&aacute;logo y cotizar, pero no modificar productos.
            </p>
        </c:if>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
