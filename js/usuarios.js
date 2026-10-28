/**
 * ============================================================================
 * SISTEMA DE GESTION DE PARQUEADEROS - PARKONE
 * Modulo Front-End: Gestion y Registro de Usuarios / Colaboradores
 * Archivo: js/usuarios.js
 * Evidencia: GA7-220501096-AA4-EV03
 * Aprendiz: Brandon Yair Galvis Diaz
 * Ficha: 3336118 - ADSO SENA Regional Cauca - 2026
 * ============================================================================
 * Descripcion:
 * Este script implementa la logica de interaccion, validacion de campos en 
 * tiempo real, captura de eventos y persistencia en LocalStorage para el 
 * modulo de colaboradores del software ParkOne.
 * ============================================================================
 */

/**
 * Alterna la visualizacion del panel para nuevo colaborador.
 */
function toggleFormUsuario() {
    const panel = document.getElementById('panelNuevoUsuario');
    if (!panel) return;

    if (panel.style.display === 'none' || panel.style.display === '') {
        panel.style.display = 'block';
        document.getElementById('inCedula').focus();
    } else {
        panel.style.display = 'none';
        document.getElementById('formUsuario').reset();
    }
}

/**
 * Muestra alertas visuales temporales.
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
 * Procesa el formulario de registro de colaborador con validaciones.
 * @param {Event} e 
 */
function guardarUsuario(e) {
    e.preventDefault();

    const cedula = document.getElementById('inCedula').value.trim();
    const nombres = document.getElementById('inNombres').value.trim();
    const apellidos = document.getElementById('inApellidos').value.trim();
    const cargo = document.getElementById('inCargo').value.trim();
    const rol = document.getElementById('inRol').value;
    const correo = document.getElementById('inCorreo').value.trim();
    const telefono = document.getElementById('inTelefono').value.trim();
    const direccion = document.getElementById('inDireccion').value.trim() || 'Popayán, Cauca';

    // 1. Validar campos requeridos
    if (!cedula || !nombres || !apellidos || !cargo || !correo || !telefono) {
        mostrarAlertaUsuario('Por favor complete todos los campos obligatorios (*).', 'danger');
        return;
    }

    // 2. Validar cedula numerica
    if (!/^\d{6,12}$/.test(cedula)) {
        mostrarAlertaUsuario('La cédula debe ser numérica y contener entre 6 y 12 dígitos.', 'danger');
        document.getElementById('inCedula').focus();
        return;
    }

    const usuarios = getUsuarios();

    // 3. Validar cedula duplicada
    if (usuarios.some(u => u.identificacion === cedula)) {
        mostrarAlertaUsuario(`Ya existe un colaborador registrado con la cédula ${cedula}.`, 'danger');
        document.getElementById('inCedula').focus();
        return;
    }

    const nuevoId = usuarios.length > 0 ? Math.max(...usuarios.map(u => u.id)) + 1 : 1;
    const nuevo = {
        id: nuevoId,
        identificacion: cedula,
        nombres: nombres,
        apellidos: apellidos,
        cargo: cargo,
        rol: rol,
        correo: correo,
        telefono: telefono,
        direccion: direccion
    };

    usuarios.push(nuevo);
    saveUsuarios(usuarios);

    document.getElementById('formUsuario').reset();
    toggleFormUsuario();
    renderizarTablaUsuarios();

    mostrarAlertaUsuario(`Colaborador ${nombres} ${apellidos} registrado exitosamente.`, 'success');
}

/**
 * Renderiza la tabla de colaboradores.
 */
function renderizarTablaUsuarios() {
    const usuarios = getUsuarios();
    const filtro = (document.getElementById('txtBuscarUser')?.value || '').trim().toUpperCase();

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
                <td colspan="8" style="text-align:center; padding:25px; color:#94a3b8;">
                    No se encontraron colaboradores registrados.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filtrados.map(u => {
        const esAdmin = u.rol === 'Administrador';
        return `
            <tr>
                <td>${u.id}</td>
                <td><strong>${u.identificacion}</strong></td>
                <td>${u.nombres} ${u.apellidos}</td>
                <td>${u.cargo}</td>
                <td>
                    <span class="badge ${esAdmin ? 'badge-admin' : 'badge-user'}">
                        ${u.rol}
                    </span>
                </td>
                <td>${u.correo}</td>
                <td>${u.telefono}</td>
                <td>
                    ${u.identificacion === '1061723849' ? `
                        <span style="color:#64748b; font-size:0.75rem;">(Principal)</span>
                    ` : `
                        <button onclick="eliminarUsuario(${u.id})" class="btn btn-danger btn-sm" title="Eliminar usuario">
                            Eliminar
                        </button>
                    `}
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Filtra los usuarios en tiempo real.
 */
function filtrarUsuarios() {
    renderizarTablaUsuarios();
}

/**
 * Limpia el filtro de usuarios.
 */
function limpiarFiltroUser() {
    const input = document.getElementById('txtBuscarUser');
    if (input) input.value = '';
    renderizarTablaUsuarios();
}

/**
 * Elimina un usuario con confirmacion.
 * @param {number} id 
 */
function eliminarUsuario(id) {
    const usuarios = getUsuarios();
    const u = usuarios.find(item => item.id === id);
    if (!u) return;

    if (u.identificacion === '1061723849') {
        mostrarAlertaUsuario('No se puede eliminar el usuario administrador principal.', 'danger');
        return;
    }

    if (!confirm(`¿Está seguro de eliminar a ${u.nombres} ${u.apellidos}?`)) return;

    const actualizados = usuarios.filter(item => item.id !== id);
    saveUsuarios(actualizados);
    renderizarTablaUsuarios();
    mostrarAlertaUsuario(`Colaborador ${u.nombres} eliminado correctamente.`, 'success');
}
