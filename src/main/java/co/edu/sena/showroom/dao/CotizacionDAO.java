package co.edu.sena.showroom.dao;

import co.edu.sena.showroom.modelo.Cotizacion;
import co.edu.sena.showroom.modelo.DetalleCotizacion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de cotizaciones y de sus lineas de detalle.
 *
 * Es el DAO mas delicado del modulo, porque guardar una cotizacion implica
 * escribir en dos tablas. Si la cabecera se guardara y el detalle fallara,
 * quedaria una cotizacion sin productos y con totales que no corresponden a
 * nada. Por eso la insercion va dentro de una TRANSACCION: o se guarda todo,
 * o no se guarda nada.
 *
 * @author Jose Cortes
 */
public class CotizacionDAO {

    /**
     * Guarda la cabecera y todas sus lineas en una sola transaccion.
     *
     * Pasos: se desactiva el commit automatico, se inserta la cabecera, se
     * insertan las lineas en lote y se confirma. Si algo falla, rollback deja
     * la base como estaba.
     */
    public int insertar(Cotizacion cotizacion) throws SQLException {
        String sqlCabecera = "INSERT INTO cotizaciones "
                + "(numero, cliente_id, usuario_id, estado, observaciones, subtotal, iva, total, fecha_creacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        String sqlDetalle = "INSERT INTO detalle_cotizaciones "
                + "(cotizacion_id, producto_id, cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtener();
            conexion.setAutoCommit(false);

            int idGenerado;

            // 1. Cabecera
            try (PreparedStatement sentencia =
                         conexion.prepareStatement(sqlCabecera, Statement.RETURN_GENERATED_KEYS)) {

                sentencia.setString(1, siguienteNumero(conexion));
                sentencia.setInt(2, cotizacion.getClienteId());
                sentencia.setInt(3, cotizacion.getUsuarioId());
                sentencia.setString(4, cotizacion.getEstado());
                sentencia.setString(5, cotizacion.getObservaciones());
                sentencia.setBigDecimal(6, cotizacion.getSubtotal());
                sentencia.setBigDecimal(7, cotizacion.getIva());
                sentencia.setBigDecimal(8, cotizacion.getTotal());
                sentencia.executeUpdate();

                try (ResultSet claves = sentencia.getGeneratedKeys()) {
                    if (!claves.next()) {
                        throw new SQLException("La base de datos no devolvio el id de la cotizacion.");
                    }
                    idGenerado = claves.getInt(1);
                }
            }

            // 2. Lineas de detalle, en lote
            try (PreparedStatement sentencia = conexion.prepareStatement(sqlDetalle)) {
                for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
                    sentencia.setInt(1, idGenerado);
                    sentencia.setInt(2, detalle.getProductoId());
                    sentencia.setInt(3, detalle.getCantidad());
                    sentencia.setBigDecimal(4, detalle.getPrecioUnitario());
                    sentencia.setBigDecimal(5, detalle.getSubtotal());
                    sentencia.addBatch();
                }
                sentencia.executeBatch();
            }

