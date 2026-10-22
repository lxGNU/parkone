package co.edu.sena.parkone.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import co.edu.sena.parkone.model.RegistroParqueadero;
import co.edu.sena.parkone.model.Usuario;
import co.edu.sena.parkone.model.Vehiculo;
import co.edu.sena.parkone.repository.RegistroParqueaderoRepository;
import co.edu.sena.parkone.repository.UsuarioRepository;
import co.edu.sena.parkone.repository.VehiculoRepository;
import co.edu.sena.parkone.service.ParqueaderoService;
import co.edu.sena.parkone.service.UsuarioService;
import co.edu.sena.parkone.service.VehiculoService;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

// Servidor Web local para ejecutar los modulos web y los endpoints de la API REST
public class ServidorWeb {

    private static UsuarioRepository usuarioRepo;
    private static VehiculoRepository vehiculoRepo;
    private static RegistroParqueaderoRepository registroRepo;

    private static UsuarioService usuarioService;
    private static VehiculoService vehiculoService;
    private static ParqueaderoService parqueaderoService;

    private static Usuario usuarioSesionActiva = null;

    public static void iniciar(int puerto) throws IOException {
        // Inicializar repositorios y servicios de la arquitectura por capas
        usuarioRepo = new UsuarioRepository();
        vehiculoRepo = new VehiculoRepository();
        registroRepo = new RegistroParqueaderoRepository();

        usuarioService = new UsuarioService(usuarioRepo);
        vehiculoService = new VehiculoService(vehiculoRepo);
        parqueaderoService = new ParqueaderoService(registroRepo, vehiculoService);

        // Usuario por defecto logueado para facilitar la prueba
        usuarioSesionActiva = usuarioService.obtenerPorId(1);

        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);

        // Rutas de Vistas Web Responsivas
        server.createContext("/", new ManejadorInicio());
        server.createContext("/login", new ManejadorLogin());
        server.createContext("/control", new ManejadorControlParqueadero());
        server.createContext("/usuarios", new ManejadorUsuario());
        server.createContext("/vehiculos", new ManejadorVehiculo());
        server.createContext("/css/style.css", new ManejadorArchivos("src/main/resources/static/css/style.css", "text/css"));

        // Rutas de la API REST JSON (Para App Móvil)
        server.createContext("/api/dashboard", new ManejadorApiDashboard());
        server.createContext("/api/vehiculos", new ManejadorApiVehiculos());
        server.createContext("/api/registros", new ManejadorApiRegistros());

