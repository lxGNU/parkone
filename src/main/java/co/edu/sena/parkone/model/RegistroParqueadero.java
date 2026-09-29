package co.edu.sena.parkone.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Clase modelo para el registro de entrada, salida y cobro del parqueadero
public class RegistroParqueadero {

    private int idRegistro;
    private String placaVehiculo;
    private String tipoVehiculo;
    private String propietarioNombre;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private int horasCobradas;
    private double tarifaAplicada;
    private double totalPagar;
    private String estado; // ACTIVO o FINALIZADO
    private String observaciones;

    // Formateador de fecha para mostrar al usuario
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor vacio
    public RegistroParqueadero() {
    }

    // Constructor para registrar un nuevo ingreso
    public RegistroParqueadero(String placaVehiculo, String tipoVehiculo, String propietarioNombre, double tarifaAplicada, String observaciones) {
        this.placaVehiculo = placaVehiculo;
        this.tipoVehiculo = tipoVehiculo;
        this.propietarioNombre = propietarioNombre;
        this.fechaEntrada = LocalDateTime.now();
        this.tarifaAplicada = tarifaAplicada;
        this.estado = "ACTIVO";
        this.observaciones = observaciones;
        this.totalPagar = 0.0;
        this.horasCobradas = 0;
    }

    // Constructor completo
    public RegistroParqueadero(int idRegistro, String placaVehiculo, String tipoVehiculo, String propietarioNombre, LocalDateTime fechaEntrada, LocalDateTime fechaSalida, int horasCobradas, double tarifaAplicada, double totalPagar, String estado, String observaciones) {
        this.idRegistro = idRegistro;
        this.placaVehiculo = placaVehiculo;
        this.tipoVehiculo = tipoVehiculo;
        this.propietarioNombre = propietarioNombre;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.horasCobradas = horasCobradas;
        this.tarifaAplicada = tarifaAplicada;
        this.totalPagar = totalPagar;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // Retorna la fecha de entrada como texto formateado
    public String getFechaEntradaFormateada() {
        return (fechaEntrada != null) ? fechaEntrada.format(FORMATO) : "";
    }

    // Retorna la fecha de salida como texto formateado
    public String getFechaSalidaFormateada() {
        return (fechaSalida != null) ? fechaSalida.format(FORMATO) : "En parqueadero";
    }

    // Getters y Setters
    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getPropietarioNombre() {
        return propietarioNombre;
    }

    public void setPropietarioNombre(String propietarioNombre) {
        this.propietarioNombre = propietarioNombre;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public int getHorasCobradas() {
        return horasCobradas;
    }

    public void setHorasCobradas(int horasCobradas) {
        this.horasCobradas = horasCobradas;
    }

    public double getTarifaAplicada() {
        return tarifaAplicada;
    }

    public void setTarifaAplicada(double tarifaAplicada) {
        this.tarifaAplicada = tarifaAplicada;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
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
}
