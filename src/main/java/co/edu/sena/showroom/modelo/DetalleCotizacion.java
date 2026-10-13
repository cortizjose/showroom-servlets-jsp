package co.edu.sena.showroom.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Linea de detalle de una cotizacion: un producto, una cantidad y su precio. */
public class DetalleCotizacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int cotizacionId;
    private int productoId;
    private String productoCodigo;
    private String productoNombre;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleCotizacion() {
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    /** Construye la linea a partir del producto del catalogo y la cantidad solicitada. */
    public DetalleCotizacion(Producto producto, int cantidad) {
        this.productoId = producto.getId();
        this.productoCodigo = producto.getCodigo();
        this.productoNombre = producto.getNombre();
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        calcularSubtotal();
    }

    /** subtotal = cantidad x precio unitario, redondeado a dos decimales. */
    public void calcularSubtotal() {
        this.subtotal = this.precioUnitario
                .multiply(new BigDecimal(this.cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCotizacionId() {
        return cotizacionId;
    }

    public void setCotizacionId(int cotizacionId) {
        this.cotizacionId = cotizacionId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public void setProductoCodigo(String productoCodigo) {
        this.productoCodigo = productoCodigo;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "Detalle{" + productoCodigo + " x" + cantidad + "}";
    }
}
