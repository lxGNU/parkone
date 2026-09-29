package co.edu.sena.parkone.repository;

import co.edu.sena.parkone.model.Vehiculo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Repositorio para gestionar la informacion de vehiculos en el sistema
public class VehiculoRepository {

    private final List<Vehiculo> listaVehiculos = new ArrayList<>();
    private final AtomicInteger contadorId = new AtomicInteger(1);

    public VehiculoRepository() {
        // Inicialmente vacio para que el usuario registre los vehiculos
    }

    // Listar todos los vehiculos
    public List<Vehiculo> listarTodos() {
        return new ArrayList<>(listaVehiculos);
    }

    // Buscar vehiculo por su id
    public Vehiculo buscarPorId(int id) {
        return listaVehiculos.stream()
                .filter(v -> v.getIdVehiculo() == id)
                .findFirst()
                .orElse(null);
    }

    // Buscar vehiculo por la placa
    public Vehiculo buscarPorPlaca(String placa) {
        if (placa == null) return null;
        return listaVehiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa.trim()))
                .findFirst()
                .orElse(null);
    }

    // Buscar por placa o cedula
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

    // Guardar nuevo vehiculo
    public Vehiculo guardar(Vehiculo vehiculo) {
        if (vehiculo.getIdVehiculo() == 0) {
            vehiculo.setIdVehiculo(contadorId.getAndIncrement());
        }
        listaVehiculos.add(vehiculo);
        return vehiculo;
    }

    // Cambiar estado del vehiculo (Estacionado / Retirado)
    public boolean cambiarEstado(int id, String nuevoEstado) {
        Vehiculo v = buscarPorId(id);
        if (v != null) {
            v.setEstado(nuevoEstado);
            return true;
        }
        return false;
    }

    // Eliminar vehiculo por id
    public boolean eliminar(int id) {
        return listaVehiculos.removeIf(v -> v.getIdVehiculo() == id);
    }
}
