-- =============================================================================
-- PROYECTO FORMATIVO: ParkOne - Sistema Integral de Gestión de Parqueaderos
-- EVIDENCIA: GA7-220501096-AA2-EV01 - Codificación de módulos del software
-- PROGRAMA: Tecnólogo en Análisis y Desarrollo de Software (ADSO) - FICHA: 3336118
-- APRENDIZ: Brandon Yair Galvis Diaz
-- CENTRO: Centro de Teleinformática y Producción Industrial - SENA Regional Cauca
-- AÑO: 2026
-- =============================================================================

DROP DATABASE IF EXISTS parkone_db;
CREATE DATABASE parkone_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE parkone_db;

-- -----------------------------------------------------------------------------
-- 1. TABLA: usuario
-- Guarda la información de colaboradores (Administradores, Operadores) y clientes.
-- -----------------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    celular VARCHAR(20) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL DEFAULT 'Operador',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_usuario_rol CHECK (rol IN ('Administrador', 'Operador', 'Usuario', 'Cliente'))
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. TABLA: espacio_parqueo
-- Administra la disponibilidad y estado de los cupos de estacionamiento.
-- -----------------------------------------------------------------------------
CREATE TABLE espacio_parqueo (
    id_espacio INT AUTO_INCREMENT PRIMARY KEY,
    numero_espacio VARCHAR(10) NOT NULL UNIQUE,
    tipo_espacio VARCHAR(30) NOT NULL, -- 'Carro', 'Moto', 'Bicicleta'
    estado VARCHAR(20) NOT NULL DEFAULT 'Libre', -- 'Libre', 'Ocupado', 'Mantenimiento'
    CONSTRAINT chk_espacio_estado CHECK (estado IN ('Libre', 'Ocupado', 'Mantenimiento'))
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. TABLA: vehiculo
-- Registra los vehículos que ingresan y se asocian a un cliente/propietario.
-- -----------------------------------------------------------------------------
CREATE TABLE vehiculo (
    placa VARCHAR(10) PRIMARY KEY,
    tipo_vehiculo VARCHAR(30) NOT NULL, -- 'Carro', 'Moto', 'Camioneta'
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    color VARCHAR(30) NOT NULL,
    id_usuario INT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehiculo_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. TABLA: tarifa
-- Define los valores de cobro según el tipo de vehículo.
-- -----------------------------------------------------------------------------
CREATE TABLE tarifa (
    id_tarifa INT AUTO_INCREMENT PRIMARY KEY,
    tipo_vehiculo VARCHAR(30) NOT NULL UNIQUE,
    valor_hora DECIMAL(10,2) NOT NULL,
    valor_fraccion DECIMAL(10,2) NOT NULL,
    valor_dia DECIMAL(10,2) NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. TABLA: ingreso_salida (Movimientos operativos)
-- Registra la trazabilidad del servicio de parqueo en tiempo real.
-- -----------------------------------------------------------------------------
CREATE TABLE ingreso_salida (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL,
    id_espacio INT NOT NULL,
    id_usuario_operador INT NOT NULL,
    fecha_hora_ingreso DATETIME NOT NULL,
    fecha_hora_salida DATETIME NULL,
    tiempo_total DECIMAL(10,2) NULL, -- Tiempo en horas / fracciones
    codigo_qr VARCHAR(255) NULL,
    estado_movimiento VARCHAR(20) NOT NULL DEFAULT 'Activo', -- 'Activo', 'Finalizado'
    CONSTRAINT fk_movimiento_vehiculo FOREIGN KEY (placa)
        REFERENCES vehiculo(placa)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movimiento_espacio FOREIGN KEY (id_espacio)
        REFERENCES espacio_parqueo(id_espacio)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movimiento_operador FOREIGN KEY (id_usuario_operador)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. TABLA: pago
-- Registra los pagos efectuados al liquidar el servicio.
-- -----------------------------------------------------------------------------
CREATE TABLE pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_movimiento INT NOT NULL,
    fecha_pago DATETIME NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL, -- 'Efectivo', 'Tarjeta', 'QR / Transferencia'
    valor_total DECIMAL(10,2) NOT NULL,
    estado_pago VARCHAR(30) NOT NULL DEFAULT 'Completado',
    CONSTRAINT fk_pago_movimiento FOREIGN KEY (id_movimiento)
        REFERENCES ingreso_salida(id_movimiento)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 7. TABLA: reserva
-- Permite reservas de cupos con antelación.
-- -----------------------------------------------------------------------------
CREATE TABLE reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_espacio INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente', -- 'Pendiente', 'Confirmada', 'Cancelada'
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_espacio FOREIGN KEY (id_espacio)
        REFERENCES espacio_parqueo(id_espacio)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 8. TABLA: auditoria
-- Trazabilidad de seguridad para registrar acciones realizadas por empleados.
-- -----------------------------------------------------------------------------
CREATE TABLE auditoria (
    id_auditoria INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion VARCHAR(100) NOT NULL,
    fecha_evento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    detalles TEXT NULL,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================================
-- INSERCIÓN DE DATOS INICIALES (SEMILLA / SEED DATA)
-- =============================================================================

-- Usuarios iniciales (clave 'admin123' / 'operador123')
INSERT INTO usuario (identificacion, nombres, apellidos, correo, celular, contrasena, rol) VALUES
('1061789001', 'Brandon Yair', 'Galvis Diaz', 'admin@parkone.com', '3125487890', 'admin123', 'Administrador'),
('1061789002', 'Ronald Javier', 'Echeverry Caicedo', 'ronald.echeverry@parkone.com', '3201456987', 'operador123', 'Operador'),
('1061789003', 'Carlos Andres', 'Perez Muñoz', 'carlos.perez@correo.com', '3157894512', 'cliente123', 'Usuario'),
('1061789004', 'Maria Camila', 'Gomez Rios', 'maria.gomez@correo.com', '3184561234', 'cliente123', 'Usuario');

-- Espacios de parqueo
INSERT INTO espacio_parqueo (numero_espacio, tipo_espacio, estado) VALUES
('A-01', 'Carro', 'Libre'),
('A-02', 'Carro', 'Libre'),
('A-03', 'Carro', 'Libre'),
('A-04', 'Carro', 'Libre'),
('A-05', 'Carro', 'Libre'),
('M-01', 'Moto', 'Libre'),
('M-02', 'Moto', 'Libre'),
('M-03', 'Moto', 'Libre'),
('M-04', 'Moto', 'Libre'),
('M-05', 'Moto', 'Libre');

-- Tarifas vigentes (COP)
INSERT INTO tarifa (tipo_vehiculo, valor_hora, valor_fraccion, valor_dia) VALUES
('Carro', 3500.00, 1000.00, 25000.00),
('Moto', 1500.00, 500.00, 12000.00),
('Camioneta', 4500.00, 1500.00, 30000.00);

-- Vehículos de prueba
INSERT INTO vehiculo (placa, tipo_vehiculo, marca, modelo, color, id_usuario) VALUES
('KGF892', 'Carro', 'Renault Stepway', '2022', 'Gris', 3),
('QWE123', 'Moto', 'Yamaha NMAX', '2023', 'Negro', 4);