        server.setExecutor(null);
        System.out.println("=================================================");
        System.out.println(" Servidor ParkOne iniciado exitosamente");
        System.out.println(" Aprendiz: Brandon Yair Galvis Diaz");
        System.out.println(" Abrir en navegador: http://localhost:" + puerto + "/");
        System.out.println(" Endpoint API Rest:  http://localhost:" + puerto + "/api/dashboard");
        System.out.println("=================================================");
        server.start();
    }

    // Manejador para el Dashboard Principal
    static class ManejadorInicio implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioSesionActiva == null) {
                redirect(exchange, "/login");
                return;
            }

            Map<String, Object> stats = parqueaderoService.obtenerEstadisticas();
            int cuposOcupados = (int) stats.get("cuposOcupados");
            int cuposDisponibles = (int) stats.get("cuposDisponibles");
            int capacidadMaxima = (int) stats.get("capacidadMaxima");
            double recaudo = (double) stats.get("recaudoTotal");

            List<RegistroParqueadero> activos = parqueaderoService.obtenerActivos();

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Panel General de Control</h2>");
            sb.append("<p>Móvil y Web - Sistema de Gestión de Parqueaderos ParkOne</p></div>");

            // Tarjetas de estadisticas
            sb.append("<div class='dashboard-grid'>");
            
            sb.append("  <div class='stat-card blue'>");
            sb.append("    <div class='stat-title'>Capacidad Total</div>");
            sb.append("    <div class='stat-value'>").append(capacidadMaxima).append("</div>");
            sb.append("    <div class='stat-desc'>Bahías de parqueadero</div>");
            sb.append("  </div>");

            sb.append("  <div class='stat-card yellow'>");
            sb.append("    <div class='stat-title'>Vehículos Estacionados</div>");
            sb.append("    <div class='stat-value'>").append(cuposOcupados).append("</div>");
            sb.append("    <div class='stat-desc'>Cupos ocupados en este momento</div>");
            sb.append("  </div>");

            sb.append("  <div class='stat-card green'>");
            sb.append("    <div class='stat-title'>Cupos Libres</div>");
            sb.append("    <div class='stat-value'>").append(cuposDisponibles).append("</div>");
            sb.append("    <div class='stat-desc'>Disponibilidad para ingreso</div>");
            sb.append("  </div>");

            sb.append("  <div class='stat-card purple'>");
            sb.append("    <div class='stat-title'>Recaudo Acumulado</div>");
            sb.append("    <div class='stat-value'>$").append(String.format("%.2f", recaudo)).append("</div>");
            sb.append("    <div class='stat-desc'>Ingresos por concepto de tarifas</div>");
            sb.append("  </div>");

            sb.append("</div>");

            // Accesos rapidos y estado de vehiculos
            sb.append("<div style='display:grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap:1.5rem; margin-top:1.5rem;'>");
            
            // Columna 1: Acceso rapido a modulos
            sb.append("<div class='card-container'><h3>Módulos del Sistema</h3>");
            sb.append("<p style='color:#9ca3af; font-size:0.9rem; margin-bottom:1rem;'>Seleccione el módulo a gestionar:</p>");
            sb.append("<div style='display:flex; flex-direction:column; gap:0.75rem;'>");
            sb.append("  <a href='/control' class='btn btn-primary' style='justify-content:center;'>Entradas, Salidas y Cobro</a>");
            sb.append("  <a href='/vehiculos' class='btn btn-success' style='justify-content:center;'>Gestión de Vehículos</a>");
            sb.append("  <a href='/usuarios' class='btn btn-secondary' style='justify-content:center;'>Gestión de Usuarios</a>");
            sb.append("  <a href='/api/dashboard' target='_blank' class='btn btn-outline' style='justify-content:center;'>Probar API REST (JSON para Móvil)</a>");
            sb.append("</div></div>");

            // Columna 2: Vehiculos en parqueadero en tiempo real
            sb.append("<div class='card-container'><h3>Vehículos Estacionados Ahora</h3>");
            if (activos.isEmpty()) {
                sb.append("<p style='color:#9ca3af; padding:1rem 0;'>No hay vehículos en el parqueadero.</p>");
            } else {
                sb.append("<div class='table-responsive'><table class='data-table'><thead><tr><th>Placa</th><th>Tipo</th><th>Hora Ingreso</th><th>Tarifa/h</th></tr></thead><tbody>");
                for (RegistroParqueadero r : activos) {
                    sb.append("<tr>");
                    sb.append("<td><strong style='color:#60a5fa;'>").append(escapeHtml(r.getPlacaVehiculo())).append("</strong></td>");
                    sb.append("<td>").append(escapeHtml(r.getTipoVehiculo())).append("</td>");
                    sb.append("<td>").append(r.getFechaEntradaFormateada()).append("</td>");
                    sb.append("<td>$").append(String.format("%.0f", r.getTarifaAplicada())).append("</td>");
                    sb.append("</tr>");
                }
                sb.append("</tbody></table></div>");
            }
            sb.append("</div>");

            sb.append("</div>");

            sendResponse(exchange, 200, renderFullLayout("Inicio", sb.toString()));
        }
    }

    // Manejador del Módulo de Control de Parqueadero (Entradas, Salidas y Liquidación)
    static class ManejadorControlParqueadero implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioSesionActiva == null) {
                redirect(exchange, "/login");
                return;
            }

            String method = exchange.getRequestMethod();
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> postParams = parseQueryParams(body);
                String accion = postParams.get("accion");

                if ("ingresar".equalsIgnoreCase(accion)) {
                    try {
                        String placa = postParams.get("placa");
                        String tipo = postParams.get("tipo");
                        String propietario = postParams.get("propietario");
                        String observaciones = postParams.get("observaciones");

                        // Validacion de placa
                        if (placa == null || placa.trim().length() < 5) {
                            redirect(exchange, "/control?accion=nuevo&error=" + URLEncoder.encode("La placa debe tener minimo 5 caracteres.", StandardCharsets.UTF_8));
                            return;
                        }

                        // Validacion de propietario
                        if (propietario == null || propietario.trim().length() < 3) {
                            redirect(exchange, "/control?accion=nuevo&error=" + URLEncoder.encode("El nombre del propietario debe tener minimo 3 caracteres.", StandardCharsets.UTF_8));
                            return;
                        }

                        parqueaderoService.registrarIngreso(placa, tipo, propietario, observaciones);
                        redirect(exchange, "/control?msg=" + URLEncoder.encode("Vehiculo ingresado correctamente.", StandardCharsets.UTF_8));
                        return;
                    } catch (Exception e) {
                        redirect(exchange, "/control?accion=nuevo&error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
                        return;
                    }
                } else if ("salida".equalsIgnoreCase(accion)) {
                    int idReg = Integer.parseInt(postParams.getOrDefault("idRegistro", "0"));
                    parqueaderoService.registrarSalida(idReg);
                    redirect(exchange, "/control?msg=" + URLEncoder.encode("Salida registrada con exito.", StandardCharsets.UTF_8));
                    return;
                }
                redirect(exchange, "/control");
                return;
            }

            String accion = queryParams.getOrDefault("accion", "listar");
            if ("nuevo".equalsIgnoreCase(accion)) {
                renderFormularioIngreso(exchange, queryParams.get("error"));
            } else if ("ticket".equalsIgnoreCase(accion)) {
                int idReg = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                renderTicketCobro(exchange, idReg);
            } else {
                renderListaControl(exchange, queryParams.get("filtro"), queryParams.get("msg"), queryParams.get("error"));
            }
        }

        private void renderListaControl(HttpExchange exchange, String filtro, String msg, String error) throws IOException {
            List<RegistroParqueadero> lista = (filtro != null && !filtro.isEmpty()) ? parqueaderoService.buscar(filtro) : parqueaderoService.obtenerTodos();

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Control de Entradas y Salidas</h2>");
            sb.append("<p>Módulo de registro en tiempo real y facturación de parqueadero</p></div>");

            if (msg != null && !msg.isEmpty()) {
                sb.append("<div class='alert alert-success'>✓ ").append(escapeHtml(msg)).append("</div>");
            }
            if (error != null && !error.isEmpty()) {
                sb.append("<div class='alert alert-danger'>⚠ ").append(escapeHtml(error)).append("</div>");
            }

            sb.append("<div class='card-container'><div class='toolbar'>");
            sb.append("<form action='/control' method='get' class='search-bar'>");
            sb.append("<input type='text' name='filtro' placeholder='Buscar por placa o nombre...' value='").append(filtro != null ? escapeHtml(filtro) : "").append("'>");
            sb.append("<button type='submit' class='btn btn-primary'>Buscar</button>");
            if (filtro != null) {
                sb.append("<a href='/control' class='btn btn-secondary btn-sm'>Limpiar</a>");
            }
            sb.append("</form>");
            sb.append("<a href='/control?accion=nuevo' class='btn btn-success'>+ Registrar Nuevo Ingreso</a></div>");

            sb.append("<div class='table-responsive'><table class='data-table'><thead><tr>");
            sb.append("<th>ID</th><th>Placa</th><th>Tipo</th><th>Propietario</th><th>Entrada</th><th>Salida</th><th>Horas</th><th>Total</th><th>Estado</th><th>Acciones</th>");
            sb.append("</tr></thead><tbody>");

            for (RegistroParqueadero r : lista) {
                boolean activo = "ACTIVO".equalsIgnoreCase(r.getEstado());
                sb.append("<tr>");
                sb.append("<td>#").append(r.getIdRegistro()).append("</td>");
                sb.append("<td><strong style='color:#60a5fa; font-size:1.1rem;'>").append(escapeHtml(r.getPlacaVehiculo())).append("</strong></td>");
                sb.append("<td>").append(escapeHtml(r.getTipoVehiculo())).append("</td>");
                sb.append("<td>").append(escapeHtml(r.getPropietarioNombre())).append("</td>");
                sb.append("<td>").append(r.getFechaEntradaFormateada()).append("</td>");
                sb.append("<td>").append(r.getFechaSalidaFormateada()).append("</td>");
                sb.append("<td>").append(activo ? "-" : r.getHorasCobradas() + " h").append("</td>");
                sb.append("<td><strong>$").append(String.format("%.2f", r.getTotalPagar())).append("</strong></td>");
                sb.append("<td><span class='badge ").append(activo ? "badge-parked" : "badge-exit").append("'>").append(r.getEstado()).append("</span></td>");
                sb.append("<td><div style='display:flex; gap:0.4rem;'>");
                if (activo) {
                    sb.append("<form action='/control' method='post' style='display:inline;'>");
                    sb.append("<input type='hidden' name='accion' value='salida'>");
                    sb.append("<input type='hidden' name='idRegistro' value='").append(r.getIdRegistro()).append("'>");
                    sb.append("<button type='submit' class='btn btn-primary btn-sm' onclick='return confirm(\"¿Marcar salida del vehículo?\");'>Marcar Salida</button>");
                    sb.append("</form>");
                } else {
                    sb.append("<a href='/control?accion=ticket&id=").append(r.getIdRegistro()).append("' class='btn btn-secondary btn-sm'>Ver Ticket</a>");
                }
                sb.append("</div></td></tr>");
            }

            sb.append("</tbody></table></div></div>");

            sendResponse(exchange, 200, renderFullLayout("Control Parqueadero", sb.toString()));
        }

        private void renderFormularioIngreso(HttpExchange exchange, String error) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Registrar Entrada de Vehículo</h2>");
            sb.append("<p>Ingrese la información para la entrada al parqueadero</p></div>");

            if (error != null && !error.isEmpty()) {
                sb.append("<div class='alert alert-danger'>⚠ ").append(escapeHtml(error)).append("</div>");
            }

            sb.append("<div class='card-container' style='max-width:700px; margin:0 auto;'>");
            sb.append("<form action='/control' method='post'>");
            sb.append("<input type='hidden' name='accion' value='ingresar'>");
            sb.append("<div class='form-grid'>");
            sb.append("<div class='form-group'><label>Placa del Vehículo *</label><input type='text' name='placa' required placeholder='Ej: KRF-502' style='text-transform:uppercase;' pattern='[A-Za-z0-9\\-]{5,8}' minlength='5' maxlength='8' title='Placa de 5 a 8 caracteres'></div>");
            sb.append("<div class='form-group'><label>Tipo de Vehículo *</label><select name='tipo'><option value='Automóvil'>Automóvil ($4,000/h)</option><option value='Motocicleta'>Motocicleta ($2,000/h)</option><option value='Camioneta'>Camioneta ($6,000/h)</option></select></div>");
            sb.append("<div class='form-group' style='grid-column: 1 / -1;'><label>Nombre del Propietario / Conductor *</label><input type='text' name='propietario' required placeholder='Ej: Brandon Yair Galvis Diaz' pattern='[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,60}' minlength='3' maxlength='60' title='Solo letras y espacios'></div>");
            sb.append("<div class='form-group' style='grid-column: 1 / -1;'><label>Observaciones de Ingreso</label><input type='text' name='observaciones' placeholder='Sin rayones, bahía número 4...'></div>");
            sb.append("</div>");
            sb.append("<div class='form-actions'>");
            sb.append("<a href='/control' class='btn btn-secondary'>Cancelar</a>");
            sb.append("<button type='submit' class='btn btn-success'>Registrar Ingreso</button>");
            sb.append("</div></form></div>");

            sendResponse(exchange, 200, renderFullLayout("Nuevo Ingreso", sb.toString()));
        }

        private void renderTicketCobro(HttpExchange exchange, int idRegistro) throws IOException {
            RegistroParqueadero r = registroRepo.buscarPorId(idRegistro);
            if (r == null) {
                redirect(exchange, "/control");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Comprobante de Pago / Ticket</h2>");
            sb.append("<p>Detalle de liquidación de servicio de parqueadero</p></div>");

            sb.append("<div class='ticket-box'>");
            sb.append("  <div class='ticket-header'><h3>PARKONE PARQUEADEROS</h3><p>Popayán, Cauca - SENA ADSO</p></div>");
            sb.append("  <hr style='border:0; border-top:1px dashed #4b5563; margin:1rem 0;'>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Ticket No:</span><strong>#").append(r.getIdRegistro()).append("</strong></div>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Placa:</span><strong style='font-size:1.2rem; color:#60a5fa;'>").append(escapeHtml(r.getPlacaVehiculo())).append("</strong></div>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Tipo:</span><span>").append(escapeHtml(r.getTipoVehiculo())).append("</span></div>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Cliente:</span><span>").append(escapeHtml(r.getPropietarioNombre())).append("</span></div>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Entrada:</span><span>").append(r.getFechaEntradaFormateada()).append("</span></div>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Salida:</span><span>").append(r.getFechaSalidaFormateada()).append("</span></div>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Horas Liquidadas:</span><span>").append(r.getHorasCobradas()).append(" hora(s)</span></div>");
            sb.append("  <div style='display:flex; justify-content:space-between; margin-bottom:0.5rem;'><span>Tarifa/Hora:</span><span>$").append(String.format("%.2f", r.getTarifaAplicada())).append("</span></div>");
            sb.append("  <hr style='border:0; border-top:1px dashed #4b5563; margin:1rem 0;'>");
            sb.append("  <div style='display:flex; justify-content:space-between; font-size:1.4rem; font-weight:bold; color:#4ade80;'><span>TOTAL A PAGAR:</span><span>$").append(String.format("%.2f", r.getTotalPagar())).append("</span></div>");
            sb.append("  <div style='text-align:center; margin-top:1.5rem;'><a href='/control' class='btn btn-primary'>Volver al Control</a></div>");
            sb.append("</div>");

            sendResponse(exchange, 200, renderFullLayout("Ticket de Pago", sb.toString()));
        }
    }

    // Manejador Módulo Usuarios
    static class ManejadorUsuario implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioSesionActiva == null || !"Administrador".equalsIgnoreCase(usuarioSesionActiva.getRol())) {
                redirect(exchange, "/");
                return;
            }

            String method = exchange.getRequestMethod();
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> postParams = parseQueryParams(body);
                String accion = postParams.get("accion");
                String pass = postParams.getOrDefault("contrasena", "123456");

                if ("actualizar".equalsIgnoreCase(accion)) {
                    int id = Integer.parseInt(postParams.getOrDefault("idUsuario", "0"));
                    Usuario u = new Usuario(id, postParams.get("identificacion"), postParams.get("nombres"), postParams.get("apellidos"), postParams.get("cargo"), postParams.get("rol"), postParams.get("correo"), pass, postParams.get("telefono"), postParams.get("direccion"), null);
                    usuarioService.actualizar(u);
                } else {
                    Usuario u = new Usuario(postParams.get("identificacion"), postParams.get("nombres"), postParams.get("apellidos"), postParams.get("cargo"), postParams.get("rol"), postParams.get("correo"), pass, postParams.get("telefono"), postParams.get("direccion"));
                    usuarioService.registrar(u);
                }
                redirect(exchange, "/usuarios");
                return;
            }

            String accion = queryParams.getOrDefault("accion", "listar");

            if ("nuevo".equalsIgnoreCase(accion)) {
                renderFormularioUsuario(exchange, null);
            } else if ("editar".equalsIgnoreCase(accion)) {
                int id = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                Usuario u = usuarioService.obtenerPorId(id);
                renderFormularioUsuario(exchange, u);
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                int id = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                usuarioService.eliminar(id);
                redirect(exchange, "/usuarios");
            } else {
                renderListaUsuarios(exchange, queryParams.get("criterio"));
            }
        }

        private void renderListaUsuarios(HttpExchange exchange, String criterio) throws IOException {
            List<Usuario> lista = usuarioService.buscar(criterio);

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Gestión de Usuarios</h2>");
            sb.append("<p>Módulo de administración de personal y roles</p></div>");

            sb.append("<div class='card-container'><div class='toolbar'>");
            sb.append("<form action='/usuarios' method='get' class='search-bar'>");
            sb.append("<input type='text' name='criterio' placeholder='Buscar por cédula o nombre...' value='").append(criterio != null ? escapeHtml(criterio) : "").append("'>");
            sb.append("<button type='submit' class='btn btn-primary'>Buscar</button>");
            if (criterio != null) {
                sb.append("<a href='/usuarios' class='btn btn-secondary btn-sm'>Limpiar</a>");
            }
            sb.append("</form>");
            sb.append("<a href='/usuarios?accion=nuevo' class='btn btn-success'>+ Registrar Usuario</a></div>");

            sb.append("<div class='table-responsive'><table class='data-table'><thead><tr>");
            sb.append("<th>ID</th><th>Identificación</th><th>Nombre Completo</th><th>Cargo</th><th>Rol</th><th>Correo</th><th>Teléfono</th><th>Acciones</th>");
            sb.append("</tr></thead><tbody>");

            for (Usuario u : lista) {
                sb.append("<tr>");
                sb.append("<td>").append(u.getIdUsuario()).append("</td>");
                sb.append("<td><strong>").append(escapeHtml(u.getIdentificacion())).append("</strong></td>");
                sb.append("<td>").append(escapeHtml(u.getNombreCompleto())).append("</td>");
                sb.append("<td>").append(escapeHtml(u.getCargo())).append("</td>");
                sb.append("<td><span class='badge ").append("Administrador".equalsIgnoreCase(u.getRol()) ? "badge-admin" : "badge-user").append("'>").append(escapeHtml(u.getRol())).append("</span></td>");
                sb.append("<td>").append(escapeHtml(u.getCorreo())).append("</td>");
                sb.append("<td>").append(u.getTelefono() != null ? escapeHtml(u.getTelefono()) : "N/A").append("</td>");
                sb.append("<td><div style='display:flex; gap:0.5rem;'>");
                sb.append("<a href='/usuarios?accion=editar&id=").append(u.getIdUsuario()).append("' class='btn btn-primary btn-sm'>Editar</a>");
                sb.append("<a href='/usuarios?accion=eliminar&id=").append(u.getIdUsuario()).append("' class='btn btn-secondary btn-sm' onclick='return confirm(\"¿Eliminar usuario?\");'>Eliminar</a>");
                sb.append("</div></td></tr>");
            }

            sb.append("</tbody></table></div></div>");

            sendResponse(exchange, 200, renderFullLayout("Usuarios", sb.toString()));
        }

        private void renderFormularioUsuario(HttpExchange exchange, Usuario u) throws IOException {
            boolean esEdit = (u != null);
            StringBuilder sb = new StringBuilder();

            sb.append("<div class='titulo-pagina'><h2>").append(esEdit ? "Editar Usuario" : "Registrar Nuevo Usuario").append("</h2>");
            sb.append("<p>Complete los datos requeridos</p></div>");

            sb.append("<div class='card-container' style='max-width:800px; margin:0 auto;'>");
            sb.append("<form action='/usuarios' method='post'>");
            sb.append("<input type='hidden' name='accion' value='").append(esEdit ? "actualizar" : "guardar").append("'>");
            if (esEdit) {
                sb.append("<input type='hidden' name='idUsuario' value='").append(u.getIdUsuario()).append("'>");
            }

            sb.append("<div class='form-grid'>");
            sb.append("<div class='form-group'><label>Cédula *</label><input type='text' name='identificacion' required value='").append(esEdit ? escapeHtml(u.getIdentificacion()) : "").append("'></div>");
            sb.append("<div class='form-group'><label>Nombres *</label><input type='text' name='nombres' required value='").append(esEdit ? escapeHtml(u.getNombres()) : "").append("'></div>");
            sb.append("<div class='form-group'><label>Apellidos *</label><input type='text' name='apellidos' required value='").append(esEdit ? escapeHtml(u.getApellidos()) : "").append("'></div>");
            sb.append("<div class='form-group'><label>Cargo *</label><input type='text' name='cargo' required value='").append(esEdit ? escapeHtml(u.getCargo()) : "").append("'></div>");
            sb.append("<div class='form-group'><label>Rol *</label><select name='rol'>");
            sb.append("<option value='Usuario' ").append(esEdit && "Usuario".equals(u.getRol()) ? "selected" : "").append(">Usuario</option>");
            sb.append("<option value='Administrador' ").append(esEdit && "Administrador".equals(u.getRol()) ? "selected" : "").append(">Administrador</option>");
            sb.append("</select></div>");
            sb.append("<div class='form-group'><label>Correo *</label><input type='email' name='correo' required value='").append(esEdit ? escapeHtml(u.getCorreo()) : "").append("'></div>");
            sb.append("<div class='form-group'><label>Contraseña *</label><input type='password' name='contrasena' required value='").append(esEdit && u.getContrasena() != null ? escapeHtml(u.getContrasena()) : "123456").append("'></div>");
            sb.append("<div class='form-group'><label>Teléfono</label><input type='text' name='telefono' value='").append(esEdit && u.getTelefono() != null ? escapeHtml(u.getTelefono()) : "").append("'></div>");
            sb.append("</div>");

            sb.append("<div class='form-actions'>");
            sb.append("<a href='/usuarios' class='btn btn-secondary'>Cancelar</a>");
            sb.append("<button type='submit' class='btn btn-success'>").append(esEdit ? "Guardar Cambios" : "Registrar").append("</button>");
            sb.append("</div></form></div>");

            sendResponse(exchange, 200, renderFullLayout("Formulario Usuario", sb.toString()));
        }
    }

    // Manejador Módulo Vehículos
    static class ManejadorVehiculo implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioSesionActiva == null) {
                redirect(exchange, "/login");
                return;
            }

            String method = exchange.getRequestMethod();
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> postParams = parseQueryParams(body);
                try {
                    String placa = postParams.get("placa");
                    String tipo = postParams.get("tipo");
                    String marca = postParams.get("marca");
                    String color = postParams.get("color");
                    String cedula = postParams.get("propietarioCedula");
                    String nombre = postParams.get("propietarioNombre");
                    double tarifa = Double.parseDouble(postParams.getOrDefault("tarifaPorHora", "4000"));

                    if (tarifa < 1000) {
                        redirect(exchange, "/vehiculos?accion=nuevo&error=" + URLEncoder.encode("La tarifa minima permitida es $1000.", StandardCharsets.UTF_8));
                        return;
                    }

                    Vehiculo v = new Vehiculo(placa, tipo, marca, color, cedula, nombre, "Estacionado", tarifa);
                    vehiculoService.guardar(v);
                    redirect(exchange, "/vehiculos?msg=" + URLEncoder.encode("Vehiculo registrado con exito.", StandardCharsets.UTF_8));
                    return;
                } catch (Exception e) {
                    redirect(exchange, "/vehiculos?accion=nuevo&error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
                    return;
                }
            }

            String accion = queryParams.getOrDefault("accion", "listar");
            if ("nuevo".equalsIgnoreCase(accion)) {
                renderFormularioVehiculo(exchange, queryParams.get("error"));
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                int id = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                vehiculoService.eliminar(id);
                redirect(exchange, "/vehiculos?msg=" + URLEncoder.encode("Vehiculo eliminado.", StandardCharsets.UTF_8));
            } else {
                renderListaVehiculos(exchange, queryParams.get("filtro"), queryParams.get("msg"));
            }
        }

        private void renderListaVehiculos(HttpExchange exchange, String filtro, String msg) throws IOException {
            List<Vehiculo> lista = vehiculoService.buscar(filtro);

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Gestión de Vehículos</h2>");
            sb.append("<p>Módulo de vehículos y tarifas por categoría</p></div>");

            if (msg != null && !msg.isEmpty()) {
                sb.append("<div class='alert alert-success'>✓ ").append(escapeHtml(msg)).append("</div>");
            }

            sb.append("<div class='card-container'><div class='toolbar'>");
            sb.append("<form action='/vehiculos' method='get' class='search-bar'>");
            sb.append("<input type='text' name='filtro' placeholder='Buscar por placa o cédula...' value='").append(filtro != null ? escapeHtml(filtro) : "").append("'>");
            sb.append("<button type='submit' class='btn btn-primary'>Buscar</button>");
            if (filtro != null) {
                sb.append("<a href='/vehiculos' class='btn btn-secondary btn-sm'>Limpiar</a>");
            }
            sb.append("</form>");
            sb.append("<a href='/vehiculos?accion=nuevo' class='btn btn-success'>+ Registrar Vehículo</a></div>");

            sb.append("<div class='table-responsive'><table class='data-table'><thead><tr>");
            sb.append("<th>Placa</th><th>Tipo</th><th>Marca/Color</th><th>Propietario</th><th>Cédula</th><th>Estado</th><th>Tarifa/Hora</th><th>Acciones</th>");
            sb.append("</tr></thead><tbody>");

            for (Vehiculo v : lista) {
                boolean estacionado = "Estacionado".equalsIgnoreCase(v.getEstado());
                sb.append("<tr>");
                sb.append("<td><strong style='color:#60a5fa;'>").append(escapeHtml(v.getPlaca())).append("</strong></td>");
                sb.append("<td>").append(escapeHtml(v.getTipo())).append("</td>");
                sb.append("<td>").append(escapeHtml(v.getMarca())).append(" (").append(escapeHtml(v.getColor())).append(")</td>");
                sb.append("<td>").append(escapeHtml(v.getPropietarioNombre())).append("</td>");
                sb.append("<td>").append(escapeHtml(v.getPropietarioCedula())).append("</td>");
                sb.append("<td><span class='badge ").append(estacionado ? "badge-parked" : "badge-exit").append("'>").append(escapeHtml(v.getEstado())).append("</span></td>");
                sb.append("<td>$").append(String.format("%.2f", v.getTarifaPorHora())).append("</td>");
                sb.append("<td><div style='display:flex; gap:0.4rem;'>");
                sb.append("<a href='/vehiculos?accion=eliminar&id=").append(v.getIdVehiculo()).append("' class='btn btn-secondary btn-sm' onclick='return confirm(\"¿Eliminar registro?\");'>Eliminar</a>");
                sb.append("</div></td></tr>");
            }

            sb.append("</tbody></table></div></div>");

            sendResponse(exchange, 200, renderFullLayout("Vehículos", sb.toString()));
        }

        private void renderFormularioVehiculo(HttpExchange exchange, String error) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Registrar Vehículo</h2>");
            sb.append("<p>Información del vehículo y propietario</p></div>");

            if (error != null && !error.isEmpty()) {
                sb.append("<div class='alert alert-danger'>⚠ ").append(escapeHtml(error)).append("</div>");
            }

            sb.append("<div class='card-container' style='max-width:800px; margin:0 auto;'>");
            sb.append("<form action='/vehiculos' method='post'>");
            sb.append("<div class='form-grid'>");
            sb.append("<div class='form-group'><label>Placa *</label><input type='text' name='placa' required placeholder='Ej: KRF-502' style='text-transform:uppercase;' pattern='[A-Za-z0-9\\-]{5,8}' minlength='5' maxlength='8' title='Placa de 5 a 8 caracteres'></div>");
            sb.append("<div class='form-group'><label>Tipo *</label><select name='tipo'><option value='Automóvil'>Automóvil</option><option value='Motocicleta'>Motocicleta</option><option value='Camioneta'>Camioneta</option></select></div>");
            sb.append("<div class='form-group'><label>Marca</label><input type='text' name='marca' placeholder='Toyota' maxlength='30'></div>");
            sb.append("<div class='form-group'><label>Color</label><input type='text' name='color' placeholder='Negro' maxlength='20'></div>");
            sb.append("<div class='form-group'><label>Cédula del Propietario *</label><input type='text' name='propietarioCedula' required placeholder='1061723849' pattern='[0-9]{6,10}' minlength='6' maxlength='10' title='Cédula de 6 a 10 dígitos numéricos'></div>");
            sb.append("<div class='form-group'><label>Nombre del Propietario *</label><input type='text' name='propietarioNombre' required placeholder='Brandon Yair Galvis Diaz' pattern='[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,60}' minlength='3' maxlength='60' title='Solo letras y espacios'></div>");
            sb.append("<div class='form-group'><label>Tarifa por Hora ($) *</label><input type='number' name='tarifaPorHora' value='4000' min='1000' max='50000' step='500' required></div>");
            sb.append("</div>");
            sb.append("<div class='form-actions'>");
            sb.append("<a href='/vehiculos' class='btn btn-secondary'>Cancelar</a>");
            sb.append("<button type='submit' class='btn btn-success'>Guardar Vehículo</button>");
            sb.append("</div></form></div>");

            sendResponse(exchange, 200, renderFullLayout("Nuevo Vehículo", sb.toString()));
        }
    }

    // Manejador del Inicio de Sesión
    static class ManejadorLogin implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            if ("logout".equalsIgnoreCase(queryParams.get("accion"))) {
                usuarioSesionActiva = null;
                redirect(exchange, "/login");
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> postParams = parseQueryParams(body);
                String user = postParams.get("usuario");
                String pass = postParams.get("contrasena");

                Usuario u = usuarioService.login(user, pass);
                if (u != null) {
                    usuarioSesionActiva = u;
                    redirect(exchange, "/");
                    return;
                } else {
                    renderFormularioLogin(exchange, "Credenciales incorrectas.");
                    return;
                }
            }

            renderFormularioLogin(exchange, null);
        }

        private void renderFormularioLogin(HttpExchange exchange, String errorMsg) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<div style='display:flex; justify-content:center; align-items:center; min-height:65vh;'>");
            sb.append("<div class='card-container' style='max-width:400px; width:100%;'>");
            sb.append("<div style='text-align:center; margin-bottom:1.5rem;'>");
            sb.append("<div class='brand-icon' style='margin:0 auto 0.75rem auto;'>P1</div>");
            sb.append("<h1 style='font-size:1.6rem; color:white;'>Park<span style='color:#39a900;'>One</span></h1>");
            sb.append("<p style='color:#9ca3af; font-size:0.9rem;'>Iniciar Sesión en el Sistema</p></div>");

            if (errorMsg != null) {
                sb.append("<div class='alert alert-danger'><span>").append(errorMsg).append("</span></div>");
            }

            sb.append("<form action='/login' method='post'>");
            sb.append("<div class='form-group' style='margin-bottom:1.2rem;'><label>Correo o Cédula *</label>");
            sb.append("<input type='text' name='usuario' required placeholder='1061723849'></div>");
            sb.append("<div class='form-group' style='margin-bottom:1.5rem;'><label>Contraseña *</label>");
            sb.append("<input type='password' name='contrasena' required value='123456'></div>");
            sb.append("<button type='submit' class='btn btn-success' style='width:100%; justify-content:center; padding:0.75rem;'>Ingresar</button>");
            sb.append("</form>");
            sb.append("</div></div>");

            sendResponse(exchange, 200, renderFullLayout("Iniciar Sesión", sb.toString()));
        }
    }

    // Handlers API REST JSON (Para App Móvil)
    static class ManejadorApiDashboard implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> stats = parqueaderoService.obtenerEstadisticas();
            sendJsonResponse(exchange, 200, toJson(stats));
        }
    }

    static class ManejadorApiVehiculos implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Vehiculo> lista = vehiculoService.obtenerTodos();
            sendJsonResponse(exchange, 200, toJsonListVehiculos(lista));
        }
    }

    static class ManejadorApiRegistros implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<RegistroParqueadero> lista = parqueaderoService.obtenerActivos();
            sendJsonResponse(exchange, 200, toJsonListRegistros(lista));
        }
    }

    // Sirve archivos estaticos como CSS
    static class ManejadorArchivos implements HttpHandler {
        private final String path;
        private final String contentType;

        public ManejadorArchivos(String path, String contentType) {
            this.path = path;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File f = new File(path);
            if (f.exists()) {
                byte[] bytes = Files.readAllBytes(f.toPath());
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                sendResponse(exchange, 404, "Página no encontrada");
            }
        }
    }

    // Metodo helper para renderizar el layout completo HTML
    private static String renderFullLayout(String activeTab, String content) {
        String infoUser = (usuarioSesionActiva != null)
                ? "<div><strong>Usuario:</strong> " + usuarioSesionActiva.getNombreCompleto() + " (" + usuarioSesionActiva.getRol() + ")</div><div><a href='/login?accion=logout' style='color:#ef4444;'>Cerrar Sesión</a></div>"
                : "<div><a href='/login' style='color:#4ade80;'>Iniciar Sesión</a></div>";

        boolean esAdmin = (usuarioSesionActiva != null && "Administrador".equalsIgnoreCase(usuarioSesionActiva.getRol()));

        String navMenu = (usuarioSesionActiva != null)
                ? "<nav id='menu-h'><ul>" +
                  "<li><a href='/' class='" + ("Inicio".equals(activeTab) ? "active" : "") + "'>Panel Principal</a></li>" +
                  "<li><a href='/control' class='" + ("Control Parqueadero".equals(activeTab) || "Nuevo Ingreso".equals(activeTab) ? "active" : "") + "'>Control Parqueadero</a></li>" +
                  "<li><a href='/vehiculos' class='" + ("Vehículos".equals(activeTab) ? "active" : "") + "'>Gestión Vehículos</a></li>" +
                  (esAdmin ? "<li><a href='/usuarios' class='" + ("Usuarios".equals(activeTab) ? "active" : "") + "'>Gestión Usuarios</a></li>" : "") +
                  "</ul></nav>"
                : "";

        return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>ParkOne Framework | " + activeTab + "</title><link rel='stylesheet' href='/css/style.css'></head><body>" +
                "<header><div class='brand'><div class='brand-icon'>P1</div><div><h1>Park<span>One</span></h1><small style='color:#9ca3af;'>Framework Web & Móvil</small></div></div>" +
                "<div class='sub-header-info'>" + infoUser + "</div></header>" +
                navMenu +
                "<main class='contenedor-principal'>" + content + "</main>" +
                "<footer><div>Popayán - Cauca</div>" +
                "<div>SENA - Tecnólogo ADSO</div>" +
                "<div style='margin-top:0.25rem;'>Evidencia GA7-220501096-AA3-EV02 &copy; 2026</div></footer></body></html>";
    }

    private static void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
            }
        }
        return map;
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    // Serializadores simples de JSON para respuestas de la API REST sin librerias externas pesadas
    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String toJsonListVehiculos(List<Vehiculo> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Vehiculo v = lista.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(v.getIdVehiculo()).append(",")
              .append("\"placa\":\"").append(v.getPlaca()).append("\",")
              .append("\"tipo\":\"").append(v.getTipo()).append("\",")
              .append("\"propietario\":\"").append(v.getPropietarioNombre()).append("\",")
              .append("\"estado\":\"").append(v.getEstado()).append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonListRegistros(List<RegistroParqueadero> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            RegistroParqueadero r = lista.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(r.getIdRegistro()).append(",")
              .append("\"placa\":\"").append(r.getPlacaVehiculo()).append("\",")
              .append("\"tipo\":\"").append(r.getTipoVehiculo()).append("\",")
              .append("\"fechaEntrada\":\"").append(r.getFechaEntradaFormateada()).append("\",")
              .append("\"estado\":\"").append(r.getEstado()).append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }
}
