/**
 * ============================================================================
 * SISTEMA DE GESTION DE PARQUEADEROS - PARKONE
 * Componente Front-End: Logica de Administracion de Usuarios y Colaboradores
 * Archivo: js/usuarios.js
 * Evidencia: GA7-220501096-AA4-EV03
 * Aprendiz: Brandon Yair Galvis Diaz
 * Ficha: 3336118 - ADSO SENA Regional Cauca - 2026
 * ============================================================================
 * Descripcion:
 * Controla el ciclo de vida de los colaboradores del software ParkOne:
 * validaciones en cliente (cédula única, formato de correo, campos obligatorios),
 * renderizado dinámico de la tabla con roles diferenciados (Administrador / Operador)
 * y persistencia en LocalStorage sincronizada con el estado general.
 * ============================================================================
 */

/**
 * Inicialización de la vista al cargar el documento HTML.
 */
document.addEventListener('DOMContentLoaded', () => {
    renderizarTablaUsuarios();
});

/**
 * Muestra u oculta el formulario de registro de colaborador.
 */
function toggleFormUsuario() {
    const panel = document.getElementById('panelFormUsuario');
    if (!panel) return;

    if (panel.style.display === 'none' || panel.style.display === '') {
        panel.style.display = 'block';
        document.getElementById('txtNombres').focus();
    } else {
        panel.style.display = 'none';
        document.getElementById('formUsuario').reset();
    }
}

/**
 * Despliega alertas flotantes en la vista de usuarios.
 * @param {string} mensaje 
 * @param {string} tipo 'success' o 'danger'
 */
function mostrarAlertaUsuario(mensaje, tipo = 'success') {
    const box = document.getElementById('alertaBox');
    if (!box) return;

    box.innerHTML = `
        <div class="alert alert-${tipo}">
            <span>${tipo === 'success' ? '✔' : '⚠'}</span>
            <div>${mensaje}</div>
        </div>
    `;

    setTimeout(() => {
        if (box.innerHTML.includes(mensaje)) {
            box.innerHTML = '';
        }
    }, 4500);
}

/**
 * Procesa el envío del formulario de usuario, aplicando validaciones de integridad.
 * @param {Event} e 
 */
function procesarGuardarUsuario(e) {
    e.preventDefault();

    // 1. Obtener valores de los campos
    const nombres = document.getElementById('txtNombres').value.trim();
    const apellidos = document.getElementById('txtApellidos').value.trim();
    const cedula = document.getElementById('txtCedula').value.trim();
    const cargo = document.getElementById('txtCargo').value.trim();
    const rol = document.getElementById('selRol').value;
    const correo = document.getElementById('txtCorreo').value.trim().toLowerCase();
    const telefono = document.getElementById('txtTelefono').value.trim();
    const direccion = document.getElementById('txtDireccion').value.trim() || 'Popayán, Cauca';

    // 2. Validación de campos obligatorios
    if (!nombres || !apellidos || !cedula || !cargo || !correo || !telefono) {
        mostrarAlertaUsuario('Por favor complete todos los campos marcados con asterisco (*).', 'danger');
        return;
    }

    // 3. Validación de formato de identificación
    if (!/^\d{6,12}$/.test(cedula)) {
        mostrarAlertaUsuario('El número de cédula debe ser numérico y contener entre 6 y 12 dígitos.', 'danger');
        document.getElementById('txtCedula').focus();
        return;
    }

    // 4. Validación de formato de correo electrónico
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexEmail.test(correo)) {
        mostrarAlertaUsuario('El formato del correo electrónico ingresado no es válido.', 'danger');
        document.getElementById('txtCorreo').focus();
        return;
    }

    const usuarios = getUsuarios();

    // 5. Validación de no duplicidad de identificación
    const existeCedula = usuarios.some(u => u.identificacion === cedula);
    if (existeCedula) {
        mostrarAlertaUsuario(`Ya existe un colaborador registrado con la cédula ${cedula}.`, 'danger');
        document.getElementById('txtCedula').focus();
        return;
    }

    // 6. Generar nuevo objeto de usuario
    const nuevoId = usuarios.length > 0 ? Math.max(...usuarios.map(u => u.id)) + 1 : 1;
    const nuevoUsuario = {
        id: nuevoId,
        identificacion: cedula,
        nombres: nombres,
        apellidos: apellidos,
        cargo: cargo,
        rol: rol,
        correo: correo,
        telefono: telefono,
        direccion: direccion,
        fechaRegistro: new Date().toISOString().split('T')[0]
    };

    // 7. Persistir en LocalStorage
    usuarios.push(nuevoUsuario);
    saveUsuarios(usuarios);

    // 8. Limpiar interfaz
    document.getElementById('formUsuario').reset();
    toggleFormUsuario();
    renderizarTablaUsuarios();

    mostrarAlertaUsuario(`Colaborador ${nombres} ${apellidos} registrado exitosamente con rol de ${rol}.`, 'success');
}

