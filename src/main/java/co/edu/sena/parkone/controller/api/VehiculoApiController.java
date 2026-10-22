package co.edu.sena.parkone.controller.api;

import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.service.VehiculoService;
import java.util.List;

// API de vehiculos
public class VehiculoApiController {

    private final VehiculoService vehiculoService;

    public VehiculoApiController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    public List<Vehiculo> listarVehiculos() {
        return vehiculoService.obtenerTodos();
    }

    public Vehiculo obtenerPorPlaca(String placa) {
        return vehiculoService.obtenerPorPlaca(placa);
    }
}
