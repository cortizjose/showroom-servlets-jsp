package co.edu.sena.showroom.dao;

import co.edu.sena.showroom.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Acceso a datos del catalogo de productos.
 *
 * @author Jose Cortes
 */
public class ProductoDAO {

    /** Columnas y JOIN comunes a todas las consultas de lectura. */
    private static final String SELECCION =
            "SELECT p.id, p.codigo, p.nombre, p.descripcion, p.categoria_id, "
            + "c.nombre AS categoria_nombre, p.precio, p.stock, p.stock_minimo, p.estado "
            + "FROM productos p INNER JOIN categorias c ON c.id = p.categoria_id ";

    /**
     * Lista el catalogo con dos filtros opcionales: texto y categoria.
     *
     * Los parametros se van agregando a una lista en el mismo orden en que se
     * agregan las condiciones al SQL. Numerar los indices a mano es una fuente
     * habitual de errores cuando los filtros son opcionales.
     */
    public List<Producto> listar(String busqueda, int categoriaId) throws SQLException {
        List<Producto> productos = new ArrayList<Producto>();
        List<Object> parametros = new ArrayList<Object>();

        StringBuilder sql = new StringBuilder(SELECCION).append("WHERE 1 = 1");

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND (p.nombre LIKE ? OR p.codigo LIKE ?)");
            String patron = "%" + busqueda.trim() + "%";
            parametros.add(patron);
            parametros.add(patron);
        }
        if (categoriaId > 0) {
            sql.append(" AND p.categoria_id = ?");
            parametros.add(categoriaId);
        }
        sql.append(" ORDER BY p.nombre");

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    productos.add(construir(fila));
                }
            }
        }
        return productos;
    }

    /** Productos activos y con existencias, los unicos que se pueden cotizar. */
    public List<Producto> listarDisponibles() throws SQLException {
        List<Producto> productos = new ArrayList<Producto>();
        String sql = SELECCION + "WHERE p.estado = 'activo' AND p.stock > 0 ORDER BY p.nombre";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            while (fila.next()) {
                productos.add(construir(fila));
            }
        }
        return productos;
    }

    public Producto buscarPorId(int id) throws SQLException {
        String sql = SELECCION + "WHERE p.id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? construir(fila) : null;
            }
        }
    }

    public boolean existeCodigo(String codigo, int idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM productos WHERE codigo = ? AND id <> ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigo);
            sentencia.setInt(2, idExcluido);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() && fila.getInt(1) > 0;
            }
        }
    }

    public int insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos "
                   + "(codigo, nombre, descripcion, categoria_id, precio, stock, stock_minimo, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            asignarParametros(sentencia, producto);
            sentencia.executeUpdate();

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    producto.setId(claves.getInt(1));
                }
            }
            return producto.getId();
        }
    }

    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET codigo = ?, nombre = ?, descripcion = ?, categoria_id = ?, "
                   + "precio = ?, stock = ?, stock_minimo = ?, estado = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            asignarParametros(sentencia, producto);
            sentencia.setInt(9, producto.getId());
            return sentencia.executeUpdate() > 0;
        }
    }

    /** Categorias disponibles, para alimentar los menus desplegables. */
    public Map<Integer, String> listarCategorias() throws SQLException {
        Map<Integer, String> categorias = new LinkedHashMap<Integer, String>();
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            while (fila.next()) {
                categorias.put(fila.getInt("id"), fila.getString("nombre"));
            }
        }
        return categorias;
    }

    public int contar() throws SQLException {
        return contarCon("SELECT COUNT(*) FROM productos WHERE estado = 'activo'");
    }

    public int contarStockBajo() throws SQLException {
        return contarCon("SELECT COUNT(*) FROM productos WHERE stock <= stock_minimo AND estado = 'activo'");
    }

    private int contarCon(String sql) throws SQLException {
        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet fila = sentencia.executeQuery()) {

            return fila.next() ? fila.getInt(1) : 0;
        }
    }

    private void asignarParametros(PreparedStatement sentencia, Producto producto) throws SQLException {
        sentencia.setString(1, producto.getCodigo());
        sentencia.setString(2, producto.getNombre());
        sentencia.setString(3, producto.getDescripcion());
        sentencia.setInt(4, producto.getCategoriaId());
        sentencia.setBigDecimal(5, producto.getPrecio());
        sentencia.setInt(6, producto.getStock());
        sentencia.setInt(7, producto.getStockMinimo());
        sentencia.setString(8, producto.getEstado());
    }

    private Producto construir(ResultSet fila) throws SQLException {
        Producto producto = new Producto();
        producto.setId(fila.getInt("id"));
        producto.setCodigo(fila.getString("codigo"));
        producto.setNombre(fila.getString("nombre"));
        producto.setDescripcion(fila.getString("descripcion"));
        producto.setCategoriaId(fila.getInt("categoria_id"));
        producto.setCategoriaNombre(fila.getString("categoria_nombre"));
        producto.setPrecio(fila.getBigDecimal("precio"));
        producto.setStock(fila.getInt("stock"));
        producto.setStockMinimo(fila.getInt("stock_minimo"));
        producto.setEstado(fila.getString("estado"));
        return producto;
    }
}
