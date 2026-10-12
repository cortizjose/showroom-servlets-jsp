<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>

</main>

<footer class="pie">
    <div class="pie-interior">
        <span>
            M&oacute;dulo Comercial del Showroom &middot; Java Servlets y JSP
        </span>
        <span>
            Evidencia GA7-220501096-AA2-EV02 &middot; Jos&eacute; Cort&eacute;s &middot;
            <%-- Expresion JSP: se evalua en el servidor cada vez que se pide la
                 pagina. Muestra el motor de servlets sobre el que corre. --%>
            <%= application.getServerInfo() %>
        </span>
    </div>
</footer>

</body>
</html>
