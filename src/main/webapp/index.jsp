<%--
  Pagina de entrada de la aplicacion.

  No muestra contenido: decide a donde mandar al visitante segun si tiene
  sesion abierta. Se usa un scriptlet (codigo Java dentro del JSP) porque la
  decision ocurre antes de escribir nada en la respuesta.

  Nota: los scriptlets se usan en este proyecto solo en casos como este. La
  practica recomendada es que la logica viva en los servlets y que el JSP use
  EL y JSTL, como ocurre en el resto de las vistas.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="co.edu.sena.showroom.modelo.Usuario" %>
<%
    Usuario conectado = (session == null) ? null : (Usuario) session.getAttribute("usuario");
    String destino = (conectado == null) ? "/login" : "/tablero";
    response.sendRedirect(request.getContextPath() + destino);
%>
