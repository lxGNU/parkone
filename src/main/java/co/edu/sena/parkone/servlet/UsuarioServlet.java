package co.edu.sena.parkone.servlet;

import co.edu.sena.parkone.dao.UsuarioDAO;
import co.edu.sena.parkone.modelo.Usuario;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

// Servlet para la gestion de usuarios
@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioDAO = new UsuarioDAO();
    }

    // Metodo GET para mostrar paginas y buscar
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // Validar rol de administrador
        if (!esAdmin(request)) {
            request.setAttribute("mensajeError", "Acceso denegado. Se requieren permisos de Administrador.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "editar":
                mostrarFormularioEditar(request, response);
                break;
            case "eliminar":
                eliminarUsuario(request, response);
                break;
            case "buscar":
                buscarUsuarios(request, response);
                break;
            case "listar":
            default:
                listarUsuarios(request, response);
                break;
        }
    }

    // Metodo POST para guardar y actualizar usuarios
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        if (!esAdmin(request)) {
            request.setAttribute("mensajeError", "Acceso denegado. Se requieren permisos de Administrador.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        String accion = request.getParameter("accion");
        if ("actualizar".equalsIgnoreCase(accion)) {
            actualizarUsuario(request, response);
        } else {
            guardarUsuario(request, response);
        }
    }

    private boolean esAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
            return u != null && "Administrador".equalsIgnoreCase(u.getRol());
        }
        return false;
    }

    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Usuario> listaUsuarios = usuarioDAO.listar();
        request.setAttribute("listaUsuarios", listaUsuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("usuarios.jsp");
        dispatcher.forward(request, response);
    }

    private void buscarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String criterio = request.getParameter("criterio");
        List<Usuario> listaUsuarios;
        if (criterio != null && !criterio.trim().isEmpty()) {
            listaUsuarios = usuarioDAO.buscarPorCriterio(criterio.trim());
            request.setAttribute("criterio", criterio);
        } else {
            listaUsuarios = usuarioDAO.listar();
        }
        request.setAttribute("listaUsuarios", listaUsuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("usuarios.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("modo", "nuevo");
        RequestDispatcher dispatcher = request.getRequestDispatcher("formulario-usuario.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Usuario usuario = usuarioDAO.buscarPorId(id);
            request.setAttribute("usuarioEditar", usuario);
            request.setAttribute("modo", "editar");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("formulario-usuario.jsp");
        dispatcher.forward(request, response);
    }

    private void guardarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String identificacion = request.getParameter("identificacion");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String cargo = request.getParameter("cargo");
        String rol = request.getParameter("rol");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");

        Usuario nuevoUsuario = new Usuario(identificacion, nombres, apellidos, cargo, rol, correo, contrasena, telefono, direccion);
        boolean exito = usuarioDAO.insertar(nuevoUsuario);

        if (exito) {
            request.setAttribute("mensajeExito", "Usuario registrado con exito.");
        } else {
            request.setAttribute("mensajeError", "Error al registrar el usuario.");
        }

        listarUsuarios(request, response);
    }

    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
        String identificacion = request.getParameter("identificacion");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String cargo = request.getParameter("cargo");
        String rol = request.getParameter("rol");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");

        Usuario usuario = new Usuario(idUsuario, identificacion, nombres, apellidos, cargo, rol, correo, contrasena, telefono, direccion, null);
        boolean exito = usuarioDAO.actualizar(usuario);

        if (exito) {
            request.setAttribute("mensajeExito", "Usuario actualizado con exito.");
        } else {
            request.setAttribute("mensajeError", "No se pudo actualizar el usuario.");
        }

        listarUsuarios(request, response);
    }

    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            boolean exito = usuarioDAO.eliminar(id);
            if (exito) {
                request.setAttribute("mensajeExito", "Usuario eliminado con exito.");
            } else {
                request.setAttribute("mensajeError", "No se pudo eliminar el usuario.");
            }
        }
        listarUsuarios(request, response);
    }
}
