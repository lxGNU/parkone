package co.edu.sena.parkone.modelo;

public class Usuario {

    private int idUsuario;
    private String identificacion;
    private String nombres;
    private String apellidos;
    private String correo;
    private String celular;
    private String contrasena;
    private String rol;

    public Usuario() {
    }

    // Constructor para nuevos usuarios (sin id)
    public Usuario(String identificacion, String nombres, String apellidos, String correo, String celular, String contrasena, String rol) {
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.celular = celular;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Constructor con id
    public Usuario(int idUsuario, String identificacion, String nombres, String apellidos, String correo, String celular, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.celular = celular;
        this.contrasena = contrasena;
        this.rol = rol;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
