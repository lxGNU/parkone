<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="co.edu.sena.parkone.modelo.Usuario" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuarios | ParkOne</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <jsp:include page="includes/header.jsp" />
    <jsp:include page="includes/menu.jsp" />

    <main class="contenedor-principal">
        <div class="titulo-pagina">
            <h2>Gestión de Usuarios</h2>
            <p>Lista de usuarios registrados en el sistema</p>
        </div>

        <%
            String mensajeExito = (String) request.getAttribute("mensajeExito");
            String mensajeError = (String) request.getAttribute("mensajeError");
            if (mensajeExito != null) {
        %>
            <div class="alert alert-success"><span><%= mensajeExito %></span></div>
        <%  } %>

        <%  if (mensajeError != null) { %>
            <div class="alert alert-danger"><span><%= mensajeError %></span></div>
        <%  } %>

        <div class="card-container">
            <div class="toolbar">
                <form action="usuarios" method="get" class="search-bar">
                    <input type="hidden" name="accion" value="buscar">
                    <input type="text" name="criterio" placeholder="Buscar por cédula o nombre..." value="<%= request.getAttribute("criterio") != null ? request.getAttribute("criterio") : "" %>">
                    <button type="submit" class="btn btn-primary">Buscar</button>
                    <% if (request.getAttribute("criterio") != null) { %>
                        <a href="usuarios?accion=listar" class="btn btn-danger btn-sm">Limpiar</a>
                    <% } %>
                </form>

                <a href="usuarios?accion=nuevo" class="btn btn-success">+ Registrar Usuario</a>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cédula</th>
                            <th>Nombres y Apellidos</th>
                            <th>Cargo</th>
                            <th>Rol</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Usuario> lista = (List<Usuario>) request.getAttribute("listaUsuarios");
                            if (lista != null && !lista.isEmpty()) {
                                for (Usuario u : lista) {
                        %>
                        <tr>
                            <td><%= u.getIdUsuario() %></td>
                            <td><strong><%= u.getIdentificacion() %></strong></td>
                            <td><%= u.getNombreCompleto() %></td>
                            <td><%= u.getCargo() %></td>
                            <td>
                                <span class="badge <%= "Administrador".equalsIgnoreCase(u.getRol()) ? "badge-admin" : "badge-user" %>">
                                    <%= u.getRol() %>
                                </span>
                            </td>
                            <td><%= u.getCorreo() %></td>
                            <td><%= u.getTelefono() != null ? u.getTelefono() : "N/A" %></td>
                            <td>
                                <div style="display: flex; gap: 0.5rem;">
                                    <a href="usuarios?accion=editar&id=<%= u.getIdUsuario() %>" class="btn btn-primary btn-sm">Editar</a>
                                    <a href="usuarios?accion=eliminar&id=<%= u.getIdUsuario() %>" class="btn btn-danger btn-sm" onclick="return confirm('¿Eliminar usuario?');">Eliminar</a>
                                </div>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2rem;">
                                No hay usuarios registrados.
                            </td>
                        </tr>
                        <%  } %>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />

</body>
</html>
