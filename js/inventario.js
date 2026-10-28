/**
 * ============================================================================
 * SISTEMA DE GESTION DE PARQUEADEROS - PARKONE
 * Componente Front-End: Logica de Control de Entradas, Salidas y Taquilla
 * Archivo: js/inventario.js
 * Evidencia: GA7-220501096-AA4-EV03
 * Aprendiz: Brandon Yair Galvis Diaz
 * Ficha: 3336118 - ADSO SENA Regional Cauca - 2026
 * ============================================================================
 * Descripcion:
 * Implementa la logica del lado cliente para el modulo de control operativo.
 * Maneja eventos del formulario, validaciones de placa (longitud y no duplicidad),
 * liquidacion de tarifas segun el tiempo transcurrido, generacion de tickets
 * termicos con opcion de impresion, y persistencia en LocalStorage.
 * ============================================================================
 */

// Estado local de la vista
let pestanaActual = 'ACTIVO';
let vehiculoEnLiquidacion = null;
let liquidacionCalculada = null;

/**
 * Inicializa la tabla y los escuchadores al cargar el script.
 */
document.addEventListener('DOMContentLoaded', () => {
    renderizarTablaControl();
});

/**
 * Alterna la visualización del formulario para nuevo ingreso.
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
 * Cambia la pestaña activa de la tabla entre vehículos en patio y finalizados.
 * @param {string} estado 'ACTIVO' o 'FINALIZADO'
 */
function cambiarPestana(estado) {
    pestanaActual = estado;

    const btnActivos = document.getElementById('tabActivos');
    const btnSalidas = document.getElementById('tabSalidas');

    if (estado === 'ACTIVO') {
        btnActivos.className = 'btn btn-primary btn-sm';
        btnSalidas.className = 'btn btn-secondary btn-sm';
    } else {
        btnActivos.className = 'btn btn-secondary btn-sm';
        btnSalidas.className = 'btn btn-primary btn-sm';
    }

    renderizarTablaControl();
}

/**
 * Muestra alertas temporales en la parte superior de la página.
 * @param {string} mensaje Texto a mostrar
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

    // Desaparece automáticamente después de 4.5 segundos
    setTimeout(() => {
        if (box.innerHTML.includes(mensaje)) {
            box.innerHTML = '';
        }
    }, 4500);
}

/**
 * Procesa el envío del formulario de registro de entrada vehicular.
 * Aplica validaciones estrictas requeridas en la evidencia formativa.
 * @param {Event} e Evento submit
 */
