<%-- Pagina para errores no previstos. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error interno del m&oacute;dulo</title>
    <link rel="stylesheet" href="${ruta}/recursos/estilos.css">
</head>
<body class="pantalla-login">

<div class="tarjeta-login ancha">
    <div class="login-encabezado">
        <span class="codigo-error">500</span>
        <h1>Ocurri&oacute; un error inesperado</h1>
        <p>La operaci&oacute;n no se complet&oacute;. No se guard&oacute; ning&uacute;n dato a medias.</p>
    </div>

    <div class="aviso error">
        <strong>Causa probable:</strong>
        Revise que <strong>MySQL</strong> est&eacute; iniciado en el panel de XAMPP y
        que la base <code>showroom_servlets</code> se haya importado con
        <code>database/esquema.sql</code>.
    </div>

    <c:if test="${not empty exception}">
        <div class="detalle-error">
            <p><strong>Tipo:</strong> <code>${exception['class'].name}</code></p>
            <p><strong>Mensaje:</strong> <c:out value="${exception.message}" /></p>
        </div>
    </c:if>

    <a class="boton principal ancho" href="${ruta}/tablero">Volver al tablero</a>
</div>

</body>
</html>
