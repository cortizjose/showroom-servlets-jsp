package co.edu.sena.showroom.filtros;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Fija UTF-8 en la peticion y en la respuesta.
 *
 * Sin esto, un nombre como "Munoz Pena" escrito con enie y con tilde llega al
 * servlet convertido en caracteres raros, porque Tomcat interpreta el cuerpo
 * del POST con una codificacion distinta a la que uso el navegador.
 *
 * Importante: setCharacterEncoding debe llamarse ANTES de leer el primer
 * parametro. Si se llama despues, ya no tiene efecto. Hacerlo en un filtro
 * garantiza que ocurra primero, antes de que cualquier servlet toque la
 * peticion.
 *
 * @author Jose Cortes
 */
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