function procesarIngreso(e) {
    e.preventDefault();

    // 1. Capturar y sanitizar datos del formulario
    const placa = document.getElementById('inPlaca').value.trim().toUpperCase();
    const tipo = document.getElementById('inTipo').value;
    const propietario = document.getElementById('inPropietario').value.trim();
    const bahia = document.getElementById('inBahia').value.trim();

    // 2. Validación de longitud de placa vehicular (Estándar Colombia mínimo 5-6 caracteres)
    if (!placa || placa.length < 5) {
        mostrarAlerta('La placa ingresada no es válida. Debe tener al menos 5 caracteres alfanuméricos.', 'danger');
        document.getElementById('inPlaca').focus();
        return;
    }

    const registros = getRegistros();

    // 3. Validación de regla de negocio: Comprobar placa duplicada en patio
    const yaExisteEnPatio = registros.some(r => r.placa === placa && r.estado === 'ACTIVO');
    if (yaExisteEnPatio) {
        mostrarAlerta(`RESTRICCIÓN: El vehículo con placa [${placa}] ya se encuentra registrado actualmente dentro del patio.`, 'danger');
        document.getElementById('inPlaca').focus();
        return;
    }

    // 4. Validación de capacidad máxima del parqueadero
    const activosActuales = registros.filter(r => r.estado === 'ACTIVO').length;
    if (activosActuales >= CAPACIDAD_MAXIMA_PARQUEADERO) {
        mostrarAlerta('CAPACIDAD MÁXIMA ALCANZADA: No hay celdas libres disponibles en este momento.', 'danger');
        return;
    }

    // 5. Determinar tarifa base por tipo
    const tarifas = getTarifas();
    const tarifaAplicada = tarifas[tipo] || 3500;
    const operadorActual = getUsuarioSesion();

    // 6. Generar el nuevo objeto registro
    const nuevoId = registros.length > 0 ? Math.max(...registros.map(r => r.id)) + 1 : 101;
    const nuevoRegistro = {
        id: nuevoId,
        placa: placa,
        tipo: tipo,
        propietario: propietario,
        bahia: bahia,
        horaIngreso: obtenerFechaHoraActual(),
        horaSalida: null,
        tarifaAplicada: tarifaAplicada,
        totalPagado: 0,
        estado: 'ACTIVO',
        operadorIngreso: `${operadorActual.nombres} ${operadorActual.apellidos}`
    };

    // 7. Persistir en LocalStorage
    registros.unshift(nuevoRegistro);
    saveRegistros(registros);

    // 8. Limpiar y ocultar formulario
    document.getElementById('formIngreso').reset();
    toggleFormIngreso();

    // 9. Refrescar la tabla en pantalla
    renderizarTablaControl();

    // 10. Notificación y apertura automática del comprobante / ticket térmico
    mostrarAlerta(`Vehículo [${placa}] ingresado exitosamente en la celda ${bahia}.`, 'success');
    mostrarTicketEntrada(nuevoRegistro);
}

/**
 * Renderiza los registros en la tabla según la pestaña activa y los filtros.
 */
