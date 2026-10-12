package co.edu.sena.showroom.servlets;

import co.edu.sena.showroom.dao.ProductoDAO;
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

/**
 * Servlet del catalogo de productos.
 *
 * Ademas del CRUD, demuestra el control de acceso por rol: consultar el
 * catalogo lo puede hacer cualquier usuario autenticado, pero crear y editar
 * productos esta reservado al administrador. La comprobacion se hace en el
 * SERVIDOR; ocultar el boton en la vista es comodidad para el usuario, no
 * seguridad.
 *
 * @author Jose Cortes
 */
public class ProductoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductoDAO productoDAO;

    @Override
    public void init() throws ServletException {
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
            if ("nuevo".equals(accion) || "editar".equals(accion)) {
                if (!esAdministrador(peticion)) {
                    rechazar(respuesta);
                    return;
                }
                mostrarFormulario(peticion, respuesta, "editar".equals(accion));
                return;
            }
            listar(peticion, respuesta);

        } catch (SQLException e) {
            throw new ServletException("Error al consultar el catalogo.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        if (!esAdministrador(peticion)) {
            rechazar(respuesta);
            return;
        }

        try {
            guardar(peticion, respuesta);
        } catch (SQLException e) {
            throw new ServletException("Error al guardar el producto.", e);
        }
    }

    // -------------------------------------------------------------------------

    private void listar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        String busqueda = peticion.getParameter("busqueda");
        int categoriaId = aEntero(peticion.getParameter("categoria"));

        peticion.setAttribute("productos", this.productoDAO.listar(busqueda, categoriaId));
        peticion.setAttribute("categorias", this.productoDAO.listarCategorias());
        peticion.setAttribute("busqueda", busqueda == null ? "" : busqueda);
        peticion.setAttribute("categoriaSeleccionada", categoriaId);

        peticion.getRequestDispatcher("/WEB-INF/vistas/productos/lista.jsp")
                .forward(peticion, respuesta);
    }

    private void mostrarFormulario(HttpServletRequest peticion, HttpServletResponse respuesta,
                                   boolean edicion)
            throws SQLException, ServletException, IOException {

        Producto producto;

        if (edicion) {
            int id = aEntero(peticion.getParameter("id"));
            producto = id > 0 ? this.productoDAO.buscarPorId(id) : null;
            if (producto == null) {
                respuesta.sendRedirect(peticion.getContextPath() + "/productos?error=no-encontrado");
                return;
            }
        } else {
            producto = new Producto();
        }

        peticion.setAttribute("producto", producto);
        peticion.setAttribute("categorias", this.productoDAO.listarCategorias());
        peticion.getRequestDispatcher("/WEB-INF/vistas/productos/formulario.jsp")
                .forward(peticion, respuesta);
    }

    private void guardar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        int id = aEntero(peticion.getParameter("id"));

        Validador validador = new Validador();
        Producto producto = new Producto();
        producto.setId(id);
        producto.setCodigo(validador.texto("codigo", "codigo", peticion.getParameter("codigo"), 20));
        producto.setNombre(validador.texto("nombre", "nombre", peticion.getParameter("nombre"), 120));
        producto.setDescripcion(validador.opcional("descripcion", "descripcion",
                peticion.getParameter("descripcion"), 400));
        producto.setCategoriaId(validador.entero("categoria", "categoria",
                peticion.getParameter("categoria"), 1, Integer.MAX_VALUE));
        producto.setPrecio(validador.decimal("precio", "precio", peticion.getParameter("precio")));
        producto.setStock(validador.entero("stock", "stock", peticion.getParameter("stock"), 0, 1000000));
        producto.setStockMinimo(validador.entero("stockMinimo", "stock minimo",
                peticion.getParameter("stockMinimo"), 0, 1000000));

        String estado = Validador.limpiar(peticion.getParameter("estado"));
        producto.setEstado("descontinuado".equals(estado) ? "descontinuado" : "activo");

        if (!producto.getCodigo().isEmpty()
                && this.productoDAO.existeCodigo(producto.getCodigo(), id)) {
            validador.agregarError("codigo", "Ya existe otro producto con ese codigo.");
        }

        if (validador.tieneErrores()) {
            peticion.setAttribute("producto", producto);
            peticion.setAttribute("errores", validador.getErrores());
            peticion.setAttribute("categorias", this.productoDAO.listarCategorias());
            respuesta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            peticion.getRequestDispatcher("/WEB-INF/vistas/productos/formulario.jsp")
                    .forward(peticion, respuesta);
            return;
        }

        if (producto.getId() == 0) {
            this.productoDAO.insertar(producto);
            respuesta.sendRedirect(peticion.getContextPath() + "/productos?mensaje=creado");
        } else {
            this.productoDAO.actualizar(producto);
            respuesta.sendRedirect(peticion.getContextPath() + "/productos?mensaje=actualizado");
        }
    }

    // -------------------------------------------------------------------------

    /** Lee el usuario de la sesion y comprueba su rol. */
    private boolean esAdministrador(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        if (sesion == null) {
            return false;
        }
        Object atributo = sesion.getAttribute("usuario");
        return atributo instanceof Usuario && ((Usuario) atributo).isAdministrador();
    }

    private void rechazar(HttpServletResponse respuesta) throws IOException {
        // 403 Forbidden: el usuario esta identificado, pero su rol no alcanza.
        // Distinto de 401, que significa "no se sabe quien es".
        respuesta.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Solo un administrador puede modificar el catalogo.");
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
