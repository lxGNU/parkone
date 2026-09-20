<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión | ParkOne</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body style="display: flex; flex-direction: column; justify-content: center; align-items: center; min-height: 100vh;">

    <div class="card-container" style="max-width: 420px; width: 100%; margin: 2rem auto;">
        <div style="text-align: center; margin-bottom: 1.5rem;">
            <div class="brand-icon" style="margin: 0 auto 0.75rem auto;">P1</div>
            <h1 style="font-size: 1.6rem; color: white;">Park<span style="color: var(--secondary-color);">One</span></h1>
            <p style="color: var(--text-muted); font-size: 0.9rem;">Iniciar Sesión en el Sistema</p>
        </div>

        <%
            String mensajeError = (String) request.getAttribute("mensajeError");
            String mensajeExito = (String) request.getAttribute("mensajeExito");
            if (mensajeError != null) {
        %>
            <div class="alert alert-danger" style="font-size: 0.85rem; padding: 0.75rem 1rem;">
                <span><%= mensajeError %></span>
            </div>
        <%  } %>

        <%  if (mensajeExito != null) { %>
            <div class="alert alert-success" style="font-size: 0.85rem; padding: 0.75rem 1rem;">
                <span><%= mensajeExito %></span>
            </div>
        <%  } %>

        <form action="login" method="post">
            <div class="form-group" style="margin-bottom: 1.2rem;">
                <label for="usuario">Correo o Cédula *</label>
                <input type="text" id="usuario" name="usuario" required placeholder="Ingrese correo o cédula">
            </div>

            <div class="form-group" style="margin-bottom: 1.5rem;">
                <label for="contrasena">Contraseña *</label>
                <input type="password" id="contrasena" name="contrasena" required placeholder="••••••••">
            </div>

            <button type="submit" class="btn btn-success" style="width: 100%; justify-content: center; padding: 0.75rem;">
                Ingresar al Sistema
            </button>
        </form>


    </div>

</body>
</html>
