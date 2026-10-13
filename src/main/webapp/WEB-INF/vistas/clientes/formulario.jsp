<%-- Formulario de cliente: sirve para crear y para editar. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="titulo" value="${cliente.nuevo ? 'Nuevo cliente' : 'Editar cliente'}" scope="request" />
<c:set var="seccion" value="clientes" scope="request" />
<jsp:include page="/WEB-INF/vistas/plantilla/encabezado.jsp" />

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<div class="cabecera-seccion">
    <div>
        <h1>${cliente.nuevo ? 'Nuevo cliente' : 'Editar cliente'}</h1>
        <p class="subtitulo">
            <c:choose>
                <c:when test="${cliente.nuevo}">Registre los datos del cliente.</c:when>
                <c:otherwise>Modifique los datos de <c:out value="${cliente.nombre}" />.</c:otherwise>
            </c:choose>
        </p>
    </div>
    <a class="boton tenue" href="${ruta}/clientes">Volver al listado</a>
</div>

<%-- Resumen de errores de validacion. --%>
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

<form method="post" action="${ruta}/clientes" class="formulario" novalidate>

    <%-- El id solo viaja cuando se esta editando. --%>
    <c:if test="${not cliente.nuevo}">
        <input type="hidden" name="id" value="${cliente.id}">
    </c:if>

    <div class="rejilla-dos">
        <div class="campo ${not empty errores['documento'] ? 'con-error' : ''}">
            <label for="documento">Documento *</label>
            <input type="text" id="documento" name="documento"
                   value="<c:out value='${cliente.documento}' />" maxlength="20" required>
            <c:if test="${not empty errores['documento']}">
                <span class="mensaje-error"><c:out value="${errores['documento']}" /></span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['nombre'] ? 'con-error' : ''}">
            <label for="nombre">Nombre completo *</label>
            <input type="text" id="nombre" name="nombre"
                   value="<c:out value='${cliente.nombre}' />" maxlength="120" required>
            <c:if test="${not empty errores['nombre']}">
                <span class="mensaje-error">${errores['nombre']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['correo'] ? 'con-error' : ''}">
            <label for="correo">Correo electr&oacute;nico *</label>
            <input type="email" id="correo" name="correo"
                   value="<c:out value='${cliente.correo}' />" maxlength="140" required>
            <c:if test="${not empty errores['correo']}">
                <span class="mensaje-error">${errores['correo']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['telefono'] ? 'con-error' : ''}">
            <label for="telefono">Tel&eacute;fono *</label>
            <input type="text" id="telefono" name="telefono"
                   value="<c:out value='${cliente.telefono}' />" maxlength="25" required>
            <c:if test="${not empty errores['telefono']}">
                <span class="mensaje-error">${errores['telefono']}</span>
            </c:if>
        </div>

        <div class="campo ${not empty errores['ciudad'] ? 'con-error' : ''}">
            <label for="ciudad">Ciudad *</label>
            <input type="text" id="ciudad" name="ciudad"
                   value="<c:out value='${cliente.ciudad}' />" maxlength="80" required>
            <c:if test="${not empty errores['ciudad']}">
                <span class="mensaje-error">${errores['ciudad']}</span>
            </c:if>
        </div>

        <div class="campo">
            <label for="direccion">Direcci&oacute;n</label>
            <input type="text" id="direccion" name="direccion"
                   value="<c:out value='${cliente.direccion}' />" maxlength="160">
        </div>
    </div>

    <div class="campo casilla">
        <label>
            <input type="checkbox" name="activo" value="1" ${cliente.activo ? 'checked' : ''}>
            Cliente activo
        </label>
    </div>

    <div class="acciones-formulario">
        <button type="submit" class="boton principal">
            ${cliente.nuevo ? 'Registrar cliente' : 'Guardar cambios'}
        </button>
        <a class="boton tenue" href="${ruta}/clientes">Cancelar</a>
    </div>

    <p class="nota">Los campos marcados con * son obligatorios.</p>
</form>

<jsp:include page="/WEB-INF/vistas/plantilla/pie.jsp" />
