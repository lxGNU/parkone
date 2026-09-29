package co.edu.sena.parkone.service;

import co.edu.sena.parkone.model.RegistroParqueadero;
import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.repository.RegistroParqueaderoRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Servicio de negocio para el control de ingresos, salidas y liquidacion del parqueadero
public class ParqueaderoService {

    private final RegistroParqueaderoRepository registroRepository;
    private final VehiculoService vehiculoService;

    // Capacidad maxima total del parqueadero (50 cupos)
    private static final int CAPACIDAD_MAXIMA = 50;

    public ParqueaderoService(RegistroParqueaderoRepository registroRepository, VehiculoService vehiculoService) {
        this.registroRepository = registroRepository;
        this.vehiculoService = vehiculoService;
    }

    // Listar todos los registros
    public List<RegistroParqueadero> obtenerTodos() {
        return registroRepository.listarTodos();
    }

    // Listar registros activos (vehiculos dentro del parqueadero)
    public List<RegistroParqueadero> obtenerActivos() {
        return registroRepository.listarActivos();
    }

    // Buscar con filtro
    public List<RegistroParqueadero> buscar(String filtro) {
        return registroRepository.buscarPorFiltro(filtro);
    }

    // Registrar ingreso de un vehiculo
    public RegistroParqueadero registrarIngreso(String placa, String tipo, String propietario, String observaciones) {
        // Validar si el vehiculo ya tiene una entrada activa
        RegistroParqueadero existe = registroRepository.buscarActivoPorPlaca(placa);
        if (existe != null) {
            throw new IllegalArgumentException("El vehículo con placa " + placa + " ya tiene un ingreso activo en el parqueadero.");
        }

        // Obtener o registrar vehiculo
        Vehiculo vehiculo = vehiculoService.obtenerPorPlaca(placa);
        double tarifa = 4000.0;
        if (vehiculo != null) {
            tarifa = vehiculo.getTarifaPorHora();
            vehiculo.setEstado("Estacionado");
        } else {
            tarifa = vehiculoService.obtenerTarifaSegunTipo(tipo);
            vehiculo = new Vehiculo(placa, tipo, "Genérica", "Sin especificar", "00000000", propietario, "Estacionado", tarifa);
            vehiculoService.guardar(vehiculo);
        }

        RegistroParqueadero nuevo = new RegistroParqueadero(placa.toUpperCase(), tipo, propietario, tarifa, observaciones);
        return registroRepository.guardar(nuevo);
    }

    // Registrar salida del vehiculo y calcular la tarifa final a pagar
    public RegistroParqueadero registrarSalida(int idRegistro) {
        RegistroParqueadero registro = registroRepository.buscarPorId(idRegistro);
        if (registro == null) {
            throw new IllegalArgumentException("Registro de parqueadero no encontrado.");
        }

        if ("FINALIZADO".equalsIgnoreCase(registro.getEstado())) {
            return registro;
        }

        LocalDateTime fechaSalida = LocalDateTime.now();
        registro.setFechaSalida(fechaSalida);

        // Calcular la diferencia de horas entre entrada y salida
        Duration duracion = Duration.between(registro.getFechaEntrada(), fechaSalida);
        long minutosTotal = duracion.toMinutes();

        // Minimo cobro de 1 hora, de ahi en adelante se redondea la hora o fraccion
        int horas = (int) Math.max(1, Math.ceil(minutosTotal / 60.0));
        double total = horas * registro.getTarifaAplicada();

        registro.setHorasCobradas(horas);
        registro.setTotalPagar(total);
        registro.setEstado("FINALIZADO");

        // Actualizar estado del vehiculo a Retirado
        Vehiculo v = vehiculoService.obtenerPorPlaca(registro.getPlacaVehiculo());
        if (v != null) {
            v.setEstado("Retirado");
        }

        registroRepository.actualizar(registro);
        return registro;
    }

    // Obtener resumen de estadisticas para el tablero del sistema
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        List<RegistroParqueadero> activos = obtenerActivos();
        List<RegistroParqueadero> todos = obtenerTodos();

        int cuposOcupados = activos.size();
        int cuposDisponibles = Math.max(0, CAPACIDAD_MAXIMA - cuposOcupados);

        double recaudoTotal = todos.stream()
                .filter(r -> "FINALIZADO".equalsIgnoreCase(r.getEstado()))
                .mapToDouble(RegistroParqueadero::getTotalPagar)
                .sum();

        stats.put("capacidadMaxima", CAPACIDAD_MAXIMA);
        stats.put("cuposOcupados", cuposOcupados);
        stats.put("cuposDisponibles", cuposDisponibles);
        stats.put("totalRegistros", todos.size());
        stats.put("recaudoTotal", recaudoTotal);

        return stats;
    }
}
