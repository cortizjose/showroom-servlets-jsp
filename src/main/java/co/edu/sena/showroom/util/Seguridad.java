package co.edu.sena.showroom.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Cifrado de contrasenas del modulo.
 *
 * La contrasena NUNCA se guarda en texto claro. Se guarda su resumen (hash)
 * junto con una sal aleatoria distinta para cada usuario.
 *
 * Por que la sal: sin ella, dos usuarios con la misma contrasena tendrian el
 * mismo hash, y bastaria una tabla precalculada para romper los dos a la vez.
 * Con una sal por usuario, el mismo texto produce resumenes distintos.
 *
 * Nota honesta sobre el algoritmo: aqui se usa SHA-256 porque viene en la
 * biblioteca estandar de Java y este proyecto no incorpora dependencias
 * externas. En un sistema en produccion corresponderia bcrypt, scrypt o
 * Argon2, que son deliberadamente lentos y por eso resisten mucho mejor un
 * ataque por fuerza bruta. SHA-256 es rapido, y esa velocidad, que aqui no
 * molesta, es justamente la que favorece al atacante.
 *
 * @author Jose Cortes
 */
public final class Seguridad {

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Seguridad() {
        // Clase de utilidad: no se instancia.
    }

    /** Genera una sal aleatoria de 16 bytes en notacion hexadecimal. */
    public static String generarSal() {
        byte[] bytes = new byte[16];
        ALEATORIO.nextBytes(bytes);
        return aHexadecimal(bytes);
    }

    /** Calcula el resumen SHA-256 de la sal concatenada con la contrasena. */
    public static String calcularHash(String sal, String contrasena) {
        try {
            MessageDigest digestor = MessageDigest.getInstance("SHA-256");
            byte[] resumen = digestor.digest((sal + ":" + contrasena).getBytes(StandardCharsets.UTF_8));
            return aHexadecimal(resumen);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 es obligatorio en toda implementacion de Java, de modo
            // que esta rama no deberia ocurrir nunca.
            throw new IllegalStateException("La plataforma no soporta SHA-256", e);
        }
    }

    /**
     * Verifica una contrasena contra el hash almacenado.
     *
     * La comparacion se hace con MessageDigest.isEqual y no con String.equals
     * porque esa comparacion es de tiempo constante: no se detiene en el primer
     * caracter distinto. Asi no se filtra informacion por el tiempo de
     * respuesta.
     */
    public static boolean verificar(String sal, String hashAlmacenado, String contrasena) {
        if (sal == null || hashAlmacenado == null || contrasena == null) {
            return false;
        }
        String calculado = calcularHash(sal, contrasena);
        return MessageDigest.isEqual(
                calculado.getBytes(StandardCharsets.UTF_8),
                hashAlmacenado.getBytes(StandardCharsets.UTF_8));
    }

    private static String aHexadecimal(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Permite generar un par sal/hash desde la linea de comandos, para sembrar
     * usuarios en el script SQL sin escribir contrasenas en texto claro.
     *
     *   java -cp WEB-INF/classes co.edu.sena.showroom.util.Seguridad Admin2025
     */
    public static void main(String[] argumentos) {
        if (argumentos.length != 1) {
            System.out.println("Uso: java co.edu.sena.showroom.util.Seguridad <contrasena>");
            return;
        }
        String sal = generarSal();
        System.out.println("sal  = " + sal);
        System.out.println("hash = " + calcularHash(sal, argumentos[0]));
    }
}
