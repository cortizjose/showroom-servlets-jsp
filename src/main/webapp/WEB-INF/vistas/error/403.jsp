<%--
  Pagina de error 403. Se declara en web.xml con <error-page>, de modo que
  Tomcat la muestra en lugar de su pagina generica.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ruta" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>403 &middot; Acceso denegado</title>
    <link rel="stylesheet" href="${ruta}/recursos/estilos.css">
</head>
<body class="pantalla-login">
<div class="tarjeta-login">
    <div class="login-encabezado">
        <span class="codigo-error">403</span>
        <h1>Acceso denegado</h1>
        <p>Su cuenta no tiene permisos para esta operaci&oacute;n. El cat&aacute;logo solo lo modifica un administrador.</p>
    </div>
    <a class="boton principal ancho" href="${ruta}/tablero">Volver al tablero</a>
</div>
</body>
</html>
