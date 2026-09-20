package co.edu.sena.parkone.servlet;

import co.edu.sena.parkone.dao.VehiculoDAO;
import co.edu.sena.parkone.modelo.Vehiculo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// Servlet para la gestion de vehiculos
@WebServlet("/vehiculos")
public class VehiculoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private VehiculoDAO vehiculoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.vehiculoDAO = new VehiculoDAO();
    }

    // Metodo GET para mostrar vehiculos y buscar
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "cambiarEstado":
                cambiarEstadoVehiculo(request, response);
                break;
            case "eliminar":
                eliminarVehiculo(request, response);
                break;
            case "buscar":
                buscarVehiculos(request, response);
                break;
            case "listar":
            default:
                listarVehiculos(request, response);
                break;
        }
    }

    // Metodo POST para guardar vehiculos
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        guardarVehiculo(request, response);
    }

    private void listarVehiculos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Vehiculo> lista = vehiculoDAO.listar();
        request.setAttribute("listaVehiculos", lista);
        RequestDispatcher dispatcher = request.getRequestDispatcher("vehiculos.jsp");
        dispatcher.forward(request, response);
    }

    private void buscarVehiculos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filtro = request.getParameter("filtro");
        List<Vehiculo> lista;
        if (filtro != null && !filtro.trim().isEmpty()) {
            lista = vehiculoDAO.buscarPorPlacaOCedula(filtro.trim());
            request.setAttribute("filtro", filtro);
        } else {
            lista = vehiculoDAO.listar();
        }
        request.setAttribute("listaVehiculos", lista);
        RequestDispatcher dispatcher = request.getRequestDispatcher("vehiculos.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("formulario-vehiculo.jsp");
        dispatcher.forward(request, response);
    }

    private void guardarVehiculo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String placa = request.getParameter("placa");
        String tipo = request.getParameter("tipo");
        String marca = request.getParameter("marca");
        String color = request.getParameter("color");
        String propietarioCedula = request.getParameter("propietarioCedula");
        String propietarioNombre = request.getParameter("propietarioNombre");
        double tarifaPorHora = Double.parseDouble(request.getParameter("tarifaPorHora"));

        Vehiculo v = new Vehiculo(placa, tipo, marca, color, propietarioCedula, propietarioNombre, "Estacionado", tarifaPorHora);
        boolean exito = vehiculoDAO.insertar(v);

        if (exito) {
            request.setAttribute("mensajeExito", "Vehiculo registrado con exito.");
        } else {
            request.setAttribute("mensajeError", "Error al registrar el vehiculo.");
        }

        listarVehiculos(request, response);
    }

    private void cambiarEstadoVehiculo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String nuevoEstado = request.getParameter("estado");
        if (idStr != null && nuevoEstado != null) {
            int id = Integer.parseInt(idStr);
            vehiculoDAO.cambiarEstado(id, nuevoEstado);
            request.setAttribute("mensajeExito", "Estado actualizado: " + nuevoEstado);
        }
        listarVehiculos(request, response);
    }

    private void eliminarVehiculo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            vehiculoDAO.eliminar(id);
            request.setAttribute("mensajeExito", "Vehiculo eliminado.");
        }
        listarVehiculos(request, response);
    }
}
