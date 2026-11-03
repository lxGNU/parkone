/**
 * Proyecto ParkOne - Sistema de Parqueadero
 * Modulo Front-End (JavaScript)
 * Aprendiz: Brandon Yair Galvis Diaz
 * Ficha: 3336118 - ADSO SENA Regional Cauca
 */

// Nombres de las llaves para guardar en el LocalStorage
const STORAGE_REGISTROS = 'parkone_registros_v2';
const STORAGE_USUARIOS  = 'parkone_usuarios_v2';
const STORAGE_AUTH      = 'parkone_auth_user';

// Tarifas por hora segun el tipo de vehiculo
const TARIFAS_HORA = {
    'Carro': 3500,
    'Moto': 2000,
    'Camioneta': 4500,
    'Bicicleta': 1000
};

// Capacidad maxima del parqueadero
const CAPACIDAD_MAXIMA = 50;

// Usuarios iniciales para pruebas del sistema
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
        direccion: "Calle 3 # 1 s 27, Popayán"
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
        direccion: "Carrera 9 # 15-40, Popayán"
    }
];

// Vehiculos iniciales cargados en el parqueadero
const DEFAULT_REGISTROS = [
    {
        id: 101,
        placa: "ABC123",
        tipo: "Carro",
        propietario: "Juan Esteban López",
        observaciones: "Bahía A-01",
        horaIngreso: "2026-10-28 08:30:00",
        horaSalida: null,
        tarifaAplicada: 3500,
        totalPagado: 0,
        estado: "ACTIVO"
    },
    {
        id: 102,
        placa: "XYZ789",
        tipo: "Carro",
        propietario: "Mariana Torres",
        observaciones: "Bahía A-02",
        horaIngreso: "2026-10-28 09:15:00",
        horaSalida: null,
        tarifaAplicada: 3500,
        totalPagado: 0,
        estado: "ACTIVO"
    },
    {
        id: 103,
        placa: "MOT45D",
        tipo: "Moto",
        propietario: "Andrés Felipe Castro",
        observaciones: "Bahía M-01",
        horaIngreso: "2026-10-28 09:40:00",
        horaSalida: null,
        tarifaAplicada: 2000,
        totalPagado: 0,
        estado: "ACTIVO"
    },
    {
        id: 100,
        placa: "COL999",
        tipo: "Camioneta",
        propietario: "Empresa de Envíos SAS",
        observaciones: "Bahía C-01",
        horaIngreso: "2026-10-28 07:00:00",
        horaSalida: "2026-10-28 08:30:00",
        tarifaAplicada: 4500,
        totalPagado: 9000,
        estado: "FINALIZADO"
    }
];

// Revisa si ya hay datos guardados en LocalStorage, si no los crea
function inicializarEstado() {
    try {
        if (!localStorage.getItem(STORAGE_USUARIOS)) {
            localStorage.setItem(STORAGE_USUARIOS, JSON.stringify(DEFAULT_USUARIOS));
        }
        if (!localStorage.getItem(STORAGE_REGISTROS)) {
            localStorage.setItem(STORAGE_REGISTROS, JSON.stringify(DEFAULT_REGISTROS));
        }
        if (!localStorage.getItem(STORAGE_AUTH)) {
            localStorage.setItem(STORAGE_AUTH, JSON.stringify(DEFAULT_USUARIOS[0]));
        }
    } catch (e) {
        console.error("Error al iniciar datos en LocalStorage:", e);
    }
}

// Obtiene el usuario que tiene la sesion abierta
function getUsuarioSesion() {
    try {
        const u = localStorage.getItem(STORAGE_AUTH);
        return u ? JSON.parse(u) : DEFAULT_USUARIOS[0];
    } catch (e) {
        return DEFAULT_USUARIOS[0];
    }
}

// Cierra la sesion y manda a la pagina de login
function cerrarSesion() {
    localStorage.removeItem(STORAGE_AUTH);
    window.location.href = 'login.html';
}

// Devuelve la lista de registros de vehiculos guardados
function getRegistros() {
    try {
        const data = localStorage.getItem(STORAGE_REGISTROS);
        return data ? JSON.parse(data) : [];
    } catch (e) {
        return [];
    }
}

// Guarda la lista de registros en el LocalStorage
function saveRegistros(registros) {
    localStorage.setItem(STORAGE_REGISTROS, JSON.stringify(registros));
}

// Calcula los totales del inicio: capacidad, ocupados, libres y dinero recaudado
function getEstadisticas() {
    const registros = getRegistros();
    const activos = registros.filter(r => r.estado === 'ACTIVO');
    const finalizados = registros.filter(r => r.estado === 'FINALIZADO');
    
    const cuposOcupados = activos.length;
    const cuposDisponibles = Math.max(0, CAPACIDAD_MAXIMA - cuposOcupados);
    const recaudoTotal = finalizados.reduce((total, r) => total + (Number(r.totalPagado) || 0), 0);

    return {
        capacidadMaxima: CAPACIDAD_MAXIMA,
        cuposOcupados,
        cuposDisponibles,
        recaudoTotal
    };
}

// Devuelve los usuarios registrados
function getUsuarios() {
    try {
        const data = localStorage.getItem(STORAGE_USUARIOS);
        return data ? JSON.parse(data) : [];
    } catch (e) {
        return [];
    }
}

// Guarda los usuarios actualizados en el LocalStorage
function saveUsuarios(usuarios) {
    localStorage.setItem(STORAGE_USUARIOS, JSON.stringify(usuarios));
}

// Obtiene la fecha y hora del sistema en formato año-mes-dia hora:minuto:segundo
function obtenerFechaHoraActual() {
    const ahora = new Date();
    const dosDigitos = (n) => String(n).padStart(2, '0');
    return `${ahora.getFullYear()}-${dosDigitos(ahora.getMonth() + 1)}-${dosDigitos(ahora.getDate())} ${dosDigitos(ahora.getHours())}:${dosDigitos(ahora.getMinutes())}:${dosDigitos(ahora.getSeconds())}`;
}

// Muestra el nombre del usuario y el boton de salir arriba en la cabecera
function renderHeaderUser(contenedorId = 'userInfoBox') {
    const box = document.getElementById(contenedorId);
    if (!box) return;

    const user = getUsuarioSesion();
    if (user) {
        box.innerHTML = `
            <div><strong>Usuario:</strong> ${user.nombres} ${user.apellidos} (${user.rol})</div>
            <div><a href="login.html" onclick="cerrarSesion()" style="color:#ef4444; font-weight:bold; text-decoration:none;">Cerrar Sesión</a></div>
        `;
    }
}

// Se ejecuta al cargar el archivo para asegurar que los datos existan
inicializarEstado();
