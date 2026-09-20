package co.edu.sena.parkone.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import co.edu.sena.parkone.dao.UsuarioDAO;
import co.edu.sena.parkone.dao.VehiculoDAO;
import co.edu.sena.parkone.modelo.Usuario;
import co.edu.sena.parkone.modelo.Vehiculo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

// Servidor local sencillo para ejecutar la app
public class ServidorWeb {

    private static final int PUERTO = 8080;
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private static Usuario usuarioSesionActiva = null;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);

        // Rutas principales
        server.createContext("/", new ManejadorInicio());
        server.createContext("/login", new ManejadorLogin());
        server.createContext("/usuarios", new ManejadorUsuario());
        server.createContext("/vehiculos", new ManejadorVehiculo());
        server.createContext("/css/style.css", new ManejadorArchivos("src/main/webapp/css/style.css", "text/css"));

        server.setExecutor(null);
        System.out.println("=================================================");
        System.out.println(" Servidor ParkOne listo");
        System.out.println(" Aprendiz: Brandon Yair Galvis Diaz");
        System.out.println(" Entrar a: http://localhost:" + PUERTO + "/");
        System.out.println("=================================================");
        server.start();
    }

    static class ManejadorInicio implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioSesionActiva == null) {
                redirect(exchange, "/login");
                return;
            }

            int totalU = usuarioDAO.listar().size();
            int totalV = vehiculoDAO.listar().size();

            String html =
                "<div class='titulo-pagina'><h2>Panel Principal - Servlets y JSP</h2>" +
                "<p>Sistema de Gestión de Parqueaderos ParkOne</p></div>" +
                "<div style='display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;'>" +
                "  <div class='card-container' style='text-align: center; border-top: 4px solid #0d6efd;'>" +
                "    <h3 style='color: #9ca3af; font-size: 1rem;'>Usuarios Registrados</h3>" +
                "    <div style='font-size: 3rem; font-weight: bold; color: white; margin: 0.5rem 0;'>" + totalU + "</div>" +
                "    <a href='/usuarios' class='btn btn-primary btn-sm'>Ver Usuarios</a>" +
                "  </div>" +
                "  <div class='card-container' style='text-align: center; border-top: 4px solid #39a900;'>" +
                "    <h3 style='color: #9ca3af; font-size: 1rem;'>Vehículos en Parqueadero</h3>" +
                "    <div style='font-size: 3rem; font-weight: bold; color: #4ade80; margin: 0.5rem 0;'>" + totalV + "</div>" +
                "    <a href='/vehiculos' class='btn btn-success btn-sm'>Ver Vehículos</a>" +
                "  </div>" +
                "</div>" +
                "<div class='card-container'><h3>Partes del Sistema</h3>" +
                "<ul style='margin-left: 1.5rem; margin-top: 1rem; color: #d1d5db; display: flex; flex-direction: column; gap: 0.5rem;'>" +
                "  <li><strong>Servlets:</strong> UsuarioServlet, VehiculoServlet y LoginServlet.</li>" +
                "  <li><strong>Formularios:</strong> Formularios HTML para guardar y editar.</li>" +
                "  <li><strong>JSP:</strong> Paginas JSP con expresiones y partes reutilizables.</li>" +
                "  <li><strong>Base de datos:</strong> Script MySQL (<code>parkone_db.sql</code>) y JDBC.</li>" +
                "</ul></div>";

            html = renderFullLayout("Inicio", html);
            sendResponse(exchange, 200, html);
        }
    }

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

                Usuario u = usuarioDAO.validarLogin(user, pass);
                if (u != null) {
                    usuarioSesionActiva = u;
                    redirect(exchange, "/");
                    return;
                } else {
                    renderFormularioLogin(exchange, "Datos incorrectos. Intente de nuevo.");
                    return;
                }
            }

            renderFormularioLogin(exchange, null);
        }

        private void renderFormularioLogin(HttpExchange exchange, String errorMsg) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<div style='display:flex; justify-content:center; align-items:center; min-height:70vh;'>");
            sb.append("<div class='card-container' style='max-width:420px; width:100%; margin:0 auto;'>");
            sb.append("<div style='text-align:center; margin-bottom:1.5rem;'>");
            sb.append("<div class='brand-icon' style='margin:0 auto 0.75rem auto;'>P1</div>");
            sb.append("<h1 style='font-size:1.6rem; color:white;'>Park<span style='color:var(--secondary-color);'>One</span></h1>");
            sb.append("<p style='color:var(--text-muted); font-size:0.9rem;'>Iniciar Sesión en el Sistema</p></div>");

            if (errorMsg != null) {
                sb.append("<div class='alert alert-danger' style='font-size:0.85rem; padding:0.75rem 1rem;'><span>").append(errorMsg).append("</span></div>");
            }

            sb.append("<form action='/login' method='post'>");
            sb.append("<div class='form-group' style='margin-bottom:1.2rem;'><label>Correo o Cédula *</label>");
            sb.append("<input type='text' name='usuario' required placeholder='Ingrese correo o cédula'></div>");
            sb.append("<div class='form-group' style='margin-bottom:1.5rem;'><label>Contraseña *</label>");
            sb.append("<input type='password' name='contrasena' required placeholder='••••••••'></div>");
            sb.append("<button type='submit' class='btn btn-success' style='width:100%; justify-content:center; padding:0.75rem;'>Ingresar al Sistema</button>");
            sb.append("</form>");
            sb.append("</div></div>");

            sendResponse(exchange, 200, renderFullLayout("Iniciar Sesión", sb.toString()));
        }
    }

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
                    usuarioDAO.actualizar(u);
                } else {
                    Usuario u = new Usuario(postParams.get("identificacion"), postParams.get("nombres"), postParams.get("apellidos"), postParams.get("cargo"), postParams.get("rol"), postParams.get("correo"), pass, postParams.get("telefono"), postParams.get("direccion"));
                    usuarioDAO.insertar(u);
                }

                redirect(exchange, "/usuarios");
                return;
            }

            String accion = queryParams.getOrDefault("accion", "listar");

            if ("nuevo".equalsIgnoreCase(accion)) {
                renderFormularioUsuario(exchange, null);
            } else if ("editar".equalsIgnoreCase(accion)) {
                int id = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                Usuario u = usuarioDAO.buscarPorId(id);
                renderFormularioUsuario(exchange, u);
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                int id = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                usuarioDAO.eliminar(id);
                redirect(exchange, "/usuarios");
            } else {
                renderListaUsuarios(exchange, queryParams.get("criterio"));
            }
        }

        private void renderListaUsuarios(HttpExchange exchange, String criterio) throws IOException {
            List<Usuario> lista = (criterio != null && !criterio.isEmpty()) ? usuarioDAO.buscarPorCriterio(criterio) : usuarioDAO.listar();

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Lista de Usuarios</h2>");
            sb.append("<p>Gestión de usuarios registrados</p></div>");

            sb.append("<div class='card-container'><div class='toolbar'>");
            sb.append("<form action='/usuarios' method='get' class='search-bar'>");
            sb.append("<input type='hidden' name='accion' value='buscar'>");
            sb.append("<input type='text' name='criterio' placeholder='Buscar por cédula o nombre...' value='").append(criterio != null ? escapeHtml(criterio) : "").append("'>");
            sb.append("<button type='submit' class='btn btn-primary'>Buscar</button>");
            if (criterio != null) {
                sb.append("<a href='/usuarios' class='btn btn-danger btn-sm'>Limpiar</a>");
            }
            sb.append("</form>");
            sb.append("<a href='/usuarios?accion=nuevo' class='btn btn-success'>+ Registrar Usuario</a></div>");

            sb.append("<div class='table-responsive'><table class='data-table'><thead><tr>");
            sb.append("<th>ID</th><th>Identificación</th><th>Nombre</th><th>Cargo</th><th>Rol</th><th>Correo</th><th>Teléfono</th><th>Acciones</th>");
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
                sb.append("<a href='/usuarios?accion=eliminar&id=").append(u.getIdUsuario()).append("' class='btn btn-danger btn-sm' onclick='return confirm(\"¿Eliminar usuario?\");'>Eliminar</a>");
                sb.append("</div></td></tr>");
            }

            sb.append("</tbody></table></div></div>");

            sendResponse(exchange, 200, renderFullLayout("Usuarios", sb.toString()));
        }

        private void renderFormularioUsuario(HttpExchange exchange, Usuario u) throws IOException {
            boolean esEdit = (u != null);
            StringBuilder sb = new StringBuilder();

            sb.append("<div class='titulo-pagina'><h2>").append(esEdit ? "Modificar Usuario" : "Nuevo Usuario").append("</h2>");
            sb.append("<p>Complete la información del usuario</p></div>");

            sb.append("<div class='card-container' style='max-width:850px; margin:0 auto;'>");
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
            sb.append("<div class='form-group'><label>Contraseña *</label><input type='password' name='contrasena' required value='").append(esEdit && u.getContrasena() != null ? escapeHtml(u.getContrasena()) : "").append("'></div>");
            sb.append("<div class='form-group'><label>Teléfono</label><input type='text' name='telefono' value='").append(esEdit && u.getTelefono() != null ? escapeHtml(u.getTelefono()) : "").append("'></div>");
            sb.append("<div class='form-group'><label>Dirección</label><input type='text' name='direccion' value='").append(esEdit && u.getDireccion() != null ? escapeHtml(u.getDireccion()) : "").append("'></div>");
            sb.append("</div>");

            sb.append("<div class='form-actions'>");
            sb.append("<a href='/usuarios' class='btn btn-danger'>Cancelar</a>");
            sb.append("<button type='submit' class='btn btn-success'>").append(esEdit ? "Guardar Cambios" : "Registrar Usuario").append("</button>");
            sb.append("</div></form></div>");

            sendResponse(exchange, 200, renderFullLayout("Formulario Usuario", sb.toString()));
        }
    }

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
                double tarifa = Double.parseDouble(postParams.getOrDefault("tarifaPorHora", "4000"));
                Vehiculo v = new Vehiculo(postParams.get("placa"), postParams.get("tipo"), postParams.get("marca"), postParams.get("color"), postParams.get("propietarioCedula"), postParams.get("propietarioNombre"), "Estacionado", tarifa);
                vehiculoDAO.insertar(v);
                redirect(exchange, "/vehiculos");
                return;
            }

            String accion = queryParams.getOrDefault("accion", "listar");
            if ("nuevo".equalsIgnoreCase(accion)) {
                renderFormularioVehiculo(exchange);
            } else if ("cambiarEstado".equalsIgnoreCase(accion)) {
                int id = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                String estado = queryParams.getOrDefault("estado", "Retirado");
                vehiculoDAO.cambiarEstado(id, estado);
                redirect(exchange, "/vehiculos");
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                int id = Integer.parseInt(queryParams.getOrDefault("id", "0"));
                vehiculoDAO.eliminar(id);
                redirect(exchange, "/vehiculos");
            } else {
                renderListaVehiculos(exchange, queryParams.get("filtro"));
            }
        }

        private void renderListaVehiculos(HttpExchange exchange, String filtro) throws IOException {
            List<Vehiculo> lista = (filtro != null && !filtro.isEmpty()) ? vehiculoDAO.buscarPorPlacaOCedula(filtro) : vehiculoDAO.listar();

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Lista de Vehículos</h2>");
            sb.append("<p>Gestión de vehículos ingresados</p></div>");

            sb.append("<div class='card-container'><div class='toolbar'>");
            sb.append("<form action='/vehiculos' method='get' class='search-bar'>");
            sb.append("<input type='hidden' name='accion' value='buscar'>");
            sb.append("<input type='text' name='filtro' placeholder='Buscar por placa o cédula...' value='").append(filtro != null ? escapeHtml(filtro) : "").append("'>");
            sb.append("<button type='submit' class='btn btn-primary'>Buscar</button>");
            if (filtro != null) {
                sb.append("<a href='/vehiculos' class='btn btn-danger btn-sm'>Limpiar</a>");
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
                if (estacionado) {
                    sb.append("<a href='/vehiculos?accion=cambiarEstado&id=").append(v.getIdVehiculo()).append("&estado=Retirado' class='btn btn-primary btn-sm'>Marcar Salida</a>");
                } else {
                    sb.append("<a href='/vehiculos?accion=cambiarEstado&id=").append(v.getIdVehiculo()).append("&estado=Estacionado' class='btn btn-success btn-sm'>Re-ingresar</a>");
                }
                sb.append("<a href='/vehiculos?accion=eliminar&id=").append(v.getIdVehiculo()).append("' class='btn btn-danger btn-sm' onclick='return confirm(\"¿Eliminar vehículo?\");'>Eliminar</a>");
                sb.append("</div></td></tr>");
            }

            sb.append("</tbody></table></div></div>");

            sendResponse(exchange, 200, renderFullLayout("Vehículos", sb.toString()));
        }

        private void renderFormularioVehiculo(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<div class='titulo-pagina'><h2>Ingreso de Vehículo</h2>");
            sb.append("<p>Complete la información del vehículo</p></div>");

            sb.append("<div class='card-container' style='max-width:850px; margin:0 auto;'>");
            sb.append("<form action='/vehiculos' method='post'>");
            sb.append("<input type='hidden' name='accion' value='guardar'>");
            sb.append("<div class='form-grid'>");
            sb.append("<div class='form-group'><label>Placa *</label><input type='text' name='placa' required placeholder='ABC-123' style='text-transform:uppercase;'></div>");
            sb.append("<div class='form-group'><label>Tipo *</label><select name='tipo'><option value='Automóvil'>Automóvil</option><option value='Motocicleta'>Motocicleta</option><option value='Camioneta'>Camioneta</option></select></div>");
            sb.append("<div class='form-group'><label>Marca</label><input type='text' name='marca' placeholder='Toyota'></div>");
            sb.append("<div class='form-group'><label>Color</label><input type='text' name='color' placeholder='Gris'></div>");
            sb.append("<div class='form-group'><label>Cédula del Propietario *</label><input type='text' name='propietarioCedula' required placeholder='1061723849'></div>");
            sb.append("<div class='form-group'><label>Nombre del Propietario *</label><input type='text' name='propietarioNombre' required placeholder='Brandon Yair Galvis Diaz'></div>");
            sb.append("<div class='form-group'><label>Tarifa por Hora ($) *</label><input type='number' name='tarifaPorHora' value='4000' required></div>");
            sb.append("</div>");
            sb.append("<div class='form-actions'>");
            sb.append("<a href='/vehiculos' class='btn btn-danger'>Cancelar</a>");
            sb.append("<button type='submit' class='btn btn-success'>Registrar Entrada</button>");
            sb.append("</div></form></div>");

            sendResponse(exchange, 200, renderFullLayout("Formulario Vehículo", sb.toString()));
        }
    }

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
                sendResponse(exchange, 404, "404 Página no encontrada");
            }
        }
    }

    private static String renderFullLayout(String activeTab, String content) {
        String infoUser = (usuarioSesionActiva != null)
                ? "<div><strong>Usuario:</strong> " + usuarioSesionActiva.getNombreCompleto() + " (" + usuarioSesionActiva.getRol() + ")</div><div><a href='/login?accion=logout' style='color:#f87171;'>Cerrar Sesión</a></div>"
                : "<div><a href='/login' style='color:#4ade80;'>Iniciar Sesión</a></div>";

        boolean esAdmin = (usuarioSesionActiva != null && "Administrador".equalsIgnoreCase(usuarioSesionActiva.getRol()));

        String navMenu = (usuarioSesionActiva != null)
                ? "<nav id='menu-h'><ul>" +
                  "<li><a href='/' class='" + ("Inicio".equals(activeTab) ? "active" : "") + "'>Inicio</a></li>" +
                  (esAdmin ? "<li><a href='/usuarios' class='" + ("Usuarios".equals(activeTab) ? "active" : "") + "'>Módulo Usuarios</a></li>" +
                             "<li><a href='/usuarios?accion=nuevo'>+ Nuevo Usuario</a></li>" : "") +
                  "<li><a href='/vehiculos' class='" + ("Vehículos".equals(activeTab) ? "active" : "") + "'>Módulo Vehículos</a></li>" +
                  "<li><a href='/vehiculos?accion=nuevo'>+ Nuevo Vehículo</a></li>" +
                  "</ul></nav>"
                : "";

        return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>ParkOne | " + activeTab + "</title><link rel='stylesheet' href='/css/style.css'></head><body>" +
                "<header><div class='brand'><div class='brand-icon'>P1</div><div><h1>Park<span>One</span></h1><small style='color:#9ca3af; font-size:0.75rem;'>Gestión de Parqueaderos</small></div></div>" +
                "<div class='sub-header-info'>" + infoUser + "</div></header>" +
                navMenu +
                "<main class='contenedor-principal'>" + content + "</main>" +
                "<footer><div>Popayán - Cauca</div>" +
                "<div>SENA - Tecnólogo ADSO</div>" +
                "<div style='margin-top:0.25rem;'>Evidencia GA7-220501096-AA2-EV02 &copy; 2026</div></footer></body></html>";
    }

    private static void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
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
}
