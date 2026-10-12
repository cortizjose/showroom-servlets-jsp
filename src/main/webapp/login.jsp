<%--
  Formulario de inicio de sesion.

  Es el formulario HTML mas importante del modulo y el que mejor muestra la
  diferencia entre GET y POST:

    method="post"  -> las credenciales viajan en el CUERPO de la peticion.
                      Con GET quedarian escritas en la barra de direcciones,
                      en el historial del navegador y en los registros del
                      servidor.

    action="login" -> la atiende LoginServlet, declarado en web.xml.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ruta" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ingresar &middot; M&oacute;dulo Comercial del Showroom</title>
    <link rel="stylesheet" href="${ruta}/recursos/estilos.css">
</head>
<body class="pantalla-login">

<div class="tarjeta-login">

    <div class="login-encabezado">
        <span class="marca-icono grande">SR</span>
        <h1>M&oacute;dulo Comercial</h1>
        <p>Showroom de muebles &middot; Gesti&oacute;n de clientes, cat&aacute;logo y cotizaciones</p>
    </div>

    <%-- Mensajes de error. El servlet los deja como atributo de la peticion
         antes de hacer forward a esta pagina. --%>
    <c:if test="${not empty error}">
        <p class="aviso error"><c:out value="${error}" /></p>
    </c:if>
    <c:if test="${param.error eq 'sesion-requerida'}">
        <p class="aviso error">Debe iniciar sesi&oacute;n para entrar al m&oacute;dulo.</p>
    </c:if>
    <c:if test="${param.mensaje eq 'sesion-cerrada'}">
        <p class="aviso exito">La sesi&oacute;n se cerr&oacute; correctamente.</p>
    </c:if>

    <form method="post" action="${ruta}/login" autocomplete="off">

        <div class="campo">
            <label for="usuario">Usuario</label>
            <%-- El valor se devuelve al usuario si hubo error, para que no
                 tenga que escribirlo otra vez. La contrasena nunca se
                 devuelve. --%>
            <input type="text" id="usuario" name="usuario"
                   value="<c:out value='${usuarioIngresado}' />" required maxlength="40" autofocus>
        </div>

        <div class="campo">
            <label for="contrasena">Contrase&ntilde;a</label>
            <input type="password" id="contrasena" name="contrasena" required maxlength="100">
        </div>

        <button type="submit" class="boton principal ancho">Ingresar</button>
    </form>

    <div class="credenciales">
        <p><strong>Cuentas de prueba</strong></p>
        <table>
            <tr><td><code>admin</code></td><td><code>Admin2025</code></td><td>Administrador</td></tr>
            <tr><td><code>asesor</code></td><td><code>Asesor2025</code></td><td>Asesor</td></tr>
        </table>
    </div>

</div>

</body>
</html>
