<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="co.edu.sena.parkone.modelo.Usuario" %>
<!-- Menú de navegación -->
<%
    Usuario uMenu = (Usuario) session.getAttribute("usuarioLogueado");
    boolean esAdmin = (uMenu != null && "Administrador".equalsIgnoreCase(uMenu.getRol()));
%>
<nav id="menu-h">
    <ul>
        <li><a href="index.jsp">Inicio</a></li>
        <% if (esAdmin) { %>
            <li><a href="usuarios?accion=listar">Módulo Usuarios</a></li>
            <li><a href="usuarios?accion=nuevo">+ Nuevo Usuario</a></li>
        <% } %>
        <li><a href="vehiculos?accion=listar">Módulo Vehículos</a></li>
        <li><a href="vehiculos?accion=nuevo">+ Nuevo Vehículo</a></li>
    </ul>
</nav>
