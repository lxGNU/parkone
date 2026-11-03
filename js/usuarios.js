/**
 * Proyecto ParkOne - Modulo de Gestion de Usuarios
 * Aprendiz: Brandon Yair Galvis Diaz - SENA ADSO
 */

// Abre o cierra el formulario para agregar un nuevo usuario
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

// Muestra mensajes de alerta en pantalla
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
    }, 4000);
}

// Guarda un nuevo colaborador validando los campos
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

    // Validamos que no queden campos en blanco
    if (!cedula || !nombres || !apellidos || !cargo || !correo || !telefono) {
        mostrarAlertaUsuario('Por favor llene todos los campos obligatorios.', 'danger');
        return;
    }

    // Validamos que la cedula contenga solo numeros
    if (!/^\d{6,12}$/.test(cedula)) {
        mostrarAlertaUsuario('La cédula debe ser numérica de 6 a 12 dígitos.', 'danger');
        document.getElementById('inCedula').focus();
        return;
    }

    const usuarios = getUsuarios();

    // Validamos que la cedula no este registrada
    if (usuarios.some(u => u.identificacion === cedula)) {
        mostrarAlertaUsuario(`Ya existe un usuario con la cédula ${cedula}.`, 'danger');
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

    mostrarAlertaUsuario(`Usuario ${nombres} ${apellidos} registrado con éxito.`, 'success');
}

// Carga la lista de usuarios en la tabla
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
                    No se encontraron usuarios.
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
                        <button onclick="eliminarUsuario(${u.id})" class="btn btn-danger btn-sm">
                            Eliminar
                        </button>
                    `}
                </td>
            </tr>
        `;
    }).join('');
}

// Filtro de busqueda por cedula o nombre
function filtrarUsuarios() {
    renderizarTablaUsuarios();
}

// Limpia el buscador de usuarios
function limpiarFiltroUser() {
    const input = document.getElementById('txtBuscarUser');
    if (input) input.value = '';
    renderizarTablaUsuarios();
}

// Elimina un usuario pidiendo confirmacion
function eliminarUsuario(id) {
    const usuarios = getUsuarios();
    const u = usuarios.find(item => item.id === id);
    if (!u) return;

    if (u.identificacion === '1061723849') {
        mostrarAlertaUsuario('No se puede eliminar el usuario administrador principal.', 'danger');
        return;
    }

    if (!confirm(`¿Desea eliminar al usuario ${u.nombres} ${u.apellidos}?`)) return;

    const actualizados = usuarios.filter(item => item.id !== id);
    saveUsuarios(actualizados);
    renderizarTablaUsuarios();
    mostrarAlertaUsuario(`Usuario ${u.nombres} eliminado.`, 'success');
}
