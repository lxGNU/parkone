-- Script para la base de datos parkone_db
CREATE DATABASE IF NOT EXISTS parkone_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE parkone_db;

DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    rol VARCHAR(30) NOT NULL DEFAULT 'Usuario',
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL DEFAULT '123456',
    telefono VARCHAR(20),
    direccion VARCHAR(150),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE vehiculo (
    id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(15) NOT NULL UNIQUE,
    tipo VARCHAR(30) NOT NULL,
    marca VARCHAR(50),
    color VARCHAR(30),
    propietario_cedula VARCHAR(20) NOT NULL,
    propietario_nombre VARCHAR(150) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Estacionado',
    tarifa_por_hora DECIMAL(10, 2) NOT NULL DEFAULT 3000.00,
    fecha_ingreso DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_salida DATETIME NULL,
    FOREIGN KEY (propietario_cedula) REFERENCES usuario(identificacion) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos de prueba
INSERT INTO usuario (identificacion, nombres, apellidos, cargo, rol, correo, contrasena, telefono, direccion) VALUES
('1061723849', 'Brandon Yair', 'Galvis Diaz', 'Administrador', 'Administrador', 'brandon.galvis@sena.edu.co', '123456', '3104567890', 'Popayán'),
('1061987654', 'Maria Fernanda', 'Lopez', 'Operador', 'Usuario', 'maria.lopez@parkone.com', '123456', '3201234567', 'Calle 5');

INSERT INTO vehiculo (placa, tipo, marca, color, propietario_cedula, propietario_nombre, estado, tarifa_por_hora, fecha_ingreso) VALUES
('ABC-123', 'Automóvil', 'Toyota', 'Gris', '1061723849', 'Brandon Yair Galvis Diaz', 'Estacionado', 4000.00, NOW());
