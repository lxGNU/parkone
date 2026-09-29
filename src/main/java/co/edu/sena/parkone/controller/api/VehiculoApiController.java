package co.edu.sena.parkone.controller.api;

import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.service.VehiculoService;
import java.util.List;

// Controlador API REST para consultar vehiculos desde aplicaciones moviles
public class VehiculoApiController {

    private final VehiculoService vehiculoService;

    public VehiculoApiController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    // Retorna todos los vehiculos en formato JSON
    public List<Vehiculo> listarVehiculos() {
        return vehiculoService.obtenerTodos();
    }

    // Buscar un vehiculo por su placa
    public Vehiculo obtenerPorPlaca(String placa) {
        return vehiculoService.obtenerPorPlaca(placa);
    }
}
