package co.edu.sena.parkone;

import co.edu.sena.parkone.server.ServidorWeb;

// Inicio de la app
public class ParkOneApplication {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Iniciando Proyecto ParkOne - Modulos Framework ");
        System.out.println(" Aprendiz: Brandon Yair Galvis Diaz");
        System.out.println(" Ficha: 3336118 - SENA Regional Cauca");
        System.out.println(" Evidencia: GA7-220501096-AA3-EV02");
        System.out.println("=================================================");

        try {
            // Iniciar servidor
            ServidorWeb.iniciar(8080);
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor ParkOne: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
