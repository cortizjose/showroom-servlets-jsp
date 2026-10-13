package co.edu.sena.showroom.servlets;

import co.edu.sena.showroom.dao.ClienteDAO;
import co.edu.sena.showroom.dao.CotizacionDAO;
import co.edu.sena.showroom.dao.ProductoDAO;
import co.edu.sena.showroom.modelo.Cotizacion;
import co.edu.sena.showroom.modelo.DetalleCotizacion;
import co.edu.sena.showroom.modelo.Producto;
import co.edu.sena.showroom.modelo.Usuario;
import co.edu.sena.showroom.util.Validador;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/** Servlet de cotizaciones: el nucleo del modulo. */
public class CotizacionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CotizacionDAO cotizacionDAO;
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;

    @Override
    public void init() throws ServletException {
        this.cotizacionDAO = new CotizacionDAO();
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = peticion.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "nueva":
                    mostrarFormulario(peticion, respuesta);
                    break;

                case "detalle":
                    mostrarDetalle(peticion, respuesta);
                    break;

                default:
                    listar(peticion, respuesta);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Error al consultar las cotizaciones.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = peticion.getParameter("accion");

        try {
            if ("estado".equals(accion)) {
                cambiarEstado(peticion, respuesta);
                return;
            }
            guardar(peticion, respuesta);

        } catch (SQLException e) {
            throw new ServletException("Error al guardar la cotizacion.", e);
        }
    }

    // -------------------------------------------------------------------------

    private void listar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        String estado = peticion.getParameter("estado");

        peticion.setAttribute("cotizaciones", this.cotizacionDAO.listar(estado));
        peticion.setAttribute("estadoSeleccionado", estado == null ? "" : estado);

        peticion.getRequestDispatcher("/WEB-INF/vistas/cotizaciones/lista.jsp")
                .forward(peticion, respuesta);
    }

    private void mostrarFormulario(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        peticion.setAttribute("clientes", this.clienteDAO.listarActivos());
        peticion.setAttribute("productos", this.productoDAO.listarDisponibles());

        peticion.getRequestDispatcher("/WEB-INF/vistas/cotizaciones/formulario.jsp")
                .forward(peticion, respuesta);
    }

    private void mostrarDetalle(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        int id = aEntero(peticion.getParameter("id"));
        Cotizacion cotizacion = id > 0 ? this.cotizacionDAO.buscarPorId(id) : null;

        if (cotizacion == null) {
            respuesta.sendRedirect(peticion.getContextPath() + "/cotizaciones?error=no-encontrada");
            return;
        }

        peticion.setAttribute("cotizacion", cotizacion);
        peticion.getRequestDispatcher("/WEB-INF/vistas/cotizaciones/detalle.jsp")
                .forward(peticion, respuesta);
    }

    /** Construye la cotizacion a partir del formulario y la guarda. */
    private void guardar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        Usuario autor = (Usuario) peticion.getSession().getAttribute("usuario");

        Validador validador = new Validador();
        int clienteId = validador.entero("cliente", "cliente",
                peticion.getParameter("clienteId"), 1, Integer.MAX_VALUE);
        String observaciones = validador.opcional("observaciones", "observaciones",
                peticion.getParameter("observaciones"), 400);

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setClienteId(clienteId);
        cotizacion.setUsuarioId(autor.getId());
        cotizacion.setObservaciones(observaciones);

        // Las dos listas paralelas que envia el formulario.
        String[] productoIds = peticion.getParameterValues("productoId");
        String[] cantidades = peticion.getParameterValues("cantidad");

        if (productoIds == null || cantidades == null || productoIds.length == 0) {
            validador.agregarError("lineas", "Debe agregar al menos un producto a la cotizacion.");
        } else {
            armarLineas(cotizacion, productoIds, cantidades, validador);
        }

        if (validador.tieneErrores()) {
            peticion.setAttribute("errores", validador.getErrores());
            peticion.setAttribute("clientes", this.clienteDAO.listarActivos());
            peticion.setAttribute("productos", this.productoDAO.listarDisponibles());
            peticion.setAttribute("clienteSeleccionado", clienteId);
            peticion.setAttribute("observaciones", observaciones);
            respuesta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            peticion.getRequestDispatcher("/WEB-INF/vistas/cotizaciones/formulario.jsp")
                    .forward(peticion, respuesta);
            return;
        }

        int id = this.cotizacionDAO.insertar(cotizacion);
        respuesta.sendRedirect(peticion.getContextPath()
                + "/cotizaciones?accion=detalle&id=" + id + "&mensaje=creada");
    }

    /** Recorre las lineas del formulario y las agrega a la cotizacion. */
    private void armarLineas(Cotizacion cotizacion, String[] productoIds, String[] cantidades,
                             Validador validador) throws SQLException {

        int lineasValidas = 0;

        for (int i = 0; i < productoIds.length; i++) {
            int productoId = aEntero(productoIds[i]);
            int cantidad = i < cantidades.length ? aEntero(cantidades[i]) : 0;

            if (productoId <= 0 || cantidad <= 0) {
                continue;
            }

            Producto producto = this.productoDAO.buscarPorId(productoId);

            if (producto == null) {
                validador.agregarError("producto" + productoId,
                        "El producto indicado no existe en el catalogo.");
                continue;
            }
            if (!"activo".equals(producto.getEstado())) {
                validador.agregarError("producto" + productoId,
                        "El producto " + producto.getNombre() + " esta descontinuado.");
                continue;
            }
            if (cantidad > producto.getStock()) {
                validador.agregarError("producto" + productoId,
                        "Solo hay " + producto.getStock() + " unidades de " + producto.getNombre() + ".");
                continue;
            }

            // El precio sale del objeto Producto recien leido de la base, nunca del formulario.
            cotizacion.agregarDetalle(new DetalleCotizacion(producto, cantidad));
            lineasValidas++;
        }

        if (lineasValidas == 0 && !validador.tieneErrores()) {
            validador.agregarError("lineas",
                    "Debe indicar una cantidad mayor que cero en al menos un producto.");
        }
    }

    /** Cambia el estado de la cotizacion respetando las transiciones validas. */
    private void cambiarEstado(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, IOException {

        int id = aEntero(peticion.getParameter("id"));
        String nuevoEstado = Validador.limpiar(peticion.getParameter("nuevoEstado"));

        Cotizacion cotizacion = id > 0 ? this.cotizacionDAO.buscarPorId(id) : null;

        if (cotizacion == null) {
            respuesta.sendRedirect(peticion.getContextPath() + "/cotizaciones?error=no-encontrada");
            return;
        }

        if (!esTransicionValida(cotizacion.getEstado(), nuevoEstado)) {
            respuesta.sendRedirect(peticion.getContextPath()
                    + "/cotizaciones?accion=detalle&id=" + id + "&error=transicion-invalida");
            return;
        }

        this.cotizacionDAO.cambiarEstado(id, nuevoEstado);
        respuesta.sendRedirect(peticion.getContextPath()
                + "/cotizaciones?accion=detalle&id=" + id + "&mensaje=estado-actualizado");
    }

    /** Maquina de estados del documento. */
    private boolean esTransicionValida(String actual, String siguiente) {
        if (Cotizacion.ESTADO_BORRADOR.equals(actual)) {
            return Cotizacion.ESTADO_ENVIADA.equals(siguiente);
        }
        if (Cotizacion.ESTADO_ENVIADA.equals(actual)) {
            return Cotizacion.ESTADO_APROBADA.equals(siguiente)
                    || Cotizacion.ESTADO_RECHAZADA.equals(siguiente);
        }
        // aprobada y rechazada son estados finales.
        return false;
    }

    private int aEntero(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
