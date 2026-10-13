package co.edu.sena.showroom.modelo;

import java.io.Serializable;

/** Usuario que puede iniciar sesion en el modulo. */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Roles reconocidos por el modulo. */
    public static final String ROL_ADMINISTRADOR = "administrador";
    public static final String ROL_ASESOR = "asesor";

    private int id;
    private String usuario;
    private String nombre;
    private String correo;
    private String rol;
    private boolean activo;

    public Usuario() {
    }

    public Usuario(int id, String usuario, String nombre, String correo, String rol, boolean activo) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
    }

    /** Indica si el usuario tiene permiso de administrador. */
    public boolean isAdministrador() {
        return ROL_ADMINISTRADOR.equals(this.rol);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Usuario{" + usuario + ", rol=" + rol + "}";
    }
}
