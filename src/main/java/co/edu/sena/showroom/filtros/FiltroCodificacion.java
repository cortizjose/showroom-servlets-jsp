package co.edu.sena.showroom.filtros;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/** Fija UTF-8 en la peticion y en la respuesta. */
public class FiltroCodificacion implements Filter {

    private String codificacion = "UTF-8";

    @Override
    public void init(FilterConfig configuracion) throws ServletException {
        String parametro = configuracion.getInitParameter("codificacion");
        if (parametro != null && !parametro.trim().isEmpty()) {
            this.codificacion = parametro.trim();
        }
    }

    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {

        solicitud.setCharacterEncoding(this.codificacion);
        respuesta.setCharacterEncoding(this.codificacion);

        cadena.doFilter(solicitud, respuesta);
    }

    @Override
    public void destroy() {
        // Nada que liberar.
    }
}
