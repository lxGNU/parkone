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

// Servlet para el inicio y cierre de sesion
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioDAO = new UsuarioDAO();
    }

    // Metodo GET para mostrar el formulario de login o cerrar sesion
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String accion = request.getParameter("accion");

        if ("logout".equalsIgnoreCase(accion)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            request.setAttribute("mensajeExito", "Sesion cerrada correctamente.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }
    }

    // Metodo POST para procesar las credenciales enviadas
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String usuarioInput = request.getParameter("usuario");
        String claveInput = request.getParameter("contrasena");

        Usuario u = usuarioDAO.validarLogin(usuarioInput, claveInput);

        if (u != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioLogueado", u);
            response.sendRedirect("index.jsp");
        } else {
            request.setAttribute("mensajeError", "Datos incorrectos. Verifique su correo o cedula y contraseña.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }
    }
}
