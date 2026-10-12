package co.edu.sena.showroom.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Entrega conexiones a la base de datos MySQL.
 *
 * Las credenciales no estan escritas en el codigo: se leen de
 * configuracion.properties, que viaja en WEB-INF/classes. Asi, para pasar de
 * desarrollo a otro servidor se cambia un archivo de texto y no se recompila.
 *
 * El driver se registra una sola vez, en el bloque static, cuando la clase se
 * carga. Repetir Class.forName en cada peticion seria trabajo inutil.
 *
 * @author Jose Cortes
 */
public class ConexionBD {

    private static final String ARCHIVO = "configuracion.properties";

    private static String url;
    private static String usuario;
    private static String contrasena;

    static {
        Properties propiedades = new Properties();
        InputStream entrada = ConexionBD.class.getClassLoader().getResourceAsStream(ARCHIVO);

        if (entrada == null) {
            throw new IllegalStateException(
                    "No se encontro " + ARCHIVO + " en el classpath (WEB-INF/classes).");
        }

        try {
            propiedades.load(entrada);
            url = propiedades.getProperty("bd.url");
            usuario = propiedades.getProperty("bd.usuario");
            contrasena = propiedades.getProperty("bd.contrasena", "");

            // Carga explicita del driver. Desde JDBC 4 el driver se descubre
            // solo, pero declararlo documenta la dependencia y da un error
            // claro si el .jar no esta en WEB-INF/lib.
            Class.forName(propiedades.getProperty("bd.driver", "com.mysql.cj.jdbc.Driver"));

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO, e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No se encontro el driver JDBC de MySQL. "
                    + "Verifique que mysql-connector-j este en WEB-INF/lib.", e);
        } finally {
            try {
                entrada.close();
            } catch (IOException e) {
                // Cerrar el flujo de configuracion no puede tumbar la aplicacion.
            }
        }
    }

    private ConexionBD() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Abre una conexion nueva.
     *
     * Quien la pide es responsable de cerrarla. En este proyecto todos los DAO
     * la obtienen dentro de un try-with-resources, de modo que se cierra sola
     * incluso si la consulta lanza una excepcion.
     */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    /**
     * Comprueba que la base de datos responda. La usa el tablero para avisar
     * de inmediato si MySQL no esta arriba, en lugar de fallar mas adelante con
     * un error incomprensible.
     */
    public static boolean verificar() {
        try (Connection conexion = obtener()) {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }
}