            conexion.commit();
            cotizacion.setId(idGenerado);
            return idGenerado;

        } catch (SQLException e) {
            if (conexion != null) {
                conexion.rollback();
            }
            throw e;
        } finally {
            if (conexion != null) {
                conexion.setAutoCommit(true);
                conexion.close();
            }
        }
    }

    /**
     * Calcula el numero consecutivo del documento: COT-000001, COT-000002...
     *
     * Recibe la conexion por parametro para ejecutarse DENTRO de la misma
     * transaccion que la insercion. Si abriera su propia conexion, dos
     * usuarios cotizando al mismo tiempo podrian obtener el mismo numero.
     */
    private String siguienteNumero(Connection conexion) throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(numero, 5) AS UNSIGNED)), 0) + 1 FROM cotizaciones";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            int siguiente = fila.next() ? fila.getInt(1) : 1;
            return String.format("COT-%06d", siguiente);
        }
    }

    /** Lista las cotizaciones, con filtro opcional por estado. */
    public List<Cotizacion> listar(String estado) throws SQLException {
        List<Cotizacion> cotizaciones = new ArrayList<Cotizacion>();

        StringBuilder sql = new StringBuilder(
                "SELECT co.id, co.numero, co.cliente_id, cl.nombre AS cliente_nombre, "
                + "co.usuario_id, u.nombre AS usuario_nombre, co.estado, co.observaciones, "
                + "co.subtotal, co.iva, co.total, co.fecha_creacion "
                + "FROM cotizaciones co "
                + "INNER JOIN clientes cl ON cl.id = co.cliente_id "
                + "INNER JOIN usuarios u ON u.id = co.usuario_id WHERE 1 = 1");

        boolean filtra = estado != null && !estado.trim().isEmpty();
        if (filtra) {
            sql.append(" AND co.estado = ?");
        }
        sql.append(" ORDER BY co.id DESC");

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            if (filtra) {
                sentencia.setString(1, estado.trim());
            }

            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    cotizaciones.add(construir(fila));
                }
            }
        }
        return cotizaciones;
    }

    /** Devuelve la cotizacion con sus lineas de detalle cargadas. */
    public Cotizacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT co.id, co.numero, co.cliente_id, cl.nombre AS cliente_nombre, "
                   + "co.usuario_id, u.nombre AS usuario_nombre, co.estado, co.observaciones, "
                   + "co.subtotal, co.iva, co.total, co.fecha_creacion "
                   + "FROM cotizaciones co "
                   + "INNER JOIN clientes cl ON cl.id = co.cliente_id "
                   + "INNER JOIN usuarios u ON u.id = co.usuario_id WHERE co.id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return null;
                }
                Cotizacion cotizacion = construir(fila);
                cotizacion.setDetalles(listarDetalles(conexion, id));
                return cotizacion;
            }
        }
    }

    private List<DetalleCotizacion> listarDetalles(Connection conexion, int cotizacionId) throws SQLException {
        List<DetalleCotizacion> detalles = new ArrayList<DetalleCotizacion>();

        String sql = "SELECT d.id, d.cotizacion_id, d.producto_id, p.codigo, p.nombre, "
                   + "d.cantidad, d.precio_unitario, d.subtotal "
                   + "FROM detalle_cotizaciones d "
                   + "INNER JOIN productos p ON p.id = d.producto_id "
                   + "WHERE d.cotizacion_id = ? ORDER BY d.id";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, cotizacionId);

            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    DetalleCotizacion detalle = new DetalleCotizacion();
                    detalle.setId(fila.getInt("id"));
                    detalle.setCotizacionId(fila.getInt("cotizacion_id"));
                    detalle.setProductoId(fila.getInt("producto_id"));
                    detalle.setProductoCodigo(fila.getString("codigo"));
                    detalle.setProductoNombre(fila.getString("nombre"));
                    detalle.setCantidad(fila.getInt("cantidad"));
                    detalle.setPrecioUnitario(fila.getBigDecimal("precio_unitario"));
                    detalle.setSubtotal(fila.getBigDecimal("subtotal"));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }

    /** Cambia el estado del documento. */
    public boolean cambiarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE cotizaciones SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, estado);
            sentencia.setInt(2, id);
            return sentencia.executeUpdate() > 0;
        }
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM cotizaciones";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            return fila.next() ? fila.getInt(1) : 0;
        }
    }

    /** Suma de los totales de las cotizaciones aprobadas. */
    public BigDecimal totalAprobado() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM cotizaciones WHERE estado = 'aprobada'";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            return fila.next() ? fila.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    private Cotizacion construir(ResultSet fila) throws SQLException {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setId(fila.getInt("id"));
        cotizacion.setNumero(fila.getString("numero"));
        cotizacion.setClienteId(fila.getInt("cliente_id"));
        cotizacion.setClienteNombre(fila.getString("cliente_nombre"));
        cotizacion.setUsuarioId(fila.getInt("usuario_id"));
        cotizacion.setUsuarioNombre(fila.getString("usuario_nombre"));
        cotizacion.setEstado(fila.getString("estado"));
        cotizacion.setObservaciones(fila.getString("observaciones"));
        cotizacion.setSubtotal(fila.getBigDecimal("subtotal"));
        cotizacion.setIva(fila.getBigDecimal("iva"));
        cotizacion.setTotal(fila.getBigDecimal("total"));
        cotizacion.setFechaCreacion(fila.getTimestamp("fecha_creacion"));
        return cotizacion;
    }
}
