package co.edu.sena.parkone.service;

import co.edu.sena.parkone.model.RegistroParqueadero;
import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.repository.RegistroParqueaderoRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Logica del parqueadero
public class ParqueaderoService {

    private final RegistroParqueaderoRepository registroRepository;
    private final VehiculoService vehiculoService;

    private static final int CAPACIDAD_MAXIMA = 50;

    public ParqueaderoService(RegistroParqueaderoRepository registroRepository, VehiculoService vehiculoService) {
        this.registroRepository = registroRepository;
        this.vehiculoService = vehiculoService;
    }

    public List<RegistroParqueadero> obtenerTodos() {
        return registroRepository.listarTodos();
    }

    public List<RegistroParqueadero> obtenerActivos() {
        return registroRepository.listarActivos();
    }

    public List<RegistroParqueadero> buscar(String filtro) {
        return registroRepository.buscarPorFiltro(filtro);
    }

    public RegistroParqueadero registrarIngreso(String placa, String tipo, String propietario, String observaciones) {
        RegistroParqueadero existe = registroRepository.buscarActivoPorPlaca(placa);
        if (existe != null) {
            throw new IllegalArgumentException("El vehículo con placa " + placa + " ya tiene un ingreso activo en el parqueadero.");
        }

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

        Duration duracion = Duration.between(registro.getFechaEntrada(), fechaSalida);
        long minutosTotal = duracion.toMinutes();

        // Minimo 1 hora
        int horas = (int) Math.max(1, Math.ceil(minutosTotal / 60.0));
        double total = horas * registro.getTarifaAplicada();

        registro.setHorasCobradas(horas);
        registro.setTotalPagar(total);
        registro.setEstado("FINALIZADO");

        Vehiculo v = vehiculoService.obtenerPorPlaca(registro.getPlacaVehiculo());
        if (v != null) {
            v.setEstado("Retirado");
        }

        registroRepository.actualizar(registro);
        return registro;
    }

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
