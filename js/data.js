/**
 * ============================================================================
 * SISTEMA DE GESTION DE PARQUEADEROS - PARKONE
 * Componente Front-End: Capa de Datos, Persistencia y Estado Global (data.js)
 * Evidencia: GA7-220501096-AA4-EV03
 * Aprendiz: Brandon Yair Galvis Diaz
 * Ficha: 3336118 - Tecnologo en Analisis y Desarrollo de Software (ADSO)
 * SENA Regional Cauca - 2026
 * ============================================================================
 * Descripcion:
 * Este archivo centraliza la capa de abstraccion de datos del front-end.
 * Gestiona el almacenamiento local (LocalStorage), inicializa datos semilla
 * (Mock Data) alineados con el modelo relacional parkone_db, provee funciones
 * matematicas de cobro por horas/fracciones y mantiene la sesion activa.
 * Cumple con estandares de codificacion limpia, modularidad y trazabilidad.
 * ============================================================================
 */

// Constantes de almacenamiento local (LocalStorage Keys)
const STORAGE_REGISTROS = 'parkone_registros_v3';
const STORAGE_USUARIOS  = 'parkone_usuarios_v3';
const STORAGE_TARIFAS   = 'parkone_tarifas_v3';
const STORAGE_RECIBOS   = 'parkone_recibos_v3';
const STORAGE_AUTH      = 'parkone_auth_user_v3';

// Capacidad maxima de celdas en el establecimiento
const CAPACIDAD_MAXIMA_PARQUEADERO = 50;

/**
 * Tarifas oficiales por hora o fraccion para cada categoria vehicular.
 * Valores definidos en COP (Pesos Colombianos).
 */
const TARIFAS_BASE = {
    'Carro': 3500,
    'Moto': 2000,
    'Camioneta': 4500,
    'Bicicleta': 1000
};

/**
 * Usuarios iniciales del sistema (Administrador y Operador).
 * Basado en la entidad 'usuario' de la base de datos parkone_db.
 */
const DEFAULT_USUARIOS = [
    {
        id: 1,
        identificacion: "1061723849",
        nombres: "Brandon Yair",
        apellidos: "Galvis Diaz",
        cargo: "Administrador del Sistema",
        rol: "Administrador",
        correo: "bygalvis@misena.edu.co",
        telefono: "3234565445",
        direccion: "Calle 3 # 1 s 27, Popayán - Cauca",
        fechaRegistro: "2026-10-01"
    },
    {
        id: 2,
        identificacion: "1061998877",
        nombres: "Carlos Andres",
        apellidos: "Perez Gomez",
        cargo: "Operador de Taquilla Turno Mañana",
        rol: "Operador",
        correo: "cperez@parkone.co",
        telefono: "3127894512",
        direccion: "Carrera 9 # 15-40, Popayán",
        fechaRegistro: "2026-10-10"
    }
];

/**
 * Vehiculos iniciales en patio y registros de trazabilidad.
 */
const DEFAULT_REGISTROS = [
    {
        id: 101,
        placa: "ABC123",
        tipo: "Carro",
        propietario: "Juan Esteban López",
        bahia: "Bahía A-01",
        horaIngreso: "2026-10-28 08:30:00",
        horaSalida: null,
        tarifaAplicada: 3500,
        totalPagado: 0,
        estado: "ACTIVO",
        operadorIngreso: "Brandon Yair Galvis Diaz"
    },
    {
        id: 102,
        placa: "XYZ789",
        tipo: "Carro",
        propietario: "Mariana Torres",
        bahia: "Bahía A-02",
        horaIngreso: "2026-10-28 09:15:00",
        horaSalida: null,
        tarifaAplicada: 3500,
        totalPagado: 0,
        estado: "ACTIVO",
        operadorIngreso: "Carlos Andres Perez Gomez"
    },
    {
        id: 103,
        placa: "MOT45D",
        tipo: "Moto",
        propietario: "Andrés Felipe Castro",
        bahia: "Bahía M-01",
        horaIngreso: "2026-10-28 09:40:00",
        horaSalida: null,
        tarifaAplicada: 2000,
        totalPagado: 0,
        estado: "ACTIVO",
        operadorIngreso: "Brandon Yair Galvis Diaz"
    },
    {
        id: 104,
        placa: "COL999",
        tipo: "Camioneta",
        propietario: "Distribuidora del Cauca SAS",
        bahia: "Bahía C-01",
        horaIngreso: "2026-10-28 07:00:00",
        horaSalida: "2026-10-28 09:00:00",
        tarifaAplicada: 4500,
        totalPagado: 9000,
        estado: "FINALIZADO",
        operadorIngreso: "Brandon Yair Galvis Diaz"
    }
];

