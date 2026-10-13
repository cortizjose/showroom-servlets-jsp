package co.edu.sena.showroom.modelo;

import java.io.Serializable;

/** Cliente del showroom. */
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String documento;
    private String nombre;
    private String correo;
    private String telefono;
    private String ciudad;
    private String direccion;
    private boolean activo;

    public Cliente() {
        // Un cliente nuevo nace activo: es lo que espera el formulario de registro.
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /** true cuando el objeto todavia no se ha guardado en la base de datos. */
    public boolean isNuevo() {
        return this.id == 0;
    }

    @Override
    public String toString() {
        return "Cliente{" + documento + " - " + nombre + "}";
    }
}
