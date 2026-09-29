-- Script de Base de Datos para el Sistema ParkOne
-- Evidencia GA7-220501096-AA3-EV01
-- Aprendiz: Brandon Yair Galvis Diaz
-- Ficha: 3336118

CREATE DATABASE IF NOT EXISTS parkone_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE parkone_db;

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(150),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Vehiculos
CREATE TABLE IF NOT EXISTS vehiculos (
    id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    tipo VARCHAR(30) NOT NULL,
    marca VARCHAR(50),
    color VARCHAR(30),
    propietario_cedula VARCHAR(20) NOT NULL,
    propietario_nombre VARCHAR(150) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Estacionado',
    tarifa_por_hora DECIMAL(10,2) NOT NULL DEFAULT 4000.00,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Registros de Parqueadero (Entradas y Salidas)
CREATE TABLE IF NOT EXISTS registros_parqueadero (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    placa_vehiculo VARCHAR(10) NOT NULL,
    tipo_vehiculo VARCHAR(30) NOT NULL,
    propietario_nombre VARCHAR(150) NOT NULL,
    fecha_entrada DATETIME NOT NULL,
    fecha_salida DATETIME,
    horas_cobradas INT DEFAULT 0,
    tarifa_aplicada DECIMAL(10,2) NOT NULL,
    total_pagar DECIMAL(10,2) DEFAULT 0.00,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    observaciones VARCHAR(255)
);

-- Usuario Administrador por defecto para ingresar al sistema
INSERT INTO usuarios (identificacion, nombres, apellidos, cargo, rol, correo, contrasena, telefono, direccion)
VALUES 
('1061723849', 'Brandon Yair', 'Galvis Diaz', 'Administrador de Sistema', 'Administrador', 'brandon.galvis@sena.edu.co', '123456', '3124567890', 'Popayán, Cauca');

