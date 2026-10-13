<%-- Formulario de producto. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="nuevo" value="${producto.id eq 0}" />
<c:set var="titulo" value="${nuevo ? 'Nuevo producto' : 'Editar producto'}" scope="request" />
<c:set var="seccion" value="productos" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<div class="cabecera-seccion">
    <div>
        <h1>${nuevo ? 'Nuevo producto' : 'Editar producto'}</h1>
        <p class="subtitulo">Datos del producto del cat&aacute;logo.</p>
    </div>
    <a class="boton tenue" href="${ruta}/productos">Volver al cat&aacute;logo</a>
</div>

<c:if test="${not empty errores}">
    <div class="aviso error">
        <strong>Revise los siguientes campos:</strong>
        <ul>
            <c:forEach var="entrada" items="${errores}">
                <li><c:out value="${entrada.value}" /></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form method="post" action="${ruta}/productos" class="formulario" novalidate>

    <c:if test="${not nuevo}">
        <input type="hidden" name="id" value="${producto.id}">
    </c:if>

    <div class="rejilla-dos">
        <div class="campo ${not empty errores['codigo'] ? 'con-error' : ''}">
            <label for="codigo">C&oacute;digo *</label>
            <input type="text" id="codigo" name="codigo" value="<c:out value='${producto.codigo}' />"
                   maxlength="20" required placeholder="SOF-009">
            <c:if test="${not empty errores['codigo']}">
                <span class="mensaje-error">${errores['codigo']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['nombre'] ? 'con-error' : ''}">
            <label for="nombre">Nombre *</label>
            <input type="text" id="nombre" name="nombre" value="<c:out value='${producto.nombre}' />"
                   maxlength="120" required>
            <c:if test="${not empty errores['nombre']}">
                <span class="mensaje-error">${errores['nombre']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['categoria'] ? 'con-error' : ''}">
            <label for="categoria">Categor&iacute;a *</label>
            <select id="categoria" name="categoria" required>
                <option value="">Seleccione...</option>
                <c:forEach var="categoria" items="${categorias}">
                    <option value="${categoria.key}"
                            ${producto.categoriaId eq categoria.key ? 'selected' : ''}>
                        <c:out value="${categoria.value}" />
                    </option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores['categoria']}">
                <span class="mensaje-error">${errores['categoria']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['precio'] ? 'con-error' : ''}">
            <label for="precio">Precio *</label>
            <%-- step="0.01" permite centavos; min="0" impide negativos en el navegador. --%>
            <input type="number" id="precio" name="precio" value="${producto.precio}"
                   step="0.01" min="0" required>
            <c:if test="${not empty errores['precio']}">
                <span class="mensaje-error">${errores['precio']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['stock'] ? 'con-error' : ''}">
            <label for="stock">Existencias *</label>
            <input type="number" id="stock" name="stock" value="${producto.stock}"
                   min="0" required>
            <c:if test="${not empty errores['stock']}">
                <span class="mensaje-error">${errores['stock']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['stockMinimo'] ? 'con-error' : ''}">
            <label for="stockMinimo">Stock m&iacute;nimo *</label>
            <input type="number" id="stockMinimo" name="stockMinimo"
                   value="${producto.stockMinimo}" min="0" required>
            <span class="ayuda">Por debajo de este valor el producto aparece como por reponer.</span>
            <c:if test="${not empty errores['stockMinimo']}">
                <span class="mensaje-error">${errores['stockMinimo']}</span>
            </c:if>
        </div>
    </div>

    <div class="campo">
        <label for="descripcion">Descripci&oacute;n</label>
        <textarea id="descripcion" name="descripcion" rows="3" maxlength="400"><c:out value="${producto.descripcion}" /></textarea>
    </div>

    <div class="campo">
        <label for="estado">Estado</label>
        <select id="estado" name="estado">
            <option value="activo" ${producto.estado eq 'activo' ? 'selected' : ''}>Activo</option>
            <option value="descontinuado" ${producto.estado eq 'descontinuado' ? 'selected' : ''}>
                Descontinuado
            </option>
        </select>
        <span class="ayuda">
            Un producto descontinuado no se puede cotizar, pero las cotizaciones
            anteriores que lo contienen siguen intactas.
        </span>
    </div>

    <div class="acciones-formulario">
        <button type="submit" class="boton principal">
            ${nuevo ? 'Crear producto' : 'Guardar cambios'}
        </button>
        <a class="boton tenue" href="${ruta}/productos">Cancelar</a>
    </div>
</form>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