/**
 * Recibos iniciales de pago histórico.
 */
const DEFAULT_RECIBOS = [
    {
        numeroRecibo: "REC-2026-0001",
        placa: "COL999",
        tipo: "Camioneta",
        propietario: "Distribuidora del Cauca SAS",
        bahia: "Bahía C-01",
        horaIngreso: "2026-10-28 07:00:00",
        horaSalida: "2026-10-28 09:00:00",
        duracionMinutos: 120,
        duracionHoras: 2,
        tarifaHora: 4500,
        totalPagado: 9000,
        metodoPago: "Efectivo",
        operador: "Brandon Yair Galvis Diaz"
    }
];

/**
 * Inicializa el estado local en el navegador asegurando que las llaves existan.
 */
function inicializarEstado() {
    try {
        if (!localStorage.getItem(STORAGE_USUARIOS)) {
            localStorage.setItem(STORAGE_USUARIOS, JSON.stringify(DEFAULT_USUARIOS));
        }
        if (!localStorage.getItem(STORAGE_REGISTROS)) {
            localStorage.setItem(STORAGE_REGISTROS, JSON.stringify(DEFAULT_REGISTROS));
        }
        if (!localStorage.getItem(STORAGE_TARIFAS)) {
            localStorage.setItem(STORAGE_TARIFAS, JSON.stringify(TARIFAS_BASE));
        }
        if (!localStorage.getItem(STORAGE_RECIBOS)) {
            localStorage.setItem(STORAGE_RECIBOS, JSON.stringify(DEFAULT_RECIBOS));
        }
        if (!localStorage.getItem(STORAGE_AUTH)) {
            // Usuario por defecto en sesión
            localStorage.setItem(STORAGE_AUTH, JSON.stringify(DEFAULT_USUARIOS[0]));
        }
    } catch (e) {
        console.error("Error al inicializar LocalStorage:", e);
    }
}

/**
 * Obtiene el usuario en sesión activa.
 * @returns {Object} Usuario autenticado
 */
function getUsuarioSesion() {
    try {
        const u = localStorage.getItem(STORAGE_AUTH);
        return u ? JSON.parse(u) : DEFAULT_USUARIOS[0];
    } catch (e) {
        return DEFAULT_USUARIOS[0];
    }
}

/**
 * Cierra la sesión activa y redirige al login.
 */
function cerrarSesion() {
    localStorage.removeItem(STORAGE_AUTH);
    window.location.href = 'login.html';
}

/**
 * Retorna todos los registros de parqueadero.
 * @returns {Array} Lista de vehículos ingresados
 */
function getRegistros() {
    try {
        const d = localStorage.getItem(STORAGE_REGISTROS);
        return d ? JSON.parse(d) : [];
    } catch (e) {
        return [];
    }
}

/**
 * Guarda los registros en LocalStorage.
 * @param {Array} registros 
 */
function saveRegistros(registros) {
    localStorage.setItem(STORAGE_REGISTROS, JSON.stringify(registros));
}

/**
 * Retorna la lista de usuarios.
 * @returns {Array} Lista de usuarios
 */
function getUsuarios() {
    try {
        const d = localStorage.getItem(STORAGE_USUARIOS);
        return d ? JSON.parse(d) : [];
    } catch (e) {
        return [];
    }
}

/**
 * Guarda los usuarios en LocalStorage.
 * @param {Array} usuarios 
 */
function saveUsuarios(usuarios) {
    localStorage.setItem(STORAGE_USUARIOS, JSON.stringify(usuarios));
}

/**
 * Retorna las tarifas vigentes por categoría.
 * @returns {Object} Mapa tipo -> tarifa
 */
function getTarifas() {
    try {
        const d = localStorage.getItem(STORAGE_TARIFAS);
        return d ? JSON.parse(d) : TARIFAS_BASE;
    } catch (e) {
        return TARIFAS_BASE;
    }
}

/**
 * Retorna el historial de recibos emitidos.
 * @returns {Array}
 */
function getRecibos() {
    try {
        const d = localStorage.getItem(STORAGE_RECIBOS);
        return d ? JSON.parse(d) : [];
    } catch (e) {
        return [];
    }
}

