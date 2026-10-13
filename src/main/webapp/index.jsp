<%-- Pagina de entrada de la aplicacion. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="co.edu.sena.showroom.modelo.Usuario" %>
<%
    Usuario conectado = (session == null) ? null : (Usuario) session.getAttribute("usuario");
    String destino = (conectado == null) ? "/login" : "/tablero";
    response.sendRedirect(request.getContextPath() + destino);
%>
