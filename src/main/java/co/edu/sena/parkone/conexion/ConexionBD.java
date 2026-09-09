package co.edu.sena.parkone.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Datos de conexion a MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/parkone_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    // Metodo para conectar con la base de datos
    public static Connection getConexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontro el driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al conectar con MySQL: " + e.getMessage());
            System.out.println("Verifique que MySQL este iniciado en XAMPP o MySQL Workbench.");
        }
        return con;
    }
}
