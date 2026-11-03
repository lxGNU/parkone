/**
 * Proyecto ParkOne - Modulo de Control de Parqueadero
 * Logica de Entradas, Salidas y Cobro
 * Aprendiz: Brandon Yair Galvis Diaz - SENA ADSO
 */

let registroEnLiquidacion = null;
let calculoCobroActual = null;

// Abre o cierra el formulario para registrar un nuevo vehiculo
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

// Muestra un mensaje verde de exito o rojo de error en pantalla
function mostrarAlerta(mensaje, tipo = 'success') {
    const box = document.getElementById('alertaBox');
    if (!box) return;

    box.innerHTML = `
        <div class="alert alert-${tipo}">
            <span>${tipo === 'success' ? '✔' : '⚠'}</span>
            <div>${mensaje}</div>
        </div>
    `;

    // Quita el mensaje despues de 4 segundos
    setTimeout(() => {
        if (box.innerHTML.includes(mensaje)) {
            box.innerHTML = '';
        }
    }, 4000);
}

// Guarda un nuevo ingreso validando los datos del formulario
function guardarIngreso(e) {
    e.preventDefault();

    const placa = document.getElementById('inPlaca').value.trim().toUpperCase();
    const tipo = document.getElementById('inTipo').value;
    const propietario = document.getElementById('inPropietario').value.trim();
    const bahia = document.getElementById('inObservacion').value.trim() || 'General';

    // Validamos que la placa tenga minimo 5 caracteres
    if (!placa || placa.length < 5) {
        mostrarAlerta('La placa debe tener minimo 5 caracteres.', 'danger');
        document.getElementById('inPlaca').focus();
        return;
    }

    const registros = getRegistros();

    // Validamos que el vehiculo no este ya adentro en el parqueadero (HU04)
    const yaEstaEnPatio = registros.some(r => r.placa === placa && r.estado === 'ACTIVO');
    if (yaEstaEnPatio) {
        mostrarAlerta(`El vehículo con placa ${placa} ya se encuentra registrado dentro del parqueadero.`, 'danger');
        document.getElementById('inPlaca').focus();
        return;
    }

    // Validamos que haya cupo disponible
    const ocupados = registros.filter(r => r.estado === 'ACTIVO').length;
    if (ocupados >= CAPACIDAD_MAXIMA) {
        mostrarAlerta('No hay cupos disponibles en este momento.', 'danger');
        return;
    }

    const tarifaAplicada = TARIFAS_HORA[tipo] || 3500;
    const nuevoId = registros.length > 0 ? Math.max(...registros.map(r => r.id)) + 1 : 101;
    const fechaHora = obtenerFechaHoraActual();

    // Creamos el nuevo registro
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

    // Lo agregamos al inicio de la lista y guardamos
    registros.unshift(nuevo);
    saveRegistros(registros);

    // Limpiamos y cerramos el formulario
    document.getElementById('formIngreso').reset();
    toggleFormIngreso();
    renderizarTablaControl();

    mostrarAlerta(`Vehículo ${placa} ingresado en ${bahia}.`, 'success');
    mostrarTicketTermicoEntrada(nuevo);
}

// Muestra los registros en la tabla
function renderizarTablaControl() {
    const registros = getRegistros();
    const filtro = (document.getElementById('txtBuscar')?.value || '').trim().toUpperCase();

    // Filtramos si el usuario escribe en el buscador
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
                    No hay registros de vehículos.
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
                    <button onclick="liquidarSalida('${r.placa}')" class="btn btn-danger btn-sm">
                        Cobrar Salida
                    </button>
                    <button onclick="reimprimirTicket(${r.id})" class="btn btn-secondary btn-sm">
                        Ticket
                    </button>
                ` : `
                    <button onclick="reimprimirReciboFinalizado(${r.id})" class="btn btn-secondary btn-sm">
                        Recibo
                    </button>
                `}
            </td>
        </tr>
    `).join('');
}

// Funcion para el buscador por placa o nombre
function filtrarRegistros() {
    renderizarTablaControl();
}

// Limpia el buscador y vuelve a cargar la tabla completa
function limpiarFiltro() {
    const input = document.getElementById('txtBuscar');
    if (input) input.value = '';
    renderizarTablaControl();
}

