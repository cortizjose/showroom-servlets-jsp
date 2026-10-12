package co.edu.sena.showroom.servlets;

import co.edu.sena.showroom.dao.ClienteDAO;
import co.edu.sena.showroom.dao.ConexionBD;
import co.edu.sena.showroom.dao.CotizacionDAO;
import co.edu.sena.showroom.dao.ProductoDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Tablero de inicio: muestra los indicadores del modulo.
 *
 * Solo responde a GET, porque unicamente consulta. Un servlet que no modifica
 * nada no tiene por que aceptar POST: si no se implementa doPost, HttpServlet
 * responde 405 Method Not Allowed por su cuenta, y esa es la respuesta
 * correcta.
 *
 * @author Jose Cortes
 */
public class TableroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;
    private CotizacionDAO cotizacionDAO;

    @Override
    public void init() throws ServletException {
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
        this.cotizacionDAO = new CotizacionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            peticion.setAttribute("totalClientes", this.clienteDAO.contar());
            peticion.setAttribute("totalProductos", this.productoDAO.contar());
            peticion.setAttribute("productosStockBajo", this.productoDAO.contarStockBajo());
            peticion.setAttribute("totalCotizaciones", this.cotizacionDAO.contar());
            peticion.setAttribute("montoAprobado", this.cotizacionDAO.totalAprobado());
            peticion.setAttribute("baseDatosActiva", ConexionBD.verificar());

            peticion.getRequestDispatcher("/WEB-INF/vistas/tablero.jsp")
                    .forward(peticion, respuesta);

        } catch (SQLException e) {
            throw new ServletException("Error al calcular los indicadores del tablero.", e);
        }
    }
}
