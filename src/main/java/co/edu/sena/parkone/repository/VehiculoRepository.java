package co.edu.sena.parkone.repository;

import co.edu.sena.parkone.model.Vehiculo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Datos de vehiculos
public class VehiculoRepository {

    private final List<Vehiculo> listaVehiculos = new ArrayList<>();
    private final AtomicInteger contadorId = new AtomicInteger(1);

    public VehiculoRepository() {
    }

    public List<Vehiculo> listarTodos() {
        return new ArrayList<>(listaVehiculos);
    }

    public Vehiculo buscarPorId(int id) {
        return listaVehiculos.stream()
                .filter(v -> v.getIdVehiculo() == id)
                .findFirst()
                .orElse(null);
    }

    public Vehiculo buscarPorPlaca(String placa) {
        if (placa == null) return null;
        return listaVehiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa.trim()))
                .findFirst()
                .orElse(null);
    }

    public List<Vehiculo> buscarPorPlacaOCedula(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return listarTodos();
        }
        String f = filtro.toLowerCase().trim();
        return listaVehiculos.stream()
                .filter(v -> v.getPlaca().toLowerCase().contains(f) ||
                             v.getPropietarioCedula().toLowerCase().contains(f) ||
                             v.getPropietarioNombre().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

    public Vehiculo guardar(Vehiculo vehiculo) {
        if (vehiculo.getIdVehiculo() == 0) {
            vehiculo.setIdVehiculo(contadorId.getAndIncrement());
        }
        listaVehiculos.add(vehiculo);
        return vehiculo;
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        Vehiculo v = buscarPorId(id);
        if (v != null) {
            v.setEstado(nuevoEstado);
            return true;
        }
        return false;
    }

    public boolean eliminar(int id) {
        return listaVehiculos.removeIf(v -> v.getIdVehiculo() == id);
    }
}
