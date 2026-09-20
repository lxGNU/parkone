package co.edu.sena.parkone.dao;

import co.edu.sena.parkone.conexion.ConexionBD;
import co.edu.sena.parkone.modelo.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Clase para registrar y listar los vehiculos
public class VehiculoDAO {

    private static final Map<Integer, Vehiculo> datosLocales = new ConcurrentHashMap<>();
    private static final AtomicInteger contador = new AtomicInteger(10);

    static {
        Vehiculo v1 = new Vehiculo(1, "ABC-123", "Automóvil", "Toyota", "Gris", "1061723849", "Brandon Yair Galvis Diaz", "Estacionado", 4000.00, new Timestamp(System.currentTimeMillis()), null);
        datosLocales.put(v1.getIdVehiculo(), v1);
    }

    // Insertar un vehiculo
    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculo (placa, tipo, marca, color, propietario_cedula, propietario_nombre, estado, tarifa_por_hora) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getTipo());
            ps.setString(3, vehiculo.getMarca());
            ps.setString(4, vehiculo.getColor());
            ps.setString(5, vehiculo.getPropietarioCedula());
            ps.setString(6, vehiculo.getPropietarioNombre());
            ps.setString(7, vehiculo.getEstado());
            ps.setDouble(8, vehiculo.getTarifaPorHora());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    vehiculo.setIdVehiculo(rs.getInt(1));
                }
                datosLocales.put(vehiculo.getIdVehiculo(), vehiculo);
                return true;
            }
        } catch (SQLException e) {
            int nuevoId = contador.incrementAndGet();
            vehiculo.setIdVehiculo(nuevoId);
            vehiculo.setFechaIngreso(new Timestamp(System.currentTimeMillis()));
            datosLocales.put(nuevoId, vehiculo);
            return true;
        }
        return false;
    }

    // Listar todos los vehiculos
    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo ORDER BY id_vehiculo DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToVehiculo(rs));
            }
            return lista;
        } catch (SQLException e) {
            return new ArrayList<>(datosLocales.values());
        }
    }

    // Buscar por id
    public Vehiculo buscarPorId(int idVehiculo) {
        String sql = "SELECT * FROM vehiculo WHERE id_vehiculo = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehiculo(rs);
                }
            }
        } catch (SQLException e) {
            // Error al buscar
        }
        return datosLocales.get(idVehiculo);
    }

    // Buscar por placa o cedula
    public List<Vehiculo> buscarPorPlacaOCedula(String filtro) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE placa LIKE ? OR propietario_cedula LIKE ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String p = "%" + filtro + "%";
            ps.setString(1, p);
            ps.setString(2, p);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToVehiculo(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            String fLower = filtro.toLowerCase();
            for (Vehiculo v : datosLocales.values()) {
                if (v.getPlaca().toLowerCase().contains(fLower) || v.getPropietarioCedula().contains(filtro)) {
                    lista.add(v);
                }
            }
            return lista;
        }
    }

    // Cambiar estado de parqueo
    public boolean cambiarEstado(int idVehiculo, String nuevoEstado) {
        String sql = "UPDATE vehiculo SET estado = ?, fecha_salida = CASE WHEN ? = 'Retirado' THEN CURRENT_TIMESTAMP ELSE fecha_salida END WHERE id_vehiculo = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setString(2, nuevoEstado);
            ps.setInt(3, idVehiculo);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                Vehiculo v = datosLocales.get(idVehiculo);
                if (v != null) {
                    v.setEstado(nuevoEstado);
                    if ("Retirado".equalsIgnoreCase(nuevoEstado)) {
                        v.setFechaSalida(new Timestamp(System.currentTimeMillis()));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            Vehiculo v = datosLocales.get(idVehiculo);
            if (v != null) {
                v.setEstado(nuevoEstado);
                if ("Retirado".equalsIgnoreCase(nuevoEstado)) {
                    v.setFechaSalida(new Timestamp(System.currentTimeMillis()));
                }
                return true;
            }
        }
        return false;
    }

    // Eliminar vehiculo
    public boolean eliminar(int idVehiculo) {
        String sql = "DELETE FROM vehiculo WHERE id_vehiculo = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                datosLocales.remove(idVehiculo);
                return true;
            }
        } catch (SQLException e) {
            return datosLocales.remove(idVehiculo) != null;
        }
        return false;
    }

    private Vehiculo mapResultSetToVehiculo(ResultSet rs) throws SQLException {
        return new Vehiculo(
            rs.getInt("id_vehiculo"),
            rs.getString("placa"),
            rs.getString("tipo"),
            rs.getString("marca"),
            rs.getString("color"),
            rs.getString("propietario_cedula"),
            rs.getString("propietario_nombre"),
            rs.getString("estado"),
            rs.getDouble("tarifa_por_hora"),
            rs.getTimestamp("fecha_ingreso"),
            rs.getTimestamp("fecha_salida")
        );
    }
}
