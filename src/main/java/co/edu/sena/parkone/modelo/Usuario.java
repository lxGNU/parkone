package co.edu.sena.parkone.modelo;

import java.io.Serializable;
import java.sql.Timestamp;

// Clase para guardar los datos del usuario
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String identificacion;
    private String nombres;
    private String apellidos;
    private String cargo;
    private String rol;
    private String correo;
    private String contrasena;
    private String telefono;
    private String direccion;
    private Timestamp fechaRegistro;

    public Usuario() {
    }

    public Usuario(String identificacion, String nombres, String apellidos, String cargo, String rol, String correo, String telefono, String direccion) {
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.rol = rol;
        this.correo = correo;
        this.contrasena = "123456";
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Usuario(String identificacion, String nombres, String apellidos, String cargo, String rol, String correo, String contrasena, String telefono, String direccion) {
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.rol = rol;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Usuario(int idUsuario, String identificacion, String nombres, String apellidos, String cargo, String rol, String correo, String telefono, String direccion, Timestamp fechaRegistro) {
        this.idUsuario = idUsuario;
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.rol = rol;
        this.correo = correo;
        this.contrasena = "123456";
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario(int idUsuario, String identificacion, String nombres, String apellidos, String cargo, String rol, String correo, String contrasena, String telefono, String direccion, Timestamp fechaRegistro) {
        this.idUsuario = idUsuario;
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.rol = rol;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNombreCompleto() {
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }
}
