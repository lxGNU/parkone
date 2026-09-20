<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulario Vehículo | ParkOne</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <jsp:include page="includes/header.jsp" />
    <jsp:include page="includes/menu.jsp" />

    <main class="contenedor-principal">
        <div class="titulo-pagina">
            <h2>Ingreso de Vehículo</h2>
            <p>Complete la información del vehículo</p>
        </div>

        <div class="card-container" style="max-width: 850px; margin: 0 auto;">
            <form action="vehiculos" method="post">
                <input type="hidden" name="accion" value="guardar">

                <div class="form-grid">
                    <div class="form-group">
                        <label for="placa">Placa *</label>
                        <input type="text" id="placa" name="placa" required placeholder="Ej: ABC-123" style="text-transform: uppercase;">
                    </div>

                    <div class="form-group">
                        <label for="tipo">Tipo *</label>
                        <select id="tipo" name="tipo" required>
                            <option value="Automóvil">Automóvil</option>
                            <option value="Motocicleta">Motocicleta</option>
                            <option value="Camioneta">Camioneta</option>
                            <option value="Bicicleta">Bicicleta</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="marca">Marca</label>
                        <input type="text" id="marca" name="marca" placeholder="Marca del vehículo">
                    </div>

                    <div class="form-group">
                        <label for="color">Color</label>
                        <input type="text" id="color" name="color" placeholder="Color del vehículo">
                    </div>

                    <div class="form-group">
                        <label for="propietarioCedula">Cédula del Propietario *</label>
                        <input type="text" id="propietarioCedula" name="propietarioCedula" required placeholder="Número de cédula">
                    </div>

                    <div class="form-group">
                        <label for="propietarioNombre">Nombre del Propietario *</label>
                        <input type="text" id="propietarioNombre" name="propietarioNombre" required placeholder="Nombre del propietario">
                    </div>

                    <div class="form-group">
                        <label for="tarifaPorHora">Tarifa por Hora ($) *</label>
                        <input type="number" id="tarifaPorHora" name="tarifaPorHora" step="500" value="4000" required>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="vehiculos?accion=listar" class="btn btn-danger">Cancelar</a>
                    <button type="submit" class="btn btn-success">Registrar Vehículo</button>
                </div>
            </form>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />

</body>
</html>
