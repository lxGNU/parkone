package co.edu.sena.parkone.modelo;

import java.io.Serializable;
import java.sql.Timestamp;

// Clase para los datos de los vehiculos
public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idVehiculo;
    private String placa;
    private String tipo;
    private String marca;
    private String color;
    private String propietarioCedula;
    private String propietarioNombre;
    private String estado;
    private double tarifaPorHora;
    private Timestamp fechaIngreso;
    private Timestamp fechaSalida;

    public Vehiculo() {
    }

    public Vehiculo(String placa, String tipo, String marca, String color, String propietarioCedula, String propietarioNombre, String estado, double tarifaPorHora) {
        this.placa = placa;
        this.tipo = tipo;
        this.marca = marca;
        this.color = color;
        this.propietarioCedula = propietarioCedula;
        this.propietarioNombre = propietarioNombre;
        this.estado = estado;
        this.tarifaPorHora = tarifaPorHora;
    }

    public Vehiculo(int idVehiculo, String placa, String tipo, String marca, String color, String propietarioCedula, String propietarioNombre, String estado, double tarifaPorHora, Timestamp fechaIngreso, Timestamp fechaSalida) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.tipo = tipo;
        this.marca = marca;
        this.color = color;
        this.propietarioCedula = propietarioCedula;
        this.propietarioNombre = propietarioNombre;
        this.estado = estado;
        this.tarifaPorHora = tarifaPorHora;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
    }

    // Getters y Setters
    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPropietarioCedula() {
        return propietarioCedula;
    }

    public void setPropietarioCedula(String propietarioCedula) {
        this.propietarioCedula = propietarioCedula;
    }

    public String getPropietarioNombre() {
        return propietarioNombre;
    }

    public void setPropietarioNombre(String propietarioNombre) {
        this.propietarioNombre = propietarioNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTarifaPorHora() {
        return tarifaPorHora;
    }

    public void setTarifaPorHora(double tarifaPorHora) {
        this.tarifaPorHora = tarifaPorHora;
    }

    public Timestamp getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Timestamp fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Timestamp getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Timestamp fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}
