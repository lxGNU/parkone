<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="co.edu.sena.parkone.dao.UsuarioDAO" %>
<%@ page import="co.edu.sena.parkone.dao.VehiculoDAO" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkOne | Inicio</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <jsp:include page="includes/header.jsp" />
    <jsp:include page="includes/menu.jsp" />

    <main class="contenedor-principal">
        <div class="titulo-pagina">
            <h2>Inicio - Sistema ParkOne</h2>
            <p>Panel de control principal</p>
        </div>

        <%
            UsuarioDAO uDao = new UsuarioDAO();
            VehiculoDAO vDao = new VehiculoDAO();
            int totalUsuarios = uDao.listar().size();
            int totalVehiculos = vDao.listar().size();
        %>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
            <div class="card-container" style="text-align: center; border-top: 4px solid var(--primary-color);">
                <h3 style="color: var(--text-muted); font-size: 1rem;">Usuarios Registrados</h3>
                <div style="font-size: 3rem; font-weight: bold; color: white; margin: 0.5rem 0;"><%= totalUsuarios %></div>
                <a href="usuarios?accion=listar" class="btn btn-primary btn-sm">Ver Usuarios</a>
            </div>

            <div class="card-container" style="text-align: center; border-top: 4px solid var(--secondary-color);">
                <h3 style="color: var(--text-muted); font-size: 1rem;">Vehículos en Parqueadero</h3>
                <div style="font-size: 3rem; font-weight: bold; color: #4ade80; margin: 0.5rem 0;"><%= totalVehiculos %></div>
                <a href="vehiculos?accion=listar" class="btn btn-success btn-sm">Ver Vehículos</a>
            </div>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp" />

</body>
</html>
