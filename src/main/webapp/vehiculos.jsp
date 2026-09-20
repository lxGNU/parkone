<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="co.edu.sena.parkone.modelo.Vehiculo" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehículos | ParkOne</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <jsp:include page="includes/header.jsp" />
    <jsp:include page="includes/menu.jsp" />

    <main class="contenedor-principal">
        <div class="titulo-pagina">
            <h2>Gestión de Vehículos</h2>
            <p>Lista de vehículos ingresados</p>
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
                <form action="vehiculos" method="get" class="search-bar">
                    <input type="hidden" name="accion" value="buscar">
                    <input type="text" name="filtro" placeholder="Buscar por placa o cédula..." value="<%= request.getAttribute("filtro") != null ? request.getAttribute("filtro") : "" %>">
                    <button type="submit" class="btn btn-primary">Buscar</button>
                    <% if (request.getAttribute("filtro") != null) { %>
                        <a href="vehiculos?accion=listar" class="btn btn-danger btn-sm">Limpiar</a>
                    <% } %>
                </form>

                <a href="vehiculos?accion=nuevo" class="btn btn-success">+ Ingresar Vehículo</a>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Placa</th>
                            <th>Tipo</th>
                            <th>Marca / Color</th>
                            <th>Propietario</th>
                            <th>Cédula</th>
                            <th>Estado</th>
                            <th>Tarifa/Hora</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Vehiculo> lista = (List<Vehiculo>) request.getAttribute("listaVehiculos");
                            if (lista != null && !lista.isEmpty()) {
                                for (Vehiculo v : lista) {
                                    boolean estacionado = "Estacionado".equalsIgnoreCase(v.getEstado());
                        %>
                        <tr>
                            <td><strong style="color: #60a5fa;"><%= v.getPlaca() %></strong></td>
                            <td><%= v.getTipo() %></td>
                            <td><%= v.getMarca() %> (<%= v.getColor() %>)</td>
                            <td><%= v.getPropietarioNombre() %></td>
                            <td><%= v.getPropietarioCedula() %></td>
                            <td>
                                <span class="badge <%= estacionado ? "badge-parked" : "badge-exit" %>">
                                    <%= v.getEstado() %>
                                </span>
                            </td>
                            <td>$<%= String.format("%.2f", v.getTarifaPorHora()) %></td>
                            <td>
                                <div style="display: flex; gap: 0.4rem;">
                                    <% if (estacionado) { %>
                                        <a href="vehiculos?accion=cambiarEstado&id=<%= v.getIdVehiculo() %>&estado=Retirado" class="btn btn-primary btn-sm">Marcar Salida</a>
                                    <% } else { %>
                                        <a href="vehiculos?accion=cambiarEstado&id=<%= v.getIdVehiculo() %>&estado=Estacionado" class="btn btn-success btn-sm">Re-ingresar</a>
                                    <% } %>
                                    <a href="vehiculos?accion=eliminar&id=<%= v.getIdVehiculo() %>" class="btn btn-danger btn-sm" onclick="return confirm('¿Eliminar vehículo?');">Eliminar</a>
                                </div>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2rem;">
                                No hay vehículos registrados.
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