/**
 * Renderiza la lista de usuarios en la tabla aplicando los filtros activos.
 */
function renderizarTablaUsuarios() {
    const usuarios = getUsuarios();
    const filtro = (document.getElementById('txtBuscarUsuario')?.value || '').trim().toUpperCase();

    let filtrados = usuarios;
    if (filtro) {
        filtrados = usuarios.filter(u => 
            u.identificacion.includes(filtro) ||
            u.nombres.toUpperCase().includes(filtro) ||
            u.apellidos.toUpperCase().includes(filtro) ||
            u.cargo.toUpperCase().includes(filtro) ||
            u.rol.toUpperCase().includes(filtro)
        );
    }

    const tbody = document.getElementById('tablaUsuariosBody');
    if (!tbody) return;

    if (filtrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" style="text-align:center; padding:25px; color:#94a3b8;">
                    No se encontraron colaboradores registrados con el criterio de búsqueda ingresado.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filtrados.map(u => {
        const iniciales = (u.nombres[0] || 'U') + (u.apellidos[0] || '');
        const esAdmin = u.rol === 'Administrador';

        return `
            <tr>
                <td><strong>#${u.id}</strong></td>
                <td><strong style="color:#60a5fa; font-family:monospace;">${u.identificacion}</strong></td>
                <td>
                    <div style="display:flex; align-items:center; gap:0.6rem;">
                        <span class="user-avatar-circle" style="background:${esAdmin ? '#f59e0b' : '#39a900'};">${iniciales}</span>
                        <strong>${u.nombres} ${u.apellidos}</strong>
                    </div>
                </td>
                <td>${u.cargo}</td>
                <td>
                    <span class="badge ${esAdmin ? 'badge-warning' : 'badge-info'}">
                        ${u.rol}
                    </span>
                </td>
                <td><a href="mailto:${u.correo}" style="color:#38bdf8; text-decoration:none;">${u.correo}</a></td>
                <td>${u.telefono}</td>
                <td><span style="color:#94a3b8; font-size:0.85rem;">${u.direccion}</span></td>
                <td>
                    ${u.identificacion === '1061723849' ? `
                        <span style="color:#64748b; font-size:0.75rem;">(Principal)</span>
                    ` : `
                        <button onclick="eliminarUsuario(${u.id})" class="btn btn-danger btn-sm" title="Eliminar colaborador">
                            Eliminar
                        </button>
                    `}
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Escucha cambios en el campo de búsqueda de usuarios.
 */
function filtrarUsuarios() {
    renderizarTablaUsuarios();
}

/**
 * Elimina un usuario previa confirmación.
 * @param {number} id 
 */
function eliminarUsuario(id) {
    const usuarios = getUsuarios();
    const usuarioAEliminar = usuarios.find(u => u.id === id);

    if (!usuarioAEliminar) return;

    // Protección para no eliminar el superadmin
    if (usuarioAEliminar.identificacion === '1061723849') {
        mostrarAlertaUsuario('No es posible eliminar al Administrador Principal del sistema.', 'danger');
        return;
    }

    const confirmar = confirm(`¿Está seguro de eliminar al colaborador ${usuarioAEliminar.nombres} ${usuarioAEliminar.apellidos}? Esta acción no se puede deshacer.`);
    if (!confirmar) return;

    const actualizados = usuarios.filter(u => u.id !== id);
    saveUsuarios(actualizados);
    renderizarTablaUsuarios();

    mostrarAlertaUsuario(`El colaborador ${usuarioAEliminar.nombres} ha sido eliminado.`, 'success');
}
