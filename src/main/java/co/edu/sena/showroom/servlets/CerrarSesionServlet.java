package co.edu.sena.showroom.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Cierra la sesion del usuario.
 *
 * @author Jose Cortes
 */
public class CerrarSesionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            // invalidate() descarta la sesion completa en el servidor. No basta
            // con borrar el atributo "usuario": el identificador seguiria vivo.
            sesion.invalidate();
        }

        respuesta.sendRedirect(peticion.getContextPath() + "/login?mensaje=sesion-cerrada");
    }
}
