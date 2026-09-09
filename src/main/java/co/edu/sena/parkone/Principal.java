package co.edu.sena.parkone;

import co.edu.sena.parkone.vista.FormularioUsuario;
import co.edu.sena.parkone.vista.MenuConsola;

import java.awt.GraphicsEnvironment;

public class Principal {

    public static void main(String[] args) {
        // Si no hay pantalla o se pasa el parametro --consola, abre el menu por consola
        if (args.length > 0 && "--consola".equals(args[0]) || GraphicsEnvironment.isHeadless()) {
            MenuConsola menu = new MenuConsola();
            menu.mostrarMenu();
        } else {
            // Abre el formulario visual
            FormularioUsuario form = new FormularioUsuario();
            form.setVisible(true);
        }
    }
}
