package co.edu.sena.showroom.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Producto del catalogo del showroom. */
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int categoriaId;
    private String categoriaNombre;
    private BigDecimal precio;
    private int stock;
    private int stockMinimo;
    private String estado;

    public Producto() {
        this.precio = BigDecimal.ZERO;
        this.estado = "activo";
    }

    /** Indica si el producto esta por debajo de su nivel minimo de reposicion. */
    public boolean isStockBajo() {
        return this.stock <= this.stockMinimo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Producto{" + codigo + " - " + nombre + "}";
    }
}
