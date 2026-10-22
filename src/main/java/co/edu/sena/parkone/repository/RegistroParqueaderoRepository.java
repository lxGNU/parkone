package co.edu.sena.parkone.repository;

import co.edu.sena.parkone.model.RegistroParqueadero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Datos de parqueadero
public class RegistroParqueaderoRepository {

    private final List<RegistroParqueadero> listaRegistros = new ArrayList<>();
    private final AtomicInteger contadorId = new AtomicInteger(1);

    public RegistroParqueaderoRepository() {
    }

    public List<RegistroParqueadero> listarTodos() {
        return new ArrayList<>(listaRegistros);
    }

    public List<RegistroParqueadero> listarActivos() {
        return listaRegistros.stream()
                .filter(r -> "ACTIVO".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());
    }

    public RegistroParqueadero buscarPorId(int id) {
        return listaRegistros.stream()
                .filter(r -> r.getIdRegistro() == id)
                .findFirst()
                .orElse(null);
    }

    public RegistroParqueadero buscarActivoPorPlaca(String placa) {
        if (placa == null) return null;
        return listaRegistros.stream()
                .filter(r -> "ACTIVO".equalsIgnoreCase(r.getEstado()) && r.getPlacaVehiculo().equalsIgnoreCase(placa.trim()))
                .findFirst()
                .orElse(null);
    }

    public List<RegistroParqueadero> buscarPorFiltro(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return listarTodos();
        }
        String f = filtro.toLowerCase().trim();
        return listaRegistros.stream()
                .filter(r -> r.getPlacaVehiculo().toLowerCase().contains(f) ||
                             r.getPropietarioNombre().toLowerCase().contains(f) ||
                             r.getTipoVehiculo().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

    public RegistroParqueadero guardar(RegistroParqueadero registro) {
        if (registro.getIdRegistro() == 0) {
            registro.setIdRegistro(contadorId.getAndIncrement());
        }
        listaRegistros.add(registro);
        return registro;
    }

    public boolean actualizar(RegistroParqueadero registroActualizado) {
        for (int i = 0; i < listaRegistros.size(); i++) {
            if (listaRegistros.get(i).getIdRegistro() == registroActualizado.getIdRegistro()) {
                listaRegistros.set(i, registroActualizado);
                return true;
            }
        }
        return false;
    }
}
