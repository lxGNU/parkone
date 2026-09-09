package co.edu.sena.parkone.vista;

import co.edu.sena.parkone.dao.UsuarioDAO;
import co.edu.sena.parkone.modelo.Usuario;

import java.util.List;
import java.util.Scanner;

public class MenuConsola {

    private UsuarioDAO dao;
    private Scanner entrada;

    public MenuConsola() {
        dao = new UsuarioDAO();
        entrada = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion = -1;

        do {
            System.out.println("\n--- MENU DE USUARIOS PARKONE ---");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Listar usuarios");
            System.out.println("3. Buscar usuario por cedula");
            System.out.println("4. Modificar usuario");
            System.out.println("5. Eliminar usuario");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = Integer.parseInt(entrada.nextLine().trim());
            } catch (Exception e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    registrar();
                    break;
                case 2:
                    listar();
                    break;
                case 3:
                    buscar();
                    break;
                case 4:
                    modificar();
                    break;
                case 5:
                    eliminar();
                    break;
                case 0:
                    System.out.println("Saliendo del programa.");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 0);
    }

    private void registrar() {
        System.out.println("\n--- Registrar usuario ---");
        System.out.print("Cedula: ");
        String cedula = entrada.nextLine().trim();
        System.out.print("Nombres: ");
        String nombres = entrada.nextLine().trim();
        System.out.print("Apellidos: ");
        String apellidos = entrada.nextLine().trim();
        System.out.print("Correo: ");
        String correo = entrada.nextLine().trim();
        System.out.print("Celular: ");
        String celular = entrada.nextLine().trim();
        System.out.print("Contrasena: ");
        String clave = entrada.nextLine().trim();
        System.out.print("Rol (Administrador, Operador, Usuario): ");
        String rol = entrada.nextLine().trim();

        Usuario u = new Usuario(cedula, nombres, apellidos, correo, celular, clave, rol);
        if (dao.insertar(u)) {
            System.out.println("Usuario guardado con exito!");
        } else {
            System.out.println("No se pudo guardar el usuario.");
        }
    }

    private void listar() {
        System.out.println("\n--- Lista de usuarios ---");
        List<Usuario> lista = dao.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario u : lista) {
            System.out.println("ID: " + u.getIdUsuario() + " | Cedula: " + u.getIdentificacion() + " | Nombre: " + u.getNombreCompleto() + " | Rol: " + u.getRol());
        }
    }

    private void buscar() {
        System.out.println("\n--- Buscar usuario ---");
        System.out.print("Ingrese la cedula: ");
        String cedula = entrada.nextLine().trim();
        Usuario u = dao.buscarPorCedula(cedula);
        if (u != null) {
            System.out.println("Usuario encontrado: " + u.getNombreCompleto() + " (" + u.getRol() + ") - Correo: " + u.getCorreo());
        } else {
            System.out.println("No se encontro ningun usuario con esa cedula.");
        }
    }

    private void modificar() {
        System.out.println("\n--- Modificar usuario ---");
        System.out.print("Ingrese el ID del usuario: ");
        try {
            int id = Integer.parseInt(entrada.nextLine().trim());
            Usuario u = dao.buscarPorId(id);
            if (u == null) {
                System.out.println("Usuario no encontrado.");
                return;
            }

            System.out.print("Nuevo nombre [" + u.getNombres() + "]: ");
            String nom = entrada.nextLine().trim();
            if (!nom.isEmpty()) u.setNombres(nom);

            System.out.print("Nuevo celular [" + u.getCelular() + "]: ");
            String cel = entrada.nextLine().trim();
            if (!cel.isEmpty()) u.setCelular(cel);

            System.out.print("Nuevo rol [" + u.getRol() + "]: ");
            String rol = entrada.nextLine().trim();
            if (!rol.isEmpty()) u.setRol(rol);

            if (dao.actualizar(u)) {
                System.out.println("Usuario modificado correctamente!");
            } else {
                System.out.println("No se pudo actualizar.");
            }
        } catch (Exception e) {
            System.out.println("ID no valido.");
        }
    }

    private void eliminar() {
        System.out.println("\n--- Eliminar usuario ---");
        System.out.print("Ingrese el ID del usuario a eliminar: ");
        try {
            int id = Integer.parseInt(entrada.nextLine().trim());
            if (dao.eliminar(id)) {
                System.out.println("Usuario eliminado con exito.");
            } else {
                System.out.println("No se pudo eliminar.");
            }
        } catch (Exception e) {
            System.out.println("ID no valido.");
        }
    }
}
