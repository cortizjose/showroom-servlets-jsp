package co.edu.sena.showroom.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Entrega conexiones a la base de datos MySQL. */
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

            // Carga explicita del driver.
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

    /** Abre una conexion nueva. */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }

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
