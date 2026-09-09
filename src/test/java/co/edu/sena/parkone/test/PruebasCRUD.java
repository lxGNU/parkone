package co.edu.sena.parkone.test;

import co.edu.sena.parkone.dao.UsuarioDAO;
import co.edu.sena.parkone.modelo.Usuario;

import java.util.List;

public class PruebasCRUD {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DEL CRUD - MODULO USUARIOS ===");
        UsuarioDAO dao = new UsuarioDAO();

        // 1. Probar insercion
        System.out.println("\n1. Probando insercion de usuario...");
        Usuario nuevo = new Usuario("1098765432", "Pedro", "Gomez", "pedro@gmail.com", "3114567890", "123456", "Operador");
        if (dao.insertar(nuevo)) {
            System.out.println("-> Usuario insertado con exito.");
        } else {
            System.out.println("-> No se pudo insertar (revise la conexion o si la cedula ya existe).");
        }

        // 2. Probar consulta
        System.out.println("\n2. Probando consulta por cedula...");
        Usuario buscado = dao.buscarPorCedula("1098765432");
        if (buscado != null) {
            System.out.println("-> Usuario encontrado: " + buscado.getNombreCompleto() + " - Correo: " + buscado.getCorreo());
        } else {
            System.out.println("-> Usuario no encontrado.");
        }

        // 2.1 Listar todos
        System.out.println("\n2.1 Listando todos los usuarios...");
        List<Usuario> lista = dao.listar();
        for (Usuario u : lista) {
            System.out.println(" - " + u.getIdentificacion() + " | " + u.getNombreCompleto() + " (" + u.getRol() + ")");
        }

        // 3. Probar actualizacion
        System.out.println("\n3. Probando actualizacion...");
        if (buscado != null) {
            buscado.setCelular("3209876543");
            buscado.setRol("Administrador");
            if (dao.actualizar(buscado)) {
                System.out.println("-> Datos actualizados con exito.");
            } else {
                System.out.println("-> Error al actualizar.");
            }
        }

        // 4. Probar eliminacion
        System.out.println("\n4. Probando eliminacion...");
        if (buscado != null) {
            if (dao.eliminar(buscado.getIdUsuario())) {
                System.out.println("-> Usuario eliminado con exito.");
            } else {
                System.out.println("-> Error al eliminar.");
            }
        }

        System.out.println("\n=== FIN DE LAS PRUEBAS ===");
    }
}
