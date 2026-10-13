package co.edu.sena.showroom.dao;

import co.edu.sena.showroom.modelo.Usuario;
import co.edu.sena.showroom.util.Seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Acceso a datos de la tabla usuarios. */
public class UsuarioDAO {

    /** Autentica un usuario por su nombre y contrasena. */
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
