/**
 * ============================================================================
 * SISTEMA DE GESTION DE PARQUEADEROS - PARKONE
 * Modulo Front-End: Control de Parqueadero (Entradas, Salidas y Cobro)
 * Archivo: js/inventario.js
 * Evidencia: GA7-220501096-AA4-EV03
 * Aprendiz: Brandon Yair Galvis Diaz
 * Ficha: 3336118 - ADSO SENA Regional Cauca - 2026
 * ============================================================================
 * Descripcion:
 * Gestiona el registro operativo de ingresos, la validacion estricta de 
 * placas duplicadas en patio, la emision de tickets termicos, y la liquidacion
 * de salidas con calculo automatico de tiempo transcurrido y tarifa.
 * ============================================================================
 */

let registroEnLiquidacion = null;
let calculoCobroActual = null;

/**
 * Alterna la visualizacion del formulario de nuevo ingreso.
 */
function toggleFormIngreso() {
    const panel = document.getElementById('panelNuevoIngreso');
    if (!panel) return;

    if (panel.style.display === 'none' || panel.style.display === '') {
        panel.style.display = 'block';
        document.getElementById('inPlaca').focus();
    } else {
        panel.style.display = 'none';
        document.getElementById('formIngreso').reset();
    }
}

/**
 * Muestra alertas visuales de exito o error en pantalla.
 * @param {string} mensaje 
 * @param {string} tipo 'success' o 'danger'
 */
