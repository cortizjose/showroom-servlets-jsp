<%-- Pagina de error 404. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ruta" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>404 &middot; P&aacute;gina no encontrada</title>
    <link rel="stylesheet" href="${ruta}/recursos/estilos.css">
</head>
<body class="pantalla-login">
<div class="tarjeta-login">
    <div class="login-encabezado">
        <span class="codigo-error">404</span>
        <h1>P&aacute;gina no encontrada</h1>
        <p>La direcci&oacute;n solicitada no existe en el m&oacute;dulo.</p>
    </div>
    <a class="boton principal ancho" href="${ruta}/tablero">Volver al tablero</a>
</div>
</body>
</html>
