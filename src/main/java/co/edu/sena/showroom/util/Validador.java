package co.edu.sena.showroom.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Valida los datos que llegan de un formulario HTML y acumula los errores.
 *
 * Por que acumular y no fallar en el primer error: si el formulario tiene tres
 * campos mal, el usuario debe verlos los tres de una vez. Devolver solo el
 * primero obliga a enviar el formulario cuatro veces.
 *
 * Se usa LinkedHashMap para que los errores se muestren en el mismo orden en
 * que estan los campos en el formulario.
 *
 * Importante: esta validacion ocurre en el SERVIDOR. Los atributos required y
 * type del HTML ayudan al usuario, pero se pueden desactivar desde el
 * navegador, asi que nunca son la ultima defensa.
 *
 * @author Jose Cortes
 */
public class Validador {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]{2,}$");

    private final Map<String, String> errores = new LinkedHashMap<String, String>();

    /** Exige que el campo venga y no este vacio. */
    public String requerido(String campo, String etiqueta, String valor) {
        String limpio = limpiar(valor);
        if (limpio.isEmpty()) {
            errores.put(campo, "El campo " + etiqueta + " es obligatorio.");
            return "";
        }
        return limpio;
    }

    /** Exige el campo y le impone una longitud maxima. */
    public String texto(String campo, String etiqueta, String valor, int maximo) {
        String limpio = requerido(campo, etiqueta, valor);
        if (!limpio.isEmpty() && limpio.length() > maximo) {
            errores.put(campo, "El campo " + etiqueta + " no puede exceder " + maximo + " caracteres.");
        }
        return limpio;
    }

    /** Campo opcional con longitud maxima. */
    public String opcional(String campo, String etiqueta, String valor, int maximo) {
        String limpio = limpiar(valor);
        if (limpio.length() > maximo) {
            errores.put(campo, "El campo " + etiqueta + " no puede exceder " + maximo + " caracteres.");
        }
        return limpio;
    }

    /** Valida el formato de un correo electronico. */
    public String correo(String campo, String etiqueta, String valor) {
        String limpio = requerido(campo, etiqueta, valor);
        if (!limpio.isEmpty() && !PATRON_CORREO.matcher(limpio).matches()) {
            errores.put(campo, "El campo " + etiqueta + " no tiene un formato valido.");
        }
        return limpio;
    }

    /** Convierte a entero y verifica que este dentro del rango permitido. */
    public int entero(String campo, String etiqueta, String valor, int minimo, int maximo) {
        String limpio = limpiar(valor);
        if (limpio.isEmpty()) {
            errores.put(campo, "El campo " + etiqueta + " es obligatorio.");
            return 0;
        }
        try {
            int numero = Integer.parseInt(limpio);
            if (numero < minimo || numero > maximo) {
                errores.put(campo, "El campo " + etiqueta + " debe estar entre " + minimo + " y " + maximo + ".");
            }
            return numero;
        } catch (NumberFormatException e) {
            errores.put(campo, "El campo " + etiqueta + " debe ser un numero entero.");
            return 0;
        }
    }

    /** Convierte a BigDecimal y exige que no sea negativo. */
    public BigDecimal decimal(String campo, String etiqueta, String valor) {
        String limpio = limpiar(valor).replace(",", ".");
        if (limpio.isEmpty()) {
            errores.put(campo, "El campo " + etiqueta + " es obligatorio.");
            return BigDecimal.ZERO;
        }
        try {
            BigDecimal numero = new BigDecimal(limpio);
            if (numero.signum() < 0) {
                errores.put(campo, "El campo " + etiqueta + " no puede ser negativo.");
            }
            return numero;
        } catch (NumberFormatException e) {
            errores.put(campo, "El campo " + etiqueta + " debe ser un valor numerico.");
            return BigDecimal.ZERO;
        }
    }

    /**
     * Normaliza un valor recibido del formulario: quita espacios sobrantes y
     * elimina las etiquetas HTML.
     *
     * Quitar las etiquetas evita que un texto como <script>...</script> quede
     * guardado y se ejecute al mostrarlo (XSS almacenado).
     *
     * Esta es solo la primera barrera. La segunda esta en las vistas, que
     * imprimen los datos del usuario con <c:out>, el cual escapa los
     * caracteres menor que, mayor que, ampersand y las comillas. Hace falta,
     * porque una expresion EL suelta como ${cliente.nombre} NO escapa nada: un
     * nombre con una comilla doble se saldria del atributo value de un
     * formulario.
     */
    public static String limpiar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("<[^>]*>", "").trim();
    }

    public void agregarError(String campo, String mensaje) {
        errores.put(campo, mensaje);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public Map<String, String> getErrores() {
        return errores;
    }

    /** Los errores en una sola linea, para mostrarlos en un mensaje unico. */
    public String getResumen() {
        return String.join(" ", errores.values());
    }
}
