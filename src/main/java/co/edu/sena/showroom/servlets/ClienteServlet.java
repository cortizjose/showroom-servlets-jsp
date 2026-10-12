package co.edu.sena.showroom.servlets;

import co.edu.sena.showroom.dao.ClienteDAO;
import co.edu.sena.showroom.modelo.Cliente;
import co.edu.sena.showroom.util.Validador;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet de gestion de clientes: listar, crear, editar y desactivar.
 *
 * Reparto de responsabilidades entre los dos metodos HTTP:
 *
 *   doGet  -> operaciones de LECTURA: el listado, el formulario vacio y el
 *             formulario cargado con un cliente para editar. El filtro de
 *             busqueda tambien viaja por GET, y eso es deseable: la URL
 *             resultante se puede guardar en favoritos o compartir.
 *
 *   doPost -> operaciones de ESCRITURA: guardar y desactivar. Van por POST
 *             porque modifican la base de datos y no deben repetirse al
 *             recargar la pagina ni quedar en el historial.
 *
 * Despues de cada POST exitoso se hace sendRedirect y no forward. Es el patron
 * POST-Redirect-GET: si el servidor respondiera directamente al POST, al
 * pulsar F5 el navegador volveria a enviarlo y se crearia un cliente
 * duplicado.
 *
 * @author Jose Cortes
 */
public class ClienteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ClienteDAO clienteDAO;

    @Override
    public void init() throws ServletException {
        this.clienteDAO = new ClienteDAO();
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
                case "nuevo":
                    peticion.setAttribute("cliente", new Cliente());
                    peticion.getRequestDispatcher("/WEB-INF/vistas/clientes/formulario.jsp")
                            .forward(peticion, respuesta);
                    break;

                case "editar":
                    mostrarEdicion(peticion, respuesta);
                    break;

                default:
                    listar(peticion, respuesta);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Error al consultar los clientes.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = peticion.getParameter("accion");

        try {
            if ("desactivar".equals(accion)) {
                int id = obtenerId(peticion);
                if (id > 0) {
                    this.clienteDAO.desactivar(id);
                    respuesta.sendRedirect(ruta(peticion, "mensaje=desactivado"));
                } else {
                    respuesta.sendRedirect(ruta(peticion, "error=id-invalido"));
                }
                return;
            }

            guardar(peticion, respuesta);

        } catch (SQLException e) {
            throw new ServletException("Error al guardar el cliente.", e);
        }
    }

    // -------------------------------------------------------------------------
    // Operaciones
    // -------------------------------------------------------------------------

    private void listar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        String busqueda = peticion.getParameter("busqueda");

        peticion.setAttribute("clientes", this.clienteDAO.listar(busqueda));
        peticion.setAttribute("busqueda", busqueda == null ? "" : busqueda);

        peticion.getRequestDispatcher("/WEB-INF/vistas/clientes/lista.jsp")
                .forward(peticion, respuesta);
    }

    private void mostrarEdicion(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        int id = obtenerId(peticion);
        Cliente cliente = id > 0 ? this.clienteDAO.buscarPorId(id) : null;

        if (cliente == null) {
            respuesta.sendRedirect(ruta(peticion, "error=no-encontrado"));
            return;
        }

        peticion.setAttribute("cliente", cliente);
        peticion.getRequestDispatcher("/WEB-INF/vistas/clientes/formulario.jsp")
                .forward(peticion, respuesta);
    }

    /**
     * Guarda un cliente nuevo o actualiza uno existente.
     *
     * El mismo formulario sirve para las dos cosas: si llega un id, se
     * actualiza; si no, se inserta. Asi no hay dos vistas casi identicas que
     * mantener en paralelo.
     */
    private void guardar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws SQLException, ServletException, IOException {

        int id = obtenerId(peticion);

        Validador validador = new Validador();
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setDocumento(validador.texto("documento", "documento", peticion.getParameter("documento"), 20));
        cliente.setNombre(validador.texto("nombre", "nombre", peticion.getParameter("nombre"), 120));
        cliente.setCorreo(validador.correo("correo", "correo", peticion.getParameter("correo")));
        cliente.setTelefono(validador.texto("telefono", "telefono", peticion.getParameter("telefono"), 25));
        cliente.setCiudad(validador.texto("ciudad", "ciudad", peticion.getParameter("ciudad"), 80));
        cliente.setDireccion(validador.opcional("direccion", "direccion", peticion.getParameter("direccion"), 160));
        cliente.setActivo(peticion.getParameter("activo") != null);

        // Regla de negocio: el documento no se puede repetir. Se consulta
        // excluyendo el propio registro, para que al editar no choque consigo
        // mismo.
        if (!cliente.getDocumento().isEmpty()
                && this.clienteDAO.existeDocumento(cliente.getDocumento(), id)) {
            validador.agregarError("documento", "Ya existe otro cliente con ese documento.");
        }

        if (validador.tieneErrores()) {
            // Se devuelve el formulario con los datos escritos y los errores,
            // para que el usuario corrija sin volver a teclear todo.
            peticion.setAttribute("cliente", cliente);
            peticion.setAttribute("errores", validador.getErrores());
            respuesta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            peticion.getRequestDispatcher("/WEB-INF/vistas/clientes/formulario.jsp")
                    .forward(peticion, respuesta);
            return;
        }

        if (cliente.isNuevo()) {
            this.clienteDAO.insertar(cliente);
            respuesta.sendRedirect(ruta(peticion, "mensaje=creado"));
        } else {
            this.clienteDAO.actualizar(cliente);
            respuesta.sendRedirect(ruta(peticion, "mensaje=actualizado"));
        }
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    /**
     * Lee el parametro id y lo convierte a entero.
     *
     * Devuelve 0 cuando no viene o no es numerico. Nunca se confia en que el
     * parametro tenga el formato esperado: cualquiera puede escribir
     * ?id=abc en la barra de direcciones.
     */
    private int obtenerId(HttpServletRequest peticion) {
        String valor = peticion.getParameter("id");
        if (valor == null || valor.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String ruta(HttpServletRequest peticion, String consulta) {
        return peticion.getContextPath() + "/clientes?" + consulta;
    }
}
