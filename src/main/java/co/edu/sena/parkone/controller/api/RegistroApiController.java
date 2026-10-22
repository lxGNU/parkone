package co.edu.sena.parkone.controller.api;

import co.edu.sena.parkone.model.RegistroParqueadero;
import co.edu.sena.parkone.service.ParqueaderoService;

import java.util.List;
import java.util.Map;

// API para movil
public class RegistroApiController {

    private final ParqueaderoService parqueaderoService;

    public RegistroApiController(ParqueaderoService parqueaderoService) {
        this.parqueaderoService = parqueaderoService;
    }

    public List<RegistroParqueadero> listarActivos() {
        return parqueaderoService.obtenerActivos();
    }

    public Map<String, Object> obtenerEstadisticas() {
        return parqueaderoService.obtenerEstadisticas();
    }

    public RegistroParqueadero ingresarVehiculo(String placa, String tipo, String propietario, String observaciones) {
        return parqueaderoService.registrarIngreso(placa, tipo, propietario, observaciones);
    }

    public RegistroParqueadero marcarSalida(int idRegistro) {
        return parqueaderoService.registrarSalida(idRegistro);
    }
}
