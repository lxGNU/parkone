package co.edu.sena.parkone.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Clase para la conexion a MySQL
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/parkone_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            // Driver no encontrado
        }
    }

    // Metodo para conectar a la base de datos
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
