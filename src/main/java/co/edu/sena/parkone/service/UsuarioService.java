package co.edu.sena.parkone.service;

import co.edu.sena.parkone.model.Usuario;
import co.edu.sena.parkone.repository.UsuarioRepository;
import java.util.List;

// Logica de usuarios
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.listarTodos();
    }

    public Usuario obtenerPorId(int id) {
        return usuarioRepository.buscarPorId(id);
    }

    public Usuario login(String usuario, String clave) {
        return usuarioRepository.validarLogin(usuario, clave);
    }

    public List<Usuario> buscar(String criterio) {
        return usuarioRepository.buscarPorCriterio(criterio);
    }

    public Usuario registrar(Usuario usuario) {
        return usuarioRepository.guardar(usuario);
    }

    public boolean actualizar(Usuario usuario) {
        return usuarioRepository.actualizar(usuario);
    }

    public boolean eliminar(int id) {
        return usuarioRepository.eliminar(id);
    }
}
