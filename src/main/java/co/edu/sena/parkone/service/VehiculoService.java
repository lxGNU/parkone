package co.edu.sena.parkone.service;

import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.repository.VehiculoRepository;
import java.util.List;

// Servicio de negocio para la gestion de vehiculos
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    // Listar todos los vehiculos
    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.listarTodos();
    }

    // Buscar vehiculo por id
    public Vehiculo obtenerPorId(int id) {
        return vehiculoRepository.buscarPorId(id);
    }

    // Buscar vehiculo por placa
    public Vehiculo obtenerPorPlaca(String placa) {
        return vehiculoRepository.buscarPorPlaca(placa);
    }

    // Buscar con filtro
    public List<Vehiculo> buscar(String filtro) {
        return vehiculoRepository.buscarPorPlacaOCedula(filtro);
    }

    // Registrar o actualizar vehiculo
    public Vehiculo guardar(Vehiculo vehiculo) {
        // Asignar tarifa por defecto segun tipo de vehiculo si no se especifica
        if (vehiculo.getTarifaPorHora() <= 0) {
            vehiculo.setTarifaPorHora(obtenerTarifaSegunTipo(vehiculo.getTipo()));
        }
        return vehiculoRepository.guardar(vehiculo);
    }

    // Cambiar estado
    public boolean cambiarEstado(int id, String nuevoEstado) {
        return vehiculoRepository.cambiarEstado(id, nuevoEstado);
    }

    // Eliminar vehiculo
    public boolean eliminar(int id) {
        return vehiculoRepository.eliminar(id);
    }

    // Calcular tarifa segun el tipo de vehiculo
    public double obtenerTarifaSegunTipo(String tipo) {
        if (tipo == null) return 4000.0;
        switch (tipo.trim().toLowerCase()) {
            case "motocicleta":
            case "moto":
                return 2000.0;
            case "camioneta":
            case "pesado":
                return 6000.0;
            case "automóvil":
            case "automovil":
            case "auto":
            default:
                return 4000.0;
        }
    }
}
