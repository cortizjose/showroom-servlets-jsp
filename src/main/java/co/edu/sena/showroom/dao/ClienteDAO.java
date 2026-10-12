package co.edu.sena.showroom.dao;

import co.edu.sena.showroom.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla clientes.
 *
 * @author Jose Cortes
 */
public class ClienteDAO {

    /**
     * Lista los clientes, con un filtro opcional de busqueda.
     *
     * El filtro se aplica con LIKE sobre nombre y documento. El comodin % se
     * agrega al VALOR del parametro, no a la sentencia: asi la consulta sigue
     * siendo preparada y el texto del usuario nunca se interpreta como SQL.
     */
    public List<Cliente> listar(String busqueda) throws SQLException {
        List<Cliente> clientes = new ArrayList<Cliente>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, documento, nombre, correo, telefono, ciudad, direccion, activo "
                + "FROM clientes WHERE 1 = 1");

        boolean filtra = busqueda != null && !busqueda.trim().isEmpty();
        if (filtra) {
            sql.append(" AND (nombre LIKE ? OR documento LIKE ?)");
        }
        sql.append(" ORDER BY nombre");

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            if (filtra) {
                String patron = "%" + busqueda.trim() + "%";
                sentencia.setString(1, patron);
                sentencia.setString(2, patron);
            }

            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    clientes.add(construir(fila));
                }
            }
        }
        return clientes;
    }

    /** Lista unicamente los clientes activos, para el selector de cotizaciones. */
    public List<Cliente> listarActivos() throws SQLException {
        List<Cliente> clientes = new ArrayList<Cliente>();
        String sql = "SELECT id, documento, nombre, correo, telefono, ciudad, direccion, activo "
                   + "FROM clientes WHERE activo = 1 ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            while (fila.next()) {
                clientes.add(construir(fila));
            }
        }
        return clientes;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, documento, nombre, correo, telefono, ciudad, direccion, activo "
                   + "FROM clientes WHERE id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? construir(fila) : null;
            }
        }
    }

    /**
     * Comprueba si el documento ya esta registrado en otro cliente.
     *
     * El parametro idExcluido permite reutilizar el metodo al editar: al
     * actualizar un cliente, su propio documento no debe contarse como
     * duplicado. Se pasa 0 cuando el cliente es nuevo.
     */
    public boolean existeDocumento(String documento, int idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clientes WHERE documento = ? AND id <> ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, documento);
            sentencia.setInt(2, idExcluido);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() && fila.getInt(1) > 0;
            }
        }
    }

    /**
     * Inserta un cliente y devuelve el identificador que genero la base.
     *
     * Se pide RETURN_GENERATED_KEYS para no tener que hacer una segunda
     * consulta averiguando el id recien creado.
     */
    public int insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (documento, nombre, correo, telefono, ciudad, direccion, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            asignarParametros(sentencia, cliente);
            sentencia.executeUpdate();

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    cliente.setId(claves.getInt(1));
                }
            }
            return cliente.getId();
        }
    }

    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET documento = ?, nombre = ?, correo = ?, telefono = ?, "
                   + "ciudad = ?, direccion = ?, activo = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            asignarParametros(sentencia, cliente);
            sentencia.setInt(8, cliente.getId());
            return sentencia.executeUpdate() > 0;
        }
    }

    /**
     * Baja logica: el cliente se marca como inactivo, no se borra.
     *
     * Borrarlo rompería las cotizaciones que lo referencian y el historial
     * comercial quedaria incompleto. Inactivo significa que no admite
     * documentos nuevos, pero los anteriores siguen consultables.
     */
    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE clientes SET activo = 0 WHERE id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        }
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM clientes WHERE activo = 1";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            return fila.next() ? fila.getInt(1) : 0;
        }
    }

    private void asignarParametros(PreparedStatement sentencia, Cliente cliente) throws SQLException {
        sentencia.setString(1, cliente.getDocumento());
        sentencia.setString(2, cliente.getNombre());
        sentencia.setString(3, cliente.getCorreo());
        sentencia.setString(4, cliente.getTelefono());
        sentencia.setString(5, cliente.getCiudad());
        sentencia.setString(6, cliente.getDireccion());
        sentencia.setBoolean(7, cliente.isActivo());
    }

    private Cliente construir(ResultSet fila) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(fila.getInt("id"));
        cliente.setDocumento(fila.getString("documento"));
        cliente.setNombre(fila.getString("nombre"));
        cliente.setCorreo(fila.getString("correo"));
        cliente.setTelefono(fila.getString("telefono"));
        cliente.setCiudad(fila.getString("ciudad"));
        cliente.setDireccion(fila.getString("direccion"));
        cliente.setActivo(fila.getBoolean("activo"));
        return cliente;
    }
}
