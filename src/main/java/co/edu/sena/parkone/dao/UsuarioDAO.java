package co.edu.sena.parkone.dao;

import co.edu.sena.parkone.conexion.ConexionBD;
import co.edu.sena.parkone.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Metodo para insertar un nuevo usuario
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (identificacion, nombres, apellidos, correo, celular, contrasena, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getIdentificacion());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getCelular());
            ps.setString(6, u.getContrasena());
            ps.setString(7, u.getRol());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // Metodo para listar todos los usuarios
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setIdentificacion(rs.getString("identificacion"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                u.setCelular(rs.getString("celular"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(rs.getString("rol"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    // Metodo para buscar usuario por cedula
    public Usuario buscarPorCedula(String cedula) {
        String sql = "SELECT * FROM usuario WHERE identificacion = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setIdentificacion(rs.getString("identificacion"));
                    u.setNombres(rs.getString("nombres"));
                    u.setApellidos(rs.getString("apellidos"));
                    u.setCorreo(rs.getString("correo"));
                    u.setCelular(rs.getString("celular"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar: " + e.getMessage());
        }
        return null;
    }

    // Metodo para buscar usuario por id
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setIdentificacion(rs.getString("identificacion"));
                    u.setNombres(rs.getString("nombres"));
                    u.setApellidos(rs.getString("apellidos"));
                    u.setCorreo(rs.getString("correo"));
                    u.setCelular(rs.getString("celular"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar por id: " + e.getMessage());
        }
        return null;
    }

    // Metodo para modificar los datos de un usuario
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET identificacion=?, nombres=?, apellidos=?, correo=?, celular=?, rol=? WHERE id_usuario=?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getIdentificacion());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getCelular());
            ps.setString(6, u.getRol());
            ps.setInt(7, u.getIdUsuario());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // Metodo para eliminar un usuario
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}
