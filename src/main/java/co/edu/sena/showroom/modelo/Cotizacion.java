package co.edu.sena.showroom.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Cotizacion entregada a un cliente del showroom. */
public class Cotizacion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** IVA vigente en Colombia. */
    public static final BigDecimal IVA = new BigDecimal("0.19");

    /** Dias de vigencia de una cotizacion desde su creacion. */
    public static final int DIAS_VIGENCIA = 15;

    public static final String ESTADO_BORRADOR = "borrador";
    public static final String ESTADO_ENVIADA = "enviada";
    public static final String ESTADO_APROBADA = "aprobada";
    public static final String ESTADO_RECHAZADA = "rechazada";

    private int id;
    private String numero;
    private int clienteId;
    private String clienteNombre;
    private int usuarioId;
    private String usuarioNombre;
    private String estado;
    private String observaciones;
    private Timestamp fechaCreacion;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private List<DetalleCotizacion> detalles;

    public Cotizacion() {
        this.estado = ESTADO_BORRADOR;
        this.detalles = new ArrayList<DetalleCotizacion>();
        this.subtotal = BigDecimal.ZERO;
        this.iva = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    /** Agrega una linea y recalcula los totales del documento. */
    public void agregarDetalle(DetalleCotizacion detalle) {
        this.detalles.add(detalle);
        calcularTotales();
    }

    /** Recalcula subtotal, IVA y total a partir de las lineas. */
    public void calcularTotales() {
        BigDecimal acumulado = BigDecimal.ZERO;
        for (DetalleCotizacion detalle : this.detalles) {
            acumulado = acumulado.add(detalle.getSubtotal());
        }
        this.subtotal = acumulado.setScale(2, RoundingMode.HALF_UP);
        this.iva = this.subtotal.multiply(IVA).setScale(2, RoundingMode.HALF_UP);
        this.total = this.subtotal.add(this.iva).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isNueva() {
        return this.id == 0;
    }

    public int getCantidadLineas() {
        return this.detalles.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalleCotizacion> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCotizacion> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Cotizacion{" + numero + ", total=" + total + "}";
    }
}
