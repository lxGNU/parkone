<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="co.edu.sena.parkone.modelo.Usuario" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulario Usuario | ParkOne</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <jsp:include page="includes/header.jsp" />
    <jsp:include page="includes/menu.jsp" />

    <%
        Usuario uEditar = (Usuario) request.getAttribute("usuarioEditar");
        boolean esEdicion = (uEditar != null);
    %>

    <main class="contenedor-principal">
        <div class="titulo-pagina">
            <h2><%= esEdicion ? "Modificar Usuario" : "Registrar Usuario" %></h2>
            <p>Complete la información del usuario</p>
        </div>

        <div class="card-container" style="max-width: 850px; margin: 0 auto;">
            <form action="usuarios" method="post">
                <input type="hidden" name="accion" value="<%= esEdicion ? "actualizar" : "guardar" %>">
                <% if (esEdicion) { %>
                    <input type="hidden" name="idUsuario" value="<%= uEditar.getIdUsuario() %>">
                <% } %>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="identificacion">Cédula *</label>
                        <input type="text" id="identificacion" name="identificacion" required placeholder="Ingrese la cédula" value="<%= esEdicion ? uEditar.getIdentificacion() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="nombres">Nombres *</label>
                        <input type="text" id="nombres" name="nombres" required placeholder="Ingrese los nombres" value="<%= esEdicion ? uEditar.getNombres() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="apellidos">Apellidos *</label>
                        <input type="text" id="apellidos" name="apellidos" required placeholder="Ingrese los apellidos" value="<%= esEdicion ? uEditar.getApellidos() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="cargo">Cargo *</label>
                        <input type="text" id="cargo" name="cargo" required placeholder="Ingrese el cargo" value="<%= esEdicion ? uEditar.getCargo() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="rol">Rol de Acceso *</label>
                        <select id="rol" name="rol" required>
                            <option value="Usuario" <%= (esEdicion && "Usuario".equalsIgnoreCase(uEditar.getRol())) ? "selected" : "" %>>Usuario</option>
                            <option value="Administrador" <%= (esEdicion && "Administrador".equalsIgnoreCase(uEditar.getRol())) ? "selected" : "" %>>Administrador</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="correo">Correo Electrónico *</label>
                        <input type="email" id="correo" name="correo" required placeholder="Ingrese el correo" value="<%= esEdicion ? uEditar.getCorreo() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="contrasena">Contraseña de Acceso *</label>
                        <input type="password" id="contrasena" name="contrasena" required placeholder="Ingrese la contraseña para inicio de sesión" value="<%= (esEdicion && uEditar.getContrasena() != null) ? uEditar.getContrasena() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="telefono">Teléfono</label>
                        <input type="text" id="telefono" name="telefono" placeholder="Ingrese el teléfono" value="<%= (esEdicion && uEditar.getTelefono() != null) ? uEditar.getTelefono() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="direccion">Dirección</label>
                        <input type="text" id="direccion" name="direccion" placeholder="Ingrese la dirección" value="<%= (esEdicion && uEditar.getDireccion() != null) ? uEditar.getDireccion() : "" %>">
                    </div>
                </div>

                <div class="form-actions">
                    <a href="usuarios?accion=listar" class="btn btn-danger">Cancelar</a>
                    <button type="submit" class="btn btn-success">
                        <%= esEdicion ? "Guardar Cambios" : "Registrar Usuario" %>
                    </button>
                </div>
            </form>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />

</body>
</html>
