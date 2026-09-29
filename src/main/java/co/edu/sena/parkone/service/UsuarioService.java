package co.edu.sena.parkone.service;

import co.edu.sena.parkone.model.Usuario;
import co.edu.sena.parkone.repository.UsuarioRepository;
import java.util.List;

// Servicio de negocio para la gestion de usuarios
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.listarTodos();
    }

    // Buscar por ID
    public Usuario obtenerPorId(int id) {
        return usuarioRepository.buscarPorId(id);
    }

    // Validar inicio de sesion
    public Usuario login(String usuario, String clave) {
        return usuarioRepository.validarLogin(usuario, clave);
    }

    // Buscar usuarios con filtro
    public List<Usuario> buscar(String criterio) {
        return usuarioRepository.buscarPorCriterio(criterio);
    }

    // Registrar un usuario nuevo
    public Usuario registrar(Usuario usuario) {
        return usuarioRepository.guardar(usuario);
    }

    // Actualizar usuario existente
    public boolean actualizar(Usuario usuario) {
        return usuarioRepository.actualizar(usuario);
    }

    // Eliminar usuario
    public boolean eliminar(int id) {
        return usuarioRepository.eliminar(id);
    }
}
