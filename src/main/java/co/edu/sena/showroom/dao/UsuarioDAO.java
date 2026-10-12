package co.edu.sena.showroom.dao;

import co.edu.sena.showroom.modelo.Usuario;
import co.edu.sena.showroom.util.Seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acceso a datos de la tabla usuarios.
 *
 * Patron DAO (Data Access Object): todo el SQL de usuarios vive aqui. El
 * servlet no sabe si los datos vienen de MySQL, de un archivo o de otro
 * servicio; solo pide objetos Usuario. Si manana cambia el motor, se cambia
 * esta clase y el resto del modulo queda intacto.
 *
 * Todas las consultas usan PreparedStatement con parametros. Nunca se
 * concatena un valor dentro del SQL: eso es lo que abre la puerta a la
 * inyeccion SQL. Con parametros, el valor viaja aparte de la sentencia y el
 * motor lo trata siempre como dato, jamas como codigo.
 *
 * @author Jose Cortes
 */
public class UsuarioDAO {

    /**
     * Autentica un usuario por su nombre y contrasena.
     *
     * @return el usuario si las credenciales son correctas y la cuenta esta
     *         activa; null en cualquier otro caso.
     *
     * Se devuelve null tanto si el usuario no existe como si la contrasena es
     * incorrecta, y el servlet muestra el mismo mensaje en ambos casos. Si los
     * mensajes fueran distintos, cualquiera podria averiguar que usuarios
     * existen probando nombres: eso se llama enumeracion de usuarios.
     */
    public Usuario autenticar(String nombreUsuario, String contrasena) throws SQLException {
        String sql = "SELECT id, usuario, nombre, correo, rol, activo, sal, contrasena_hash "
                   + "FROM usuarios WHERE usuario = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombreUsuario);

            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return null;
                }

                boolean correcta = Seguridad.verificar(
                        fila.getString("sal"),
                        fila.getString("contrasena_hash"),
                        contrasena);

                if (!correcta || !fila.getBoolean("activo")) {
                    return null;
                }

                return construir(fila);
            }
        }
    }

    /** Busca un usuario por su identificador. */
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, usuario, nombre, correo, rol, activo FROM usuarios WHERE id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? construir(fila) : null;
            }
        }
    }

    /** Registra el momento del ultimo ingreso exitoso. */
    public void registrarIngreso(int usuarioId) throws SQLException {
        String sql = "UPDATE usuarios SET ultimo_ingreso = NOW() WHERE id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.executeUpdate();
        }
    }

    /** Traduce una fila del ResultSet a un objeto del dominio. */
    private Usuario construir(ResultSet fila) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(fila.getInt("id"));
        usuario.setUsuario(fila.getString("usuario"));
        usuario.setNombre(fila.getString("nombre"));
        usuario.setCorreo(fila.getString("correo"));
        usuario.setRol(fila.getString("rol"));
        usuario.setActivo(fila.getBoolean("activo"));
        return usuario;
    }
}
