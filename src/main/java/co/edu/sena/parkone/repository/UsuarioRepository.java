package co.edu.sena.parkone.repository;

import co.edu.sena.parkone.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Datos de usuarios
public class UsuarioRepository {

    private final List<Usuario> listaUsuarios = new ArrayList<>();
    private final AtomicInteger contadorId = new AtomicInteger(1);

    public UsuarioRepository() {
        // Admin inicial
        guardar(new Usuario("1061723849", "Brandon Yair", "Galvis Diaz", "Administrador de Sistema", "Administrador", "brandon.galvis@sena.edu.co", "123456", "3124567890", "Popayán, Cauca"));
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(listaUsuarios);
    }

    public Usuario buscarPorId(int id) {
        return listaUsuarios.stream()
                .filter(u -> u.getIdUsuario() == id)
                .findFirst()
                .orElse(null);
    }

    public Usuario validarLogin(String usuarioOCorreo, String clave) {
        return listaUsuarios.stream()
                .filter(u -> (u.getCorreo().equalsIgnoreCase(usuarioOCorreo) || u.getIdentificacion().equalsIgnoreCase(usuarioOCorreo)) && u.getContrasena().equals(clave))
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> buscarPorCriterio(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarTodos();
        }
        String c = criterio.toLowerCase().trim();
        return listaUsuarios.stream()
                .filter(u -> u.getIdentificacion().toLowerCase().contains(c) ||
                             u.getNombres().toLowerCase().contains(c) ||
                             u.getApellidos().toLowerCase().contains(c) ||
                             u.getCorreo().toLowerCase().contains(c))
                .collect(Collectors.toList());
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getIdUsuario() == 0) {
            usuario.setIdUsuario(contadorId.getAndIncrement());
        }
        listaUsuarios.add(usuario);
        return usuario;
    }

    public boolean actualizar(Usuario usuarioActualizado) {
        for (int i = 0; i < listaUsuarios.size(); i++) {
            if (listaUsuarios.get(i).getIdUsuario() == usuarioActualizado.getIdUsuario()) {
                listaUsuarios.set(i, usuarioActualizado);
                return true;
            }
        }
        return false;
    }

    public boolean eliminar(int id) {
        return listaUsuarios.removeIf(u -> u.getIdUsuario() == id);
    }
}
