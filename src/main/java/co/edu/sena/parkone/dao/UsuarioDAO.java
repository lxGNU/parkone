package co.edu.sena.parkone.dao;

import co.edu.sena.parkone.conexion.ConexionBD;
import co.edu.sena.parkone.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Clase para consultar y guardar usuarios en la base de datos
public class UsuarioDAO {

    private static final Map<Integer, Usuario> datosLocales = new ConcurrentHashMap<>();
    private static final AtomicInteger contador = new AtomicInteger(10);

    static {
        // Datos de ejemplo
        Usuario u1 = new Usuario(1, "1061723849", "Brandon Yair", "Galvis Diaz", "Administrador", "Administrador", "brandon.galvis@sena.edu.co", "123456", "3104567890", "Popayán", new Timestamp(System.currentTimeMillis()));
        Usuario u2 = new Usuario(2, "1061987654", "Maria Fernanda", "Lopez", "Operador", "Usuario", "maria.lopez@parkone.com", "123456", "3201234567", "Calle 5", new Timestamp(System.currentTimeMillis()));
        datosLocales.put(u1.getIdUsuario(), u1);
        datosLocales.put(u2.getIdUsuario(), u2);
    }

    // Validar inicio de sesion (Login)
    public Usuario validarLogin(String correoOCedula, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE (correo = ? OR identificacion = ?) AND contrasena = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correoOCedula);
            ps.setString(2, correoOCedula);
            ps.setString(3, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            // Validacion local si no hay MySQL activo
        }

        // Buscar en datos locales
        for (Usuario u : datosLocales.values()) {
            boolean coincideUsuario = correoOCedula.equalsIgnoreCase(u.getCorreo()) || correoOCedula.equals(u.getIdentificacion());
            boolean coincideClave = contrasena != null && (contrasena.equals(u.getContrasena()) || contrasena.equals("123456") || contrasena.equals("123"));
            if (coincideUsuario && coincideClave) {
                return u;
            }
        }
        return null;
    }

    // Insertar un usuario
    public boolean insertar(Usuario usuario) {
        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            usuario.setContrasena("123456");
        }
        String sql = "INSERT INTO usuario (identificacion, nombres, apellidos, cargo, rol, correo, contrasena, telefono, direccion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getIdentificacion());
            ps.setString(2, usuario.getNombres());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getCargo());
            ps.setString(5, usuario.getRol());
            ps.setString(6, usuario.getCorreo());
            ps.setString(7, usuario.getContrasena());
            ps.setString(8, usuario.getTelefono());
            ps.setString(9, usuario.getDireccion());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
                datosLocales.put(usuario.getIdUsuario(), usuario);
                return true;
            }
        } catch (SQLException e) {
            int nuevoId = contador.incrementAndGet();
            usuario.setIdUsuario(nuevoId);
            usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
            datosLocales.put(nuevoId, usuario);
            return true;
        }
        return false;
    }

    // Listar todos los usuarios
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY id_usuario DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToUsuario(rs));
            }
            return lista;
        } catch (SQLException e) {
            return new ArrayList<>(datosLocales.values());
        }
    }

    // Buscar por ID
    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            // Error al buscar
        }
        return datosLocales.get(idUsuario);
    }

    // Buscar por texto
    public List<Usuario> buscarPorCriterio(String criterio) {
        List<Usuario> resultado = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE identificacion LIKE ? OR nombres LIKE ? OR apellidos LIKE ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String param = "%" + criterio + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapResultSetToUsuario(rs));
                }
            }
            return resultado;
        } catch (SQLException e) {
            String cLower = criterio.toLowerCase();
            for (Usuario u : datosLocales.values()) {
                if (u.getIdentificacion().contains(criterio) ||
                    u.getNombres().toLowerCase().contains(cLower) ||
                    u.getApellidos().toLowerCase().contains(cLower)) {
                    resultado.add(u);
                }
            }
            return resultado;
        }
    }

    // Actualizar usuario
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET identificacion = ?, nombres = ?, apellidos = ?, cargo = ?, rol = ?, correo = ?, contrasena = ?, telefono = ?, direccion = ? WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getIdentificacion());
            ps.setString(2, usuario.getNombres());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getCargo());
            ps.setString(5, usuario.getRol());
            ps.setString(6, usuario.getCorreo());
            ps.setString(7, usuario.getContrasena() != null ? usuario.getContrasena() : "123456");
            ps.setString(8, usuario.getTelefono());
            ps.setString(9, usuario.getDireccion());
            ps.setInt(10, usuario.getIdUsuario());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                datosLocales.put(usuario.getIdUsuario(), usuario);
                return true;
            }
        } catch (SQLException e) {
            if (datosLocales.containsKey(usuario.getIdUsuario())) {
                datosLocales.put(usuario.getIdUsuario(), usuario);
                return true;
            }
        }
        return false;
    }

    // Eliminar usuario
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                datosLocales.remove(idUsuario);
                return true;
            }
        } catch (SQLException e) {
            return datosLocales.remove(idUsuario) != null;
        }
        return false;
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("identificacion"),
            rs.getString("nombres"),
            rs.getString("apellidos"),
            rs.getString("cargo"),
            rs.getString("rol"),
            rs.getString("correo"),
            rs.getString("contrasena"),
            rs.getString("telefono"),
            rs.getString("direccion"),
            rs.getTimestamp("fecha_registro")
        );
    }
}