function renderizarTablaControl() {
    const registros = getRegistros();
    const filtroTexto = (document.getElementById('txtBuscar')?.value || '').trim().toUpperCase();

    // Filtrar por pestaña (ACTIVO vs FINALIZADO)
    let filtrados = registros.filter(r => r.estado === pestanaActual);

    // Filtrar por texto de búsqueda
    if (filtroTexto) {
        filtrados = filtrados.filter(r => 
            r.placa.toUpperCase().includes(filtroTexto) || 
            r.propietario.toUpperCase().includes(filtroTexto) ||
            r.bahia.toUpperCase().includes(filtroTexto)
        );
    }

    const tbody = document.getElementById('tablaControlBody');
    if (!tbody) return;

    if (filtrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="10" style="text-align:center; padding:25px; color:#94a3b8;">
                    No se encontraron registros ${pestanaActual === 'ACTIVO' ? 'activos en patio' : 'en el historial de salidas'}.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filtrados.map(r => `
        <tr>
            <td><strong>#${r.id}</strong></td>
            <td><span class="placa-badge">${r.placa}</span></td>
            <td><strong>${r.tipo}</strong></td>
            <td>${r.propietario}</td>
            <td><span style="color:#38bdf8; font-weight:600;">${r.bahia}</span></td>
            <td>${r.horaIngreso}</td>
            <td>${r.horaSalida || '<em style="color:#64748b;">En patio</em>'}</td>
            <td><strong>${r.totalPagado > 0 ? formatearMoneda(r.totalPagado) : '$0'}</strong></td>
            <td>
                <span class="badge ${r.estado === 'ACTIVO' ? 'badge-success' : 'badge-secondary'}">
                    ${r.estado === 'ACTIVO' ? 'En Patio' : 'Finalizado'}
                </span>
            </td>
            <td>
                ${r.estado === 'ACTIVO' ? `
                    <button onclick="abrirModalLiquidacion('${r.placa}')" class="btn btn-danger btn-sm" title="Calcular cobro y registrar salida">
                        Cobrar Salida
                    </button>
                    <button onclick="reimprimirTicketEntrada(${r.id})" class="btn btn-secondary btn-sm" title="Reimprimir ticket de entrada">
                        Ticket
                    </button>
                ` : `
                    <button onclick="verComprobanteSalida('${r.placa}')" class="btn btn-secondary btn-sm" title="Ver comprobante de pago">
                        Ver Recibo
                    </button>
                `}
            </td>
        </tr>
    `).join('');
}

/**
 * Escucha cambios en el campo de búsqueda de la tabla.
 */
function filtrarTabla() {
    renderizarTablaControl();
}

/**
 * Abre la ventana modal para liquidar la salida de un vehículo estacionado.
 * Calcula en tiempo real el tiempo transcurrido y el valor a cancelar.
 * @param {string} placa Placa del vehículo a liquidar
 */
function abrirModalLiquidacion(placa) {
    const registros = getRegistros();
    const vehiculo = registros.find(r => r.placa === placa && r.estado === 'ACTIVO');

    if (!vehiculo) {
        mostrarAlerta(`No se encontró un vehículo activo con la placa ${placa}.`, 'danger');
        return;
    }

    vehiculoEnLiquidacion = vehiculo;
    const horaActual = obtenerFechaHoraActual();
    const liquidacion = calcularCobro(vehiculo.horaIngreso, horaActual, vehiculo.tipo);
    liquidacionCalculada = { ...liquidacion, horaSalida: horaActual };

    const contenedor = document.getElementById('detalleCobroContenido');
    contenedor.innerHTML = `
        <div style="display:flex; justify-content:space-between; margin-bottom:8px;">
            <span>Placa del Vehículo:</span>
            <strong style="color:#fef08a; font-size:1.1rem; font-family:monospace;">${vehiculo.placa}</strong>
        </div>
        <div style="display:flex; justify-content:space-between; margin-bottom:8px;">
            <span>Tipo / Categoría:</span>
            <strong>${vehiculo.tipo}</strong>
        </div>
        <div style="display:flex; justify-content:space-between; margin-bottom:8px;">
            <span>Propietario:</span>
            <span>${vehiculo.propietario}</span>
        </div>
        <div style="display:flex; justify-content:space-between; margin-bottom:8px;">
            <span>Bahía Asignada:</span>
            <span style="color:#38bdf8;">${vehiculo.bahia}</span>
        </div>
        <hr style="border:none; border-top:1px dashed #334155; margin:10px 0;">
        <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
            <span>Hora de Ingreso:</span>
            <span>${vehiculo.horaIngreso}</span>
        </div>
        <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
            <span>Hora de Salida:</span>
            <span>${horaActual}</span>
        </div>
        <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
            <span>Tiempo Transcurrido:</span>
            <strong>${liquidacion.duracionMinutos} min (${liquidacion.horasACobrar} hora(s) facturadas)</strong>
        </div>
        <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
            <span>Tarifa Aplicada:</span>
            <span>${formatearMoneda(liquidacion.tarifaHora)} / hora</span>
        </div>
        <div style="display:flex; justify-content:space-between; margin-top:12px; padding:10px; background:#1e293b; border-radius:6px; border:1px solid #10b981;">
            <span style="font-size:1.1rem; font-weight:bold; color:#10b981;">TOTAL A COBRAR:</span>
            <span style="font-size:1.3rem; font-weight:800; color:#ffffff;">${formatearMoneda(liquidacion.totalPagar)} COP</span>
        </div>
    `;

    document.getElementById('modalLiquidacion').style.display = 'flex';
}

/**
 * Cierra la ventana modal de liquidación.
 */
function cerrarModalLiquidacion() {
    document.getElementById('modalLiquidacion').style.display = 'none';
    vehiculoEnLiquidacion = null;
    liquidacionCalculada = null;
}

/**
 * Confirma el pago de la tarifa y registra la salida del vehículo en LocalStorage.
 */
function confirmarSalida() {
    if (!vehiculoEnLiquidacion || !liquidacionCalculada) return;

    const registros = getRegistros();
    const indice = registros.findIndex(r => r.id === vehiculoEnLiquidacion.id);

    if (indice === -1) return;

    const metodoPago = document.getElementById('selMetodoPago').value;
    const operador = getUsuarioSesion();

    // Actualizar registro
    registros[indice].horaSalida = liquidacionCalculada.horaSalida;
    registros[indice].totalPagado = liquidacionCalculada.totalPagar;
    registros[indice].estado = 'FINALIZADO';
    registros[indice].metodoPago = metodoPago;
    registros[indice].operadorSalida = `${operador.nombres} ${operador.apellidos}`;

    saveRegistros(registros);

    // Crear recibo histórico
    const recibos = getRecibos();
    const numRecibo = `REC-${new Date().getFullYear()}-${String(recibos.length + 1).padStart(4, '0')}`;
    const nuevoRecibo = {
        numeroRecibo: numRecibo,
        placa: vehiculoEnLiquidacion.placa,
        tipo: vehiculoEnLiquidacion.tipo,
        propietario: vehiculoEnLiquidacion.propietario,
        bahia: vehiculoEnLiquidacion.bahia,
        horaIngreso: vehiculoEnLiquidacion.horaIngreso,
        horaSalida: liquidacionCalculada.horaSalida,
        duracionMinutos: liquidacionCalculada.duracionMinutos,
        duracionHoras: liquidacionCalculada.horasACobrar,
        tarifaHora: liquidacionCalculada.tarifaHora,
        totalPagado: liquidacionCalculada.totalPagar,
        metodoPago: metodoPago,
        operador: `${operador.nombres} ${operador.apellidos}`
    };

    recibos.unshift(nuevoRecibo);
    saveRecibos(recibos);

    cerrarModalLiquidacion();
    renderizarTablaControl();

    mostrarAlerta(`Salida del vehículo [${nuevoRecibo.placa}] registrada con éxito. Recaudo: ${formatearMoneda(nuevoRecibo.totalPagado)}.`, 'success');
    mostrarTicketSalida(nuevoRecibo);
}

/**
 * Genera y muestra el comprobante térmico de ingreso.
 * @param {Object} registro 
 */
function mostrarTicketEntrada(registro) {
    const contenedor = document.getElementById('ticketPrintContent');
    contenedor.innerHTML = `
        <div class="ticket-header">
            <h3>PARK ONE</h3>
            <div style="font-size:0.75rem;">SISTEMA DE GESTION DE PARQUEADEROS</div>
            <div style="font-size:0.75rem;">NIT: 900.123.456-7 | SENA CAUCA</div>
            <div style="font-size:0.72rem; color:#64748b;">Calle 3 # 1 s 27, Popayán</div>
            <div style="font-weight:bold; margin-top:4px;">TICKET DE ENTRADA</div>
        </div>

        <div class="ticket-row">
            <span>Ticket N°:</span>
            <strong>#ENT-${String(registro.id).padStart(5, '0')}</strong>
        </div>
        <div class="ticket-row">
            <span>Fecha y Hora:</span>
            <span>${registro.horaIngreso}</span>
        </div>
        <div class="ticket-row">
            <span>Placa Vehicular:</span>
            <strong style="font-size:1.1rem; border:1px solid #000; padding:1px 4px;">${registro.placa}</strong>
        </div>
        <div class="ticket-row">
            <span>Tipo Vehículo:</span>
            <span>${registro.tipo}</span>
        </div>
        <div class="ticket-row">
            <span>Bahía Asignada:</span>
            <strong>${registro.bahia}</strong>
        </div>
        <div class="ticket-row">
            <span>Conductor:</span>
            <span>${registro.propietario}</span>
        </div>
        <div class="ticket-row">
            <span>Tarifa / Hora:</span>
            <strong>${formatearMoneda(registro.tarifaAplicada)}</strong>
        </div>
        <div class="ticket-row">
            <span>Atendido por:</span>
            <span>${registro.operadorIngreso}</span>
        </div>

        <div style="text-align:center; margin-top:12px; font-size:0.72rem; border-top:1px dashed #64748b; padding-top:6px;">
            Conserve este ticket para retirar su vehículo.<br>
            No nos hacemos responsables por objetos de valor no declarados.
        </div>
        <div class="ticket-barcode">||| | |||| | ||| || |||</div>
    `;

    document.getElementById('modalTicket').style.display = 'flex';
}

/**
 * Genera y muestra el recibo térmico de pago de salida.
 * @param {Object} recibo 
 */
function mostrarTicketSalida(recibo) {
    const contenedor = document.getElementById('ticketPrintContent');
    contenedor.innerHTML = `
        <div class="ticket-header">
            <h3>PARK ONE</h3>
            <div style="font-size:0.75rem;">SISTEMA DE GESTION DE PARQUEADEROS</div>
            <div style="font-size:0.75rem;">NIT: 900.123.456-7 | SENA CAUCA</div>
            <div style="font-size:0.72rem; color:#64748b;">Calle 3 # 1 s 27, Popayán</div>
            <div style="font-weight:bold; margin-top:4px;">COMPROBANTE DE PAGO</div>
        </div>

        <div class="ticket-row">
            <span>Factura N°:</span>
            <strong>${recibo.numeroRecibo}</strong>
        </div>
        <div class="ticket-row">
            <span>Placa:</span>
            <strong style="font-size:1.1rem;">${recibo.placa}</strong>
        </div>
        <div class="ticket-row">
            <span>Tipo:</span>
            <span>${recibo.tipo}</span>
        </div>
        <div class="ticket-row">
            <span>Cliente:</span>
            <span>${recibo.propietario}</span>
        </div>
        <div class="ticket-row">
            <span>Ingreso:</span>
            <span>${recibo.horaIngreso}</span>
        </div>
        <div class="ticket-row">
            <span>Salida:</span>
            <span>${recibo.horaSalida}</span>
        </div>
        <div class="ticket-row">
            <span>Tiempo Total:</span>
            <strong>${recibo.duracionMinutos} min (${recibo.duracionHoras}h facturadas)</strong>
        </div>
        <div class="ticket-row">
            <span>Método de Pago:</span>
            <span>${recibo.metodoPago}</span>
        </div>
        <div class="ticket-row">
            <span>Cajero:</span>
            <span>${recibo.operador}</span>
        </div>

        <div class="ticket-total">
            <span>TOTAL PAGADO:</span>
            <span>${formatearMoneda(recibo.totalPagado)}</span>
        </div>

        <div style="text-align:center; font-size:0.72rem;">
            ¡Gracias por preferir a ParkOne!<br>
            Régimen Simplificado - Popayán, Cauca.
        </div>
        <div class="ticket-barcode">|||| | || |||| || | |||</div>
    `;

    document.getElementById('modalTicket').style.display = 'flex';
}

/**
 * Cierra la ventana modal del ticket térmico.
 */
function cerrarModalTicket() {
    document.getElementById('modalTicket').style.display = 'none';
}

/**
 * Lanza la impresión del comprobante usando el diálogo nativo del navegador.
 */
function imprimirTicket() {
    window.print();
}

/**
 * Reimprime el ticket de entrada a partir de su ID.
 * @param {number} id 
 */
function reimprimirTicketEntrada(id) {
    const registros = getRegistros();
    const item = registros.find(r => r.id === id);
    if (item) {
        mostrarTicketEntrada(item);
    }
}

/**
 * Muestra el comprobante de un vehículo que ya finalizó su estancia.
 * @param {string} placa 
 */
function verComprobanteSalida(placa) {
    const recibos = getRecibos();
    const item = recibos.find(r => r.placa === placa);
    if (item) {
        mostrarTicketSalida(item);
    } else {
        mostrarAlerta(`No se encontró el comprobante para la placa ${placa}.`, 'danger');
    }
}