/**
 * Guarda el historial de recibos en LocalStorage.
 * @param {Array} recibos 
 */
function saveRecibos(recibos) {
    localStorage.setItem(STORAGE_RECIBOS, JSON.stringify(recibos));
}

/**
 * Calcula las metricas de ocupacion, disponibilidad y recaudo para el Dashboard.
 * @returns {Object} Metricas consolidadas
 */
function getEstadisticas() {
    const registros = getRegistros();
    const activos = registros.filter(r => r.estado === 'ACTIVO');
    const finalizados = registros.filter(r => r.estado === 'FINALIZADO');

    const cuposOcupados = activos.length;
    const cuposDisponibles = Math.max(0, CAPACIDAD_MAXIMA_PARQUEADERO - cuposOcupados);
    const recaudoTotal = finalizados.reduce((acc, r) => acc + (Number(r.totalPagado) || 0), 0);

    return {
        capacidadMaxima: CAPACIDAD_MAXIMA_PARQUEADERO,
        cuposOcupados,
        cuposDisponibles,
        recaudoTotal
    };
}

/**
 * Calcula el tiempo de estancia y el valor a pagar de acuerdo con la tarifa.
 * Aplica regla de negocio: minimo 1 hora; fracciones subsiguientes se cobran como hora completa.
 * @param {string} fechaHoraIngreso 
 * @param {string} fechaHoraSalida 
 * @param {string} tipoVehiculo 
 * @returns {Object} Detalle de la liquidación
 */
function calcularCobro(fechaHoraIngreso, fechaHoraSalida, tipoVehiculo) {
    const inicio = new Date(fechaHoraIngreso.replace(' ', 'T'));
    const fin = new Date(fechaHoraSalida.replace(' ', 'T'));
    
    // Diferencia en milisegundos
    let diffMs = fin.getTime() - inicio.getTime();
    if (diffMs < 0) diffMs = 0;

    const diffMinutos = Math.max(1, Math.round(diffMs / 60000));
    
    // Regla de cobro por horas o fraccion:
    // Si dura hasta 60 min -> 1 hora.
    // Si dura 65 min -> 2 horas.
    const horasACobrar = Math.max(1, Math.ceil(diffMinutos / 60));

    const tarifas = getTarifas();
    const tarifaHora = tarifas[tipoVehiculo] || 3500;
    const totalPagar = horasACobrar * tarifaHora;

    return {
        duracionMinutos: diffMinutos,
        horasACobrar,
        tarifaHora,
        totalPagar
    };
}

/**
 * Formatea un numero como moneda colombiana (COP).
 * @param {number} valor 
 * @returns {string} Ejemplo: "$3.500"
 */
function formatearMoneda(valor) {
    return '$' + (Number(valor) || 0).toLocaleString('es-CO');
}

/**
 * Retorna la fecha y hora actual en formato estándar YYYY-MM-DD HH:mm:ss.
 * @returns {string}
 */
function obtenerFechaHoraActual() {
    const ahora = new Date();
    const pad = (n) => String(n).padStart(2, '0');
    const anio = ahora.getFullYear();
    const mes = pad(ahora.getMonth() + 1);
    const dia = pad(ahora.getDate());
    const hora = pad(ahora.getHours());
    const min = pad(ahora.getMinutes());
    const seg = pad(ahora.getSeconds());
    return `${anio}-${mes}-${dia} ${hora}:${min}:${seg}`;
}

/**
 * Renderiza el bloque de información del usuario en el header de cualquier pantalla.
 * @param {string} contenedorId ID del elemento contenedor
 */
function renderHeaderUser(contenedorId = 'userInfoBox') {
    const box = document.getElementById(contenedorId);
    if (!box) return;

    const user = getUsuarioSesion();
    const iniciales = (user.nombres[0] || 'U') + (user.apellidos[0] || '');

    box.innerHTML = `
        <div class="user-badge">
            <div class="user-avatar-circle">${iniciales}</div>
            <div>
                <strong>${user.nombres} ${user.apellidos}</strong>
                <span style="color:#94a3b8; font-size:0.75rem; display:block;">${user.rol} | C.C. ${user.identificacion}</span>
            </div>
        </div>
        <a href="login.html" onclick="cerrarSesion()" class="btn-logout" title="Cerrar Sesión Activa">Salir</a>
    `;
}

// Ejecutar inicialización de estado al cargar
inicializarEstado();
