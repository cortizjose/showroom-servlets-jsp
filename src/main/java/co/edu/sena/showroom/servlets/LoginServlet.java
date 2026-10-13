package co.edu.sena.showroom.servlets;

import co.edu.sena.showroom.dao.UsuarioDAO;
import co.edu.sena.showroom.modelo.Usuario;
import co.edu.sena.showroom.util.Validador;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/** Servlet de inicio de sesion. */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UsuarioDAO usuarioDAO;

    /** init() se ejecuta UNA sola vez, cuando Tomcat crea el servlet. */
    @Override
    public void init() throws ServletException {
        this.usuarioDAO = new UsuarioDAO();
    }

    /** GET /login : muestra el formulario. */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        // Si ya hay sesion activa, no tiene sentido pedir las credenciales otra vez.
        HttpSession sesion = peticion.getSession(false);
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            respuesta.sendRedirect(peticion.getContextPath() + "/tablero");
            return;
        }

        peticion.getRequestDispatcher("/login.jsp").forward(peticion, respuesta);
    }

    /** POST /login : valida las credenciales y abre la sesion. */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String nombreUsuario = Validador.limpiar(peticion.getParameter("usuario"));
        String contrasena = peticion.getParameter("contrasena");

        if (nombreUsuario.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            reenviarConError(peticion, respuesta, "Debe indicar el usuario y la contrasena.", nombreUsuario);
            return;
        }

        try {
            Usuario usuario = this.usuarioDAO.autenticar(nombreUsuario, contrasena);

            if (usuario == null) {
                // Un unico mensaje para "no existe" y para "clave incorrecta".
                reenviarConError(peticion, respuesta,
                        "Usuario o contrasena incorrectos.", nombreUsuario);
                return;
            }

            // Sesion nueva despues de autenticar: evita la fijacion de sesion.
            HttpSession anterior = peticion.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession sesion = peticion.getSession(true);
            sesion.setAttribute("usuario", usuario);
            sesion.setMaxInactiveInterval(30 * 60); // 30 minutos de inactividad

            this.usuarioDAO.registrarIngreso(usuario.getId());

            // Redireccion y no forward: asi la URL del navegador queda en /tablero.
            respuesta.sendRedirect(peticion.getContextPath() + "/tablero");

        } catch (SQLException e) {
            throw new ServletException("Error al consultar el usuario en la base de datos.", e);
        }
    }

    private void reenviarConError(HttpServletRequest peticion, HttpServletResponse respuesta,
                                  String mensaje, String usuarioIngresado)
            throws ServletException, IOException {

        peticion.setAttribute("error", mensaje);
        // Se devuelve el usuario escrito para que no tenga que teclearlo de nuevo.
        peticion.setAttribute("usuarioIngresado", usuarioIngresado);
        respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        peticion.getRequestDispatcher("/login.jsp").forward(peticion, respuesta);
    }
}
