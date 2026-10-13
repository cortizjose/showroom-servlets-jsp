package co.edu.sena.showroom.filtros;

import co.edu.sena.showroom.modelo.Usuario;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/** Filtro que exige sesion activa para entrar al modulo. */
public class FiltroAutenticacion implements Filter {

    private static final List<String> RUTAS_PUBLICAS = Arrays.asList(
            "/login", "/login.jsp", "/recursos/", "/index.jsp");

    @Override
    public void init(FilterConfig configuracion) throws ServletException {
        // Este filtro no necesita configuracion.
    }

    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {

        HttpServletRequest peticionHttp = (HttpServletRequest) solicitud;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;

        String ruta = peticionHttp.getRequestURI().substring(peticionHttp.getContextPath().length());

        if (esPublica(ruta)) {
            cadena.doFilter(solicitud, respuesta);
            return;
        }

        HttpSession sesion = peticionHttp.getSession(false);
        boolean autenticado = sesion != null && sesion.getAttribute("usuario") instanceof Usuario;

        if (autenticado) {
            // La peticion continua su camino hacia el servlet correspondiente.
            cadena.doFilter(solicitud, respuesta);
            return;
        }

        // Sin sesion: se envia al login.
        respuestaHttp.sendRedirect(peticionHttp.getContextPath() + "/login?error=sesion-requerida");
    }

    @Override
    public void destroy() {
        // Nada que liberar.
    }

    private boolean esPublica(String ruta) {
        if (ruta == null || ruta.isEmpty() || "/".equals(ruta)) {
            return true;
        }
        for (String publica : RUTAS_PUBLICAS) {
            if (ruta.equals(publica) || ruta.startsWith(publica)) {
                return true;
            }
        }
        return false;
    }
}
