package co.edu.sena.parkone;

import co.edu.sena.parkone.model.RegistroParqueadero;
import co.edu.sena.parkone.model.Usuario;
import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.repository.RegistroParqueaderoRepository;
import co.edu.sena.parkone.repository.UsuarioRepository;
import co.edu.sena.parkone.repository.VehiculoRepository;
import co.edu.sena.parkone.service.ParqueaderoService;
import co.edu.sena.parkone.service.UsuarioService;
import co.edu.sena.parkone.service.VehiculoService;

import java.time.LocalDateTime;
import java.util.Map;

// Pruebas del sistema
public class PruebasModulosTest {

    private static int pruebasExitosas = 0;
    private static int pruebasTotales = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  SENA ADSO - PRUEBAS DE MODULOS Y VALIDACIONES ");
        System.out.println("  Evidencia: GA7-220501096-AA3-EV02");
        System.out.println("  Aprendiz: Brandon Yair Galvis Diaz");
        System.out.println("==========================================================\n");

        UsuarioRepository usuarioRepo = new UsuarioRepository();
        VehiculoRepository vehiculoRepo = new VehiculoRepository();
        RegistroParqueaderoRepository registroRepo = new RegistroParqueaderoRepository();

        UsuarioService usuarioService = new UsuarioService(usuarioRepo);
        VehiculoService vehiculoService = new VehiculoService(vehiculoRepo);
        ParqueaderoService parqueaderoService = new ParqueaderoService(registroRepo, vehiculoService);

        test("1. Autenticacion con credenciales correctas", () -> {
            Usuario u = usuarioService.login("1061723849", "123456");
            return u != null && "Brandon Yair".equals(u.getNombres());
        });

        test("2. Rechazo de login con contrasena incorrecta", () -> {
            Usuario u = usuarioService.login("1061723849", "clave_mala");
            return u == null;
        });

        test("3. Validacion de tarifas numericas por categoria", () -> {
            double tarifaMoto = vehiculoService.obtenerTarifaSegunTipo("Motocicleta");
            double tarifaAuto = vehiculoService.obtenerTarifaSegunTipo("Automóvil");
            double tarifaCamioneta = vehiculoService.obtenerTarifaSegunTipo("Camioneta");
            return tarifaMoto == 2000.0 && tarifaAuto == 4000.0 && tarifaCamioneta == 6000.0;
        });

        test("4. Registro de vehiculo y datos de texto", () -> {
            Vehiculo v = new Vehiculo("KRF-502", "Automóvil", "Mazda", "Rojo", "1061723849", "Brandon Galvis", "Estacionado", 4000.0);
            vehiculoService.guardar(v);
            Vehiculo buscado = vehiculoService.obtenerPorPlaca("krf-502");
            return buscado != null && "Mazda".equals(buscado.getMarca());
        });

        test("5. Validacion de fecha y hora automatica de ingreso", () -> {
            RegistroParqueadero reg = parqueaderoService.registrarIngreso("ABC-123", "Automóvil", "Carlos Perez", "Bahia 2");
            LocalDateTime ahora = LocalDateTime.now();
            return reg != null && reg.getFechaEntrada() != null && reg.getFechaEntrada().getYear() == ahora.getYear() && "ACTIVO".equals(reg.getEstado());
        });

        test("6. Validacion de restriccion: no permitir placa duplicada activa", () -> {
            try {
                parqueaderoService.registrarIngreso("ABC-123", "Automóvil", "Carlos Perez", "Intento duplicado");
                return false;
            } catch (IllegalArgumentException e) {
                return true;
            }
        });

        test("7. Liquidacion de salida y cobro de tarifa minima", () -> {
            RegistroParqueadero activo = registroRepo.buscarActivoPorPlaca("ABC-123");
            RegistroParqueadero salida = parqueaderoService.registrarSalida(activo.getIdRegistro());
            return "FINALIZADO".equals(salida.getEstado()) && salida.getHorasCobradas() >= 1 && salida.getTotalPagar() >= 4000.0;
        });

        test("8. Calculo de cupos y estadisticas del parqueadero", () -> {
            Map<String, Object> stats = parqueaderoService.obtenerEstadisticas();
            int cuposOcupados = (int) stats.get("cuposOcupados");
            int cuposDisponibles = (int) stats.get("cuposDisponibles");
            int capacidadTotal = (int) stats.get("capacidadMaxima");
            return (cuposOcupados + cuposDisponibles) == capacidadTotal;
        });

        System.out.println("==========================================================");
        System.out.println("  RESUMEN DE PRUEBAS:");
        System.out.println("  Pruebas ejecutadas: " + pruebasTotales);
        System.out.println("  Pruebas superadas:  " + pruebasExitosas);
        System.out.println("  Estado: " + (pruebasExitosas == pruebasTotales ? "TODAS LAS PRUEBAS PASARON EXITOSAMENTE (100%)" : "HUBO FALLAS"));
        System.out.println("==========================================================");
    }

    private static void test(String nombre, TestRunner runner) {
        pruebasTotales++;
        try {
            boolean resultado = runner.run();
            if (resultado) {
                pruebasExitosas++;
                System.out.println("[PASÓ] " + nombre);
            } else {
                System.out.println("[FALLÓ] " + nombre);
            }
        } catch (Exception e) {
            System.out.println("[FALLÓ] " + nombre + " - Error: " + e.getMessage());
        }
    }

    @FunctionalInterface
    interface TestRunner {
        boolean run() throws Exception;
    }
}