// Abre la ventana para cobrar y calcula el valor segun el tiempo
function liquidarSalida(placa) {
    const registros = getRegistros();
    const r = registros.find(item => item.placa === placa && item.estado === 'ACTIVO');

    if (!r) {
        mostrarAlerta(`No se encontró el vehículo con placa ${placa}.`, 'danger');
        return;
    }

    registroEnLiquidacion = r;
    const horaSalida = obtenerFechaHoraActual();
    
    // Calculamos los minutos y horas transcurridas
    const inicio = new Date(r.horaIngreso.replace(' ', 'T'));
    const fin = new Date(horaSalida.replace(' ', 'T'));
    let diffMs = fin.getTime() - inicio.getTime();
    if (diffMs < 0) diffMs = 0;
    
    const diffMinutos = Math.max(1, Math.round(diffMs / 60000));
    // Redondeamos hacia arriba para cobrar la fraccion de hora
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
        <div><strong>Tiempo:</strong> ${diffMinutos} min (${horasFacturadas} hora(s) cobradas)</div>
        <div><strong>Tarifa / Hora:</strong> $${tarifa.toLocaleString('es-CO')}</div>
        <div style="margin-top:12px; padding:12px; background:#0f172a; border-radius:8px; border:1px solid #10b981; font-size:1.2rem; font-weight:bold; color:#10b981; text-align:center;">
            TOTAL A PAGAR: $${totalPagar.toLocaleString('es-CO')} COP
        </div>
    `;

    const modal = document.getElementById('modalSalida');
    if (modal) modal.style.display = 'flex';
}

// Cierra la ventana de cobro
function cerrarModalSalida() {
    const modal = document.getElementById('modalSalida');
    if (modal) modal.style.display = 'none';
    registroEnLiquidacion = null;
    calculoCobroActual = null;
}

// Guarda la salida del vehiculo y muestra el recibo
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

    mostrarAlerta(`Salida registrada para la placa ${registros[idx].placa}. Total: $${calculoCobroActual.total.toLocaleString('es-CO')}.`, 'success');
    mostrarTicketTermicoSalida(registros[idx], calculoCobroActual);
}

// Arma el ticket de entrada para imprimir
function mostrarTicketTermicoEntrada(r) {
    const contenedor = document.getElementById('ticketContenido');
    contenedor.innerHTML = `
        <div style="text-align:center; border-bottom:1px dashed #000; padding-bottom:8px; margin-bottom:8px;">
            <h3 style="margin:0; font-size:1.2rem;">PARK ONE</h3>
            <small>Sistema de Parqueadero</small><br>
            <small>Popayán - SENA</small><br>
            <strong>TICKET DE ENTRADA</strong>
        </div>
        <div><strong>Ticket N°:</strong> #ENT-${r.id}</div>
        <div><strong>Fecha/Hora:</strong> ${r.horaIngreso}</div>
        <div><strong>Placa:</strong> <span style="font-size:1.2rem; font-weight:bold; border:1px solid #000; padding:1px 4px;">${r.placa}</span></div>
        <div><strong>Tipo:</strong> ${r.tipo}</div>
        <div><strong>Bahía:</strong> ${r.observaciones || 'General'}</div>
        <div><strong>Conductor:</strong> ${r.propietario}</div>
        <div><strong>Tarifa:</strong> $${Number(r.tarifaAplicada).toLocaleString('es-CO')}/h</div>
        <div style="text-align:center; font-size:0.75rem; margin-top:10px; border-top:1px dashed #000; padding-top:6px;">
            Conserve este ticket para retirar su vehículo.<br>
            ||||| ||| |||| || |||||
        </div>
    `;

    document.getElementById('ticketModal').style.display = 'block';
}

// Arma el recibo de pago de salida
function mostrarTicketTermicoSalida(r, cobro) {
    const contenedor = document.getElementById('ticketContenido');
    contenedor.innerHTML = `
        <div style="text-align:center; border-bottom:1px dashed #000; padding-bottom:8px; margin-bottom:8px;">
            <h3 style="margin:0; font-size:1.2rem;">PARK ONE</h3>
            <small>Sistema de Parqueadero</small><br>
            <small>Popayán - SENA</small><br>
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
            ¡Gracias por su visita!<br>
            |||| ||||| ||| |||| |||
        </div>
    `;

    document.getElementById('ticketModal').style.display = 'block';
}

// Vuelve a abrir el ticket de entrada
function reimprimirTicket(id) {
    const registros = getRegistros();
    const r = registros.find(item => item.id === id);
    if (r) mostrarTicketTermicoEntrada(r);
}

// Vuelve a abrir el recibo de salida
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
