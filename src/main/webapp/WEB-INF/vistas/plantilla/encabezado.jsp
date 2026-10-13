<%-- Encabezado comun a todas las paginas internas. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty titulo ? 'Modulo Comercial' : titulo}" /> &middot; Showroom</title>
    <link rel="stylesheet" href="${ruta}/recursos/estilos.css">
</head>
<body>

<header class="barra">
    <div class="barra-interior">
        <a class="marca" href="${ruta}/tablero">
            <span class="marca-icono">SR</span>
            <span>Showroom &middot; M&oacute;dulo Comercial</span>
        </a>

        <nav class="menu">
            <a href="${ruta}/tablero"      class="${seccion eq 'tablero'      ? 'activo' : ''}">Tablero</a>
            <a href="${ruta}/clientes"     class="${seccion eq 'clientes'     ? 'activo' : ''}">Clientes</a>
            <a href="${ruta}/productos"    class="${seccion eq 'productos'    ? 'activo' : ''}">Cat&aacute;logo</a>
            <a href="${ruta}/cotizaciones" class="${seccion eq 'cotizaciones' ? 'activo' : ''}">Cotizaciones</a>
        </nav>

        <div class="sesion">
            <%-- El usuario se lee de la sesion con EL. --%>
            <span class="usuario">
                <c:out value="${sessionScope.usuario.nombre}" />
                <small><c:out value="${sessionScope.usuario.rol}" /></small>
            </span>
            <a class="boton-salir" href="${ruta}/cerrar-sesion">Salir</a>
        </div>
    </div>
</header>

<main class="contenido">

    <%-- Mensajes de resultado. --%>
    <c:choose>
        <c:when test="${param.mensaje eq 'creado'}">
            <p class="aviso exito">El registro se cre&oacute; correctamente.</p>
        </c:when>
        <c:when test="${param.mensaje eq 'actualizado'}">
            <p class="aviso exito">Los cambios se guardaron correctamente.</p>
        </c:when>
        <c:when test="${param.mensaje eq 'desactivado'}">
            <p class="aviso exito">El cliente qued&oacute; inactivo.</p>
        </c:when>
        <c:when test="${param.mensaje eq 'creada'}">
            <p class="aviso exito">La cotizaci&oacute;n se gener&oacute; correctamente.</p>
        </c:when>
        <c:when test="${param.mensaje eq 'estado-actualizado'}">
            <p class="aviso exito">El estado de la cotizaci&oacute;n se actualiz&oacute;.</p>
        </c:when>
    </c:choose>

    <c:choose>
        <c:when test="${param.error eq 'no-encontrado' or param.error eq 'no-encontrada'}">
            <p class="aviso error">No se encontr&oacute; el registro solicitado.</p>
        </c:when>
        <c:when test="${param.error eq 'transicion-invalida'}">
            <p class="aviso error">
                Ese cambio de estado no est&aacute; permitido. Una cotizaci&oacute;n
                aprobada o rechazada ya no admite m&aacute;s cambios.
            </p>
        </c:when>
        <c:when test="${param.error eq 'id-invalido'}">
            <p class="aviso error">El identificador recibido no es v&aacute;lido.</p>
        </c:when>
    </c:choose>
