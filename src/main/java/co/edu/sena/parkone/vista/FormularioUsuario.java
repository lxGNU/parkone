package co.edu.sena.parkone.vista;

import co.edu.sena.parkone.dao.UsuarioDAO;
import co.edu.sena.parkone.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FormularioUsuario extends JFrame {

    private UsuarioDAO dao;

    // Componentes del formulario
    private JTextField txtId;
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtCorreo;
    private JTextField txtCelular;
    private JPasswordField txtClave;
    private JComboBox<String> cbRol;

    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public FormularioUsuario() {
        dao = new UsuarioDAO();
        iniciarComponentes();
        cargarTabla();
    }

    private void iniciarComponentes() {
        setTitle("Modulo de Usuarios - ParkOne");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con el titulo
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(24, 43, 73));
        JLabel lblTitulo = new JLabel("PARK ONE - REGISTRO DE USUARIOS");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel con los campos de texto
        JPanel panelCampos = new JPanel(new GridLayout(4, 4, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        txtId = new JTextField();
        txtId.setEditable(false);
        txtCedula = new JTextField();
        txtNombres = new JTextField();
        txtApellidos = new JTextField();
        txtCorreo = new JTextField();
        txtCelular = new JTextField();
        txtClave = new JPasswordField();
        cbRol = new JComboBox<>(new String[]{"Operador", "Administrador", "Usuario"});

        panelCampos.add(new JLabel("ID:"));
        panelCampos.add(txtId);
        panelCampos.add(new JLabel("Cedula:"));
        panelCampos.add(txtCedula);

        panelCampos.add(new JLabel("Nombres:"));
        panelCampos.add(txtNombres);
        panelCampos.add(new JLabel("Apellidos:"));
        panelCampos.add(txtApellidos);

        panelCampos.add(new JLabel("Correo:"));
        panelCampos.add(txtCorreo);
        panelCampos.add(new JLabel("Celular:"));
        panelCampos.add(txtCelular);

        panelCampos.add(new JLabel("Rol:"));
        panelCampos.add(cbRol);
        panelCampos.add(new JLabel("Contrasena:"));
        panelCampos.add(txtClave);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelCampos, BorderLayout.NORTH);
        panelCentro.add(panelBotones, BorderLayout.CENTER);

        // Tabla para ver los registros
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Cedula", "Nombres", "Apellidos", "Correo", "Celular", "Rol"}, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(800, 220));
        panelCentro.add(scroll, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        // Eventos de los botones
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        // Evento al hacer clic en una fila de la tabla
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtCedula.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtNombres.setText(modeloTabla.getValueAt(fila, 2).toString());
                    txtApellidos.setText(modeloTabla.getValueAt(fila, 3).toString());
                    txtCorreo.setText(modeloTabla.getValueAt(fila, 4).toString());
                    txtCelular.setText(modeloTabla.getValueAt(fila, 5).toString());
                    cbRol.setSelectedItem(modeloTabla.getValueAt(fila, 6).toString());
                }
            }
        });
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Usuario> lista = dao.listar();
        for (Usuario u : lista) {
            modeloTabla.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getIdentificacion(),
                    u.getNombres(),
                    u.getApellidos(),
                    u.getCorreo(),
                    u.getCelular(),
                    u.getRol()
            });
        }
    }

    private void guardar() {
        String cedula = txtCedula.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String correo = txtCorreo.getText().trim();
        String celular = txtCelular.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();
        String rol = cbRol.getSelectedItem().toString();

        if (cedula.isEmpty() || nombres.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete los campos obligatorios.");
            return;
        }

        Usuario u = new Usuario(cedula, nombres, apellidos, correo, celular, clave, rol);
        if (dao.insertar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario guardado con exito.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el usuario.");
        }
    }

    private void modificar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        String cedula = txtCedula.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String correo = txtCorreo.getText().trim();
        String celular = txtCelular.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();
        String rol = cbRol.getSelectedItem().toString();

        Usuario u = new Usuario(id, cedula, nombres, apellidos, correo, celular, clave, rol);
        if (dao.actualizar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario modificado correctamente.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar el usuario.");
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para eliminar.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        int confirmar = JOptionPane.showConfirmDialog(this, "Esta seguro de eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            if (dao.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
            }
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCorreo.setText("");
        txtCelular.setText("");
        txtClave.setText("");
        cbRol.setSelectedIndex(0);
        tabla.clearSelection();
    }
}
