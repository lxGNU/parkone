package co.edu.sena.parkone.controller.api;

import co.edu.sena.parkone.model.RegistroParqueadero;
import co.edu.sena.parkone.service.ParqueaderoService;

import java.util.List;
import java.util.Map;

// Controlador API REST para comunicacion con aplicaciones moviles (Android / Flutter)
public class RegistroApiController {

    private final ParqueaderoService parqueaderoService;

    public RegistroApiController(ParqueaderoService parqueaderoService) {
        this.parqueaderoService = parqueaderoService;
    }

    // Retorna los registros activos del parqueadero
    public List<RegistroParqueadero> listarActivos() {
        return parqueaderoService.obtenerActivos();
    }

    // Retorna las estadisticas generales del parqueadero
    public Map<String, Object> obtenerEstadisticas() {
        return parqueaderoService.obtenerEstadisticas();
    }

    // Registrar ingreso desde app movil
    public RegistroParqueadero ingresarVehiculo(String placa, String tipo, String propietario, String observaciones) {
        return parqueaderoService.registrarIngreso(placa, tipo, propietario, observaciones);
    }

    // Marcar salida desde app movil
    public RegistroParqueadero marcarSalida(int idRegistro) {
        return parqueaderoService.registrarSalida(idRegistro);
    }
}
