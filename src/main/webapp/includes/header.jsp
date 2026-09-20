<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="co.edu.sena.parkone.modelo.Usuario" %>
<!-- Cabecera modular -->
<%
    Usuario userSesion = (Usuario) session.getAttribute("usuarioLogueado");
%>
<header>
    <div class="brand">
        <div class="brand-icon">P1</div>
        <div>
            <h1>Park<span>One</span></h1>
            <small style="color: #9ca3af; font-size: 0.75rem;">Gestion de Parqueaderos</small>
        </div>
    </div>
    <div class="sub-header-info">
        <% if (userSesion != null) { %>
            <div><strong>Usuario:</strong> <%= userSesion.getNombreCompleto() %> (<%= userSesion.getRol() %>)</div>
            <div><a href="login?accion=logout" style="color: #f87171; text-decoration: underline;">Cerrar Sesión</a></div>
        <% } else { %>
            <div><a href="login.jsp" style="color: #4ade80; text-decoration: underline;">Iniciar Sesión</a></div>
        <% } %>
    </div>
</header>
