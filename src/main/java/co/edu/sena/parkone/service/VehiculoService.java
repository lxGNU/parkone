package co.edu.sena.parkone.service;

import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.repository.VehiculoRepository;
import java.util.List;

// Logica de vehiculos
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.listarTodos();
    }

    public Vehiculo obtenerPorId(int id) {
        return vehiculoRepository.buscarPorId(id);
    }

    public Vehiculo obtenerPorPlaca(String placa) {
        return vehiculoRepository.buscarPorPlaca(placa);
    }

    public List<Vehiculo> buscar(String filtro) {
        return vehiculoRepository.buscarPorPlacaOCedula(filtro);
    }

    public Vehiculo guardar(Vehiculo vehiculo) {
        if (vehiculo.getTarifaPorHora() <= 0) {
            vehiculo.setTarifaPorHora(obtenerTarifaSegunTipo(vehiculo.getTipo()));
        }
        return vehiculoRepository.guardar(vehiculo);
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        return vehiculoRepository.cambiarEstado(id, nuevoEstado);
    }

    public boolean eliminar(int id) {
        return vehiculoRepository.eliminar(id);
    }

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