function mostrarAlerta(mensaje, tipo = 'success') {
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
 * Procesa el formulario de entrada vehicular con validaciones de placa.
 * @param {Event} e 
 */
function guardarIngreso(e) {
    e.preventDefault();

    const placa = document.getElementById('inPlaca').value.trim().toUpperCase();
    const tipo = document.getElementById('inTipo').value;
    const propietario = document.getElementById('inPropietario').value.trim();
    const bahia = document.getElementById('inObservacion').value.trim() || 'General';

    // 1. Validar longitud minima de placa colombiana
    if (!placa || placa.length < 5) {
        mostrarAlerta('La placa ingresada debe tener al menos 5 caracteres alfanumericos.', 'danger');
        document.getElementById('inPlaca').focus();
        return;
    }

    const registros = getRegistros();

    // 2. REGLA DE NEGOCIO (HU04): Bloqueo estricto de placa duplicada en patio
    const yaEstaEnPatio = registros.some(r => r.placa === placa && r.estado === 'ACTIVO');
    if (yaEstaEnPatio) {
        mostrarAlerta(`RESTRICCIÓN: El vehículo con placa [${placa}] ya se encuentra registrado actualmente dentro del parqueadero.`, 'danger');
        document.getElementById('inPlaca').focus();
        return;
    }

    // 3. Validar capacidad maxima
    const ocupados = registros.filter(r => r.estado === 'ACTIVO').length;
    if (ocupados >= CAPACIDAD_MAXIMA) {
        mostrarAlerta('CAPACIDAD MÁXIMA ALCANZADA: No hay bahías libres disponibles.', 'danger');
        return;
    }

    const tarifaAplicada = TARIFAS_HORA[tipo] || 3500;
    const nuevoId = registros.length > 0 ? Math.max(...registros.map(r => r.id)) + 1 : 101;
    const fechaHora = obtenerFechaHoraActual();

    const nuevo = {
        id: nuevoId,
        placa: placa,
        tipo: tipo,
        propietario: propietario,
        observaciones: bahia,
        horaIngreso: fechaHora,
        horaSalida: null,
        tarifaAplicada: tarifaAplicada,
        totalPagado: 0,
        estado: 'ACTIVO'
    };

    registros.unshift(nuevo);
    saveRegistros(registros);

    document.getElementById('formIngreso').reset();
    toggleFormIngreso();
    renderizarTablaControl();

    mostrarAlerta(`Vehículo [${placa}] ingresado exitosamente en la celda ${bahia}.`, 'success');
    mostrarTicketTermicoEntrada(nuevo);
}

/**
 * Renderiza la tabla de registros operativos en control.html.
 */
function renderizarTablaControl() {
    const registros = getRegistros();
    const filtro = (document.getElementById('txtBuscar')?.value || '').trim().toUpperCase();

    let filtrados = registros;
    if (filtro) {
        filtrados = registros.filter(r => 
            r.placa.toUpperCase().includes(filtro) ||
            r.propietario.toUpperCase().includes(filtro) ||
            (r.observaciones && r.observaciones.toUpperCase().includes(filtro))
        );
    }

    const tbody = document.getElementById('tablaControlBody');
    if (!tbody) return;

    if (filtrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="10" style="text-align:center; padding:25px; color:#94a3b8;">
                    No se encontraron registros de parqueadero.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filtrados.map(r => `
        <tr>
            <td>${r.id}</td>
            <td><strong style="color:#60a5fa;">${r.placa}</strong></td>
            <td>${r.tipo}</td>
            <td>${r.propietario}</td>
            <td><span style="color:#4ade80; font-weight:bold;">${r.observaciones || 'General'}</span></td>
            <td>${r.horaIngreso}</td>
            <td>${r.horaSalida || '<em style="color:#64748b;">En patio</em>'}</td>
            <td><strong>$${(Number(r.totalPagado) || 0).toLocaleString('es-CO')}</strong></td>
            <td>
                <span class="badge ${r.estado === 'ACTIVO' ? 'badge-parked' : 'badge-exit'}">
                    ${r.estado === 'ACTIVO' ? 'Estacionado' : 'Finalizado'}
                </span>
            </td>
            <td>
                ${r.estado === 'ACTIVO' ? `
                    <button onclick="liquidarSalida('${r.placa}')" class="btn btn-danger btn-sm" title="Cobrar salida">
                        Cobrar Salida
                    </button>
                    <button onclick="reimprimirTicket(${r.id})" class="btn btn-secondary btn-sm" title="Ticket de entrada">
                        Ticket
                    </button>
                ` : `
                    <button onclick="reimprimirReciboFinalizado(${r.id})" class="btn btn-secondary btn-sm" title="Ver comprobante de pago">
                        Recibo
                    </button>
                `}
            </td>
        </tr>
    `).join('');
}

/**
 * Filtra los registros por texto de placa o nombre.
 */
function filtrarRegistros() {
    renderizarTablaControl();
}

/**
 * Limpia el campo de filtro y restablece la tabla.
 */
function limpiarFiltro() {
    const input = document.getElementById('txtBuscar');
    if (input) input.value = '';
    renderizarTablaControl();
}

/**
 * Despliega el modal de liquidacion calculando el tiempo transcurrido y tarifa.
 * @param {string} placa 
 */
function liquidarSalida(placa) {
    const registros = getRegistros();
    const r = registros.find(item => item.placa === placa && item.estado === 'ACTIVO');

    if (!r) {
        mostrarAlerta(`No se encontró vehículo activo con placa ${placa}.`, 'danger');
        return;
    }

    registroEnLiquidacion = r;
    const horaSalida = obtenerFechaHoraActual();
    
    // Calcular tiempo
    const inicio = new Date(r.horaIngreso.replace(' ', 'T'));
    const fin = new Date(horaSalida.replace(' ', 'T'));
    let diffMs = fin.getTime() - inicio.getTime();
    if (diffMs < 0) diffMs = 0;
    
    const diffMinutos = Math.max(1, Math.round(diffMs / 60000));
    const horasFacturadas = Math.max(1, Math.ceil(diffMinutos / 60));
    const tarifa = Number(r.tarifaAplicada) || 3500;
    const totalPagar = horasFacturadas * tarifa;

    calculoCobroActual = {
        horaSalida: horaSalida,
        minutos: diffMinutos,
        horas: horasFacturadas,
        total: totalPagar
    };

    const modalContenido = document.getElementById('contenidoModalSalida');
    modalContenido.innerHTML = `
        <div><strong>Placa:</strong> <span style="color:#60a5fa; font-size:1.1rem; font-weight:bold;">${r.placa}</span></div>
        <div><strong>Tipo:</strong> ${r.tipo} | <strong>Bahía:</strong> ${r.observaciones || 'General'}</div>
        <div><strong>Propietario:</strong> ${r.propietario}</div>
        <hr style="border:none; border-top:1px dashed #334155; margin:10px 0;">
        <div><strong>Hora Entrada:</strong> ${r.horaIngreso}</div>
        <div><strong>Hora Salida:</strong> ${horaSalida}</div>
        <div><strong>Tiempo Transcurrido:</strong> ${diffMinutos} min (${horasFacturadas} hora(s) facturadas)</div>
        <div><strong>Tarifa / Hora:</strong> $${tarifa.toLocaleString('es-CO')}</div>
        <div style="margin-top:12px; padding:12px; background:#0f172a; border-radius:8px; border:1px solid #10b981; font-size:1.2rem; font-weight:bold; color:#10b981; text-align:center;">
            TOTAL A COBRAR: $${totalPagar.toLocaleString('es-CO')} COP
        </div>
    `;

    const modal = document.getElementById('modalSalida');
    if (modal) modal.style.display = 'flex';
}

/**
 * Cierra la ventana modal de liquidación.
 */
function cerrarModalSalida() {
    const modal = document.getElementById('modalSalida');
    if (modal) modal.style.display = 'none';
    registroEnLiquidacion = null;
    calculoCobroActual = null;
}

/**
 * Confirma el cobro de la salida, actualiza el registro en LocalStorage y emite el recibo.
 */
function confirmarCobroSalida() {
    if (!registroEnLiquidacion || !calculoCobroActual) return;

    const registros = getRegistros();
    const idx = registros.findIndex(r => r.id === registroEnLiquidacion.id);
    if (idx === -1) return;

    registros[idx].horaSalida = calculoCobroActual.horaSalida;
    registros[idx].totalPagado = calculoCobroActual.total;
    registros[idx].estado = 'FINALIZADO';

    saveRegistros(registros);
    cerrarModalSalida();
    renderizarTablaControl();

    mostrarAlerta(`Salida de la placa [${registros[idx].placa}] registrada con éxito. Total cobrado: $${calculoCobroActual.total.toLocaleString('es-CO')}.`, 'success');
    mostrarTicketTermicoSalida(registros[idx], calculoCobroActual);
}

/**
 * Muestra el ticket térmico de entrada.
 * @param {Object} r 
 */
function mostrarTicketTermicoEntrada(r) {
    const contenedor = document.getElementById('ticketContenido');
    contenedor.innerHTML = `
        <div style="text-align:center; border-bottom:1px dashed #000; padding-bottom:8px; margin-bottom:8px;">
            <h3 style="margin:0; font-size:1.2rem;">PARK ONE</h3>
            <small>Framework Web & Móvil de Gestión de Parqueaderos</small><br>
            <small>SENA Regional Cauca - Popayán</small><br>
            <strong>TICKET DE ENTRADA</strong>
        </div>
        <div><strong>Ticket N°:</strong> #ENT-${r.id}</div>
        <div><strong>Fecha/Hora:</strong> ${r.horaIngreso}</div>
        <div><strong>Placa:</strong> <span style="font-size:1.2rem; font-weight:bold; border:1px solid #000; padding:1px 4px;">${r.placa}</span></div>
        <div><strong>Tipo:</strong> ${r.tipo}</div>
        <div><strong>Bahía:</strong> ${r.observaciones || 'General'}</div>
        <div><strong>Conductor:</strong> ${r.propietario}</div>
        <div><strong>Tarifa Base:</strong> $${Number(r.tarifaAplicada).toLocaleString('es-CO')}/h</div>
        <div style="text-align:center; font-size:0.75rem; margin-top:10px; border-top:1px dashed #000; padding-top:6px;">
            Conserve este comprobante para retirar su vehículo.<br>
            ||||| ||| |||| || |||||
        </div>
    `;

    document.getElementById('ticketModal').style.display = 'block';
}

/**
 * Muestra el comprobante térmico de salida.
 * @param {Object} r 
 * @param {Object} cobro 
 */
function mostrarTicketTermicoSalida(r, cobro) {
    const contenedor = document.getElementById('ticketContenido');
    contenedor.innerHTML = `
        <div style="text-align:center; border-bottom:1px dashed #000; padding-bottom:8px; margin-bottom:8px;">
            <h3 style="margin:0; font-size:1.2rem;">PARK ONE</h3>
            <small>Framework Web & Móvil de Gestión de Parqueaderos</small><br>
            <small>SENA Regional Cauca - Popayán</small><br>
            <strong>COMPROBANTE DE PAGO</strong>
        </div>
        <div><strong>Factura N°:</strong> #FAC-${r.id}</div>
        <div><strong>Placa:</strong> <span style="font-size:1.2rem; font-weight:bold;">${r.placa}</span></div>
        <div><strong>Tipo:</strong> ${r.tipo}</div>
        <div><strong>Conductor:</strong> ${r.propietario}</div>
        <div><strong>Entrada:</strong> ${r.horaIngreso}</div>
        <div><strong>Salida:</strong> ${r.horaSalida}</div>
        <div><strong>Tiempo:</strong> ${cobro.minutos} min (${cobro.horas} hora(s))</div>
        <div style="font-size:1.2rem; font-weight:bold; margin:8px 0; border-top:1px dashed #000; border-bottom:1px dashed #000; padding:6px 0; display:flex; justify-content:space-between;">
            <span>TOTAL PAGADO:</span>
            <span>$${cobro.total.toLocaleString('es-CO')}</span>
        </div>
        <div style="text-align:center; font-size:0.75rem;">
            ¡Gracias por preferir a ParkOne!<br>
            |||| ||||| ||| |||| |||
        </div>
    `;

    document.getElementById('ticketModal').style.display = 'block';
}

/**
 * Reimprime el ticket de entrada.
 * @param {number} id 
 */
function reimprimirTicket(id) {
    const registros = getRegistros();
    const r = registros.find(item => item.id === id);
    if (r) mostrarTicketTermicoEntrada(r);
}

/**
 * Muestra el comprobante finalizado.
 * @param {number} id 
 */
function reimprimirReciboFinalizado(id) {
    const registros = getRegistros();
    const r = registros.find(item => item.id === id);
    if (r) {
        mostrarTicketTermicoSalida(r, {
            minutos: 60,
            horas: 1,
            total: r.totalPagado
        });
    }
}
