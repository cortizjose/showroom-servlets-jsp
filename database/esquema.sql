-- =============================================================================
-- Base de datos del Modulo Comercial del Showroom
-- Evidencia GA7-220501096-AA2-EV02  |  Autor: Jose Cortes
--
-- Ejecucion:
--   1. Abrir phpMyAdmin:  http://localhost/phpmyadmin
--   2. Pestana "Importar" y seleccionar este archivo.
--
--   O por consola:
--   C:\xampp\mysql\bin\mysql.exe -u root < database\esquema.sql
-- =============================================================================

DROP DATABASE IF EXISTS showroom_servlets;
CREATE DATABASE showroom_servlets
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE showroom_servlets;

-- -----------------------------------------------------------------------------
-- usuarios
--
-- La contrasena no se guarda: se guarda su resumen SHA-256 junto con una sal
-- distinta para cada usuario. Ni el administrador de la base de datos puede
-- leer las contrasenas.
-- -----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usuario         VARCHAR(40)  NOT NULL UNIQUE,
    nombre          VARCHAR(120) NOT NULL,
    correo          VARCHAR(140) NOT NULL,
    sal             CHAR(32)     NOT NULL,
    contrasena_hash CHAR(64)     NOT NULL,
    rol             ENUM('administrador', 'asesor') NOT NULL DEFAULT 'asesor',
    activo          TINYINT(1)   NOT NULL DEFAULT 1,
    ultimo_ingreso  DATETIME     NULL,
    creado_en       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------------
-- categorias
-- -----------------------------------------------------------------------------
CREATE TABLE categorias (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(200) NULL
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------------
-- productos
--
-- precio es DECIMAL y no FLOAT: los tipos de punto flotante no representan los
-- valores decimales de forma exacta, y en dinero ese error se acumula.
-- -----------------------------------------------------------------------------
CREATE TABLE productos (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    codigo        VARCHAR(20)  NOT NULL UNIQUE,
    nombre        VARCHAR(120) NOT NULL,
    descripcion   VARCHAR(400) NULL,
    categoria_id  INT          NOT NULL,
    precio        DECIMAL(12,2) NOT NULL DEFAULT 0,
    stock         INT          NOT NULL DEFAULT 0,
    stock_minimo  INT          NOT NULL DEFAULT 0,
    estado        ENUM('activo', 'descontinuado') NOT NULL DEFAULT 'activo',
    creado_en     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias (id),
    INDEX idx_producto_nombre (nombre),
    INDEX idx_producto_estado (estado)
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------------
-- clientes
-- -----------------------------------------------------------------------------
CREATE TABLE clientes (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    documento VARCHAR(20)  NOT NULL UNIQUE,
    nombre    VARCHAR(120) NOT NULL,
    correo    VARCHAR(140) NOT NULL,
    telefono  VARCHAR(25)  NOT NULL,
    ciudad    VARCHAR(80)  NOT NULL,
    direccion VARCHAR(160) NULL,
    activo    TINYINT(1)   NOT NULL DEFAULT 1,
    creado_en DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_cliente_nombre (nombre)
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------------
-- cotizaciones
--
-- Los totales quedan guardados en la cabecera. No se recalculan al consultar,
-- porque una cotizacion es un documento historico: debe seguir mostrando los
-- valores que se le ofrecieron al cliente, aunque hoy los precios sean otros.
-- -----------------------------------------------------------------------------
CREATE TABLE cotizaciones (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    numero         VARCHAR(20) NOT NULL UNIQUE,
    cliente_id     INT         NOT NULL,
    usuario_id     INT         NOT NULL,
    estado         ENUM('borrador', 'enviada', 'aprobada', 'rechazada')
                   NOT NULL DEFAULT 'borrador',
    observaciones  VARCHAR(400) NULL,
    subtotal       DECIMAL(14,2) NOT NULL DEFAULT 0,
    iva            DECIMAL(14,2) NOT NULL DEFAULT 0,
    total          DECIMAL(14,2) NOT NULL DEFAULT 0,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cotizacion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    CONSTRAINT fk_cotizacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    INDEX idx_cotizacion_estado (estado)
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------------
-- detalle_cotizaciones
--
-- ON DELETE CASCADE: si se borrara una cotizacion, sus lineas se van con ella.
-- Nunca deben quedar lineas huerfanas apuntando a un documento inexistente.
-- -----------------------------------------------------------------------------
CREATE TABLE detalle_cotizaciones (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    cotizacion_id   INT NOT NULL,
    producto_id     INT NOT NULL,
    cantidad        INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal        DECIMAL(14,2) NOT NULL,
    CONSTRAINT fk_detalle_cotizacion
        FOREIGN KEY (cotizacion_id) REFERENCES cotizaciones (id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id) REFERENCES productos (id),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0)
) ENGINE = InnoDB;

-- =============================================================================
-- DATOS DE EJEMPLO
-- =============================================================================

-- Usuarios. Las credenciales de prueba son:
--   admin  / Admin2025   (administrador: puede modificar el catalogo)
--   asesor / Asesor2025  (asesor: clientes y cotizaciones)
INSERT INTO usuarios (usuario, nombre, correo, sal, contrasena_hash, rol) VALUES
('admin',  'Jose Cortes',    'admin@showroom.test',
 '4a1b16f5a0fcf12e427e20a293a45fca',
 'f51cc0934dabe194deb0ad29553ad35e6455c89611f744137a8906c644838010', 'administrador'),
('asesor', 'Laura Jimenez',  'asesor@showroom.test',
 'c674359e736bacfadb35984de3331226',
 '5aca2067cd8434742970c51c01545a44dad9a0a79c583f7efc9e33cd7e8710c2', 'asesor');

INSERT INTO categorias (nombre, descripcion) VALUES
('Salas',      'Sofas, poltronas y mesas de centro'),
('Comedores',  'Mesas y sillas de comedor'),
('Dormitorio', 'Camas, nocheros y colchones'),
('Oficina',    'Escritorios y sillas ergonomicas');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, precio, stock, stock_minimo) VALUES
('SOF-001', 'Sofa modular tres puestos', 'Tapizado en lino, estructura en madera de pino', 1, 2890000.00, 6, 2),
('POL-002', 'Poltrona reclinable',       'Cuero sintetico, reclinable en tres posiciones',  1,  1250000.00, 9, 3),
('MES-003', 'Mesa de centro en nogal',   'Madera maciza, acabado natural',                  1,   780000.00, 12, 4),
('COM-004', 'Comedor seis puestos',      'Mesa en vidrio templado y sillas tapizadas',      2,  3450000.00, 4, 2),
('SIL-005', 'Silla de comedor',          'Estructura metalica, asiento en tela',            2,   185000.00, 24, 8),
('CAM-006', 'Cama king con base',        'Base cama en madera y cabecero tapizado',         3,  2150000.00, 5, 2),
('NOC-007', 'Nochero dos cajones',       'Acabado en laca blanca',                          3,   320000.00, 15, 5),
('ESC-008', 'Escritorio ejecutivo',      'Superficie en MDF enchapado, 140 cm',             4,  1180000.00, 3, 3);

INSERT INTO clientes (documento, nombre, correo, telefono, ciudad, direccion) VALUES
('1032456789', 'Maria Fernanda Rojas', 'mrojas@correo.test',   '3104567890', 'Bogota',   'Calle 127 # 15-40'),
('8745123',    'Carlos Andres Mejia',  'camejia@correo.test',  '3209876543', 'Medellin', 'Carrera 43A # 18-25'),
('52987456',   'Diana Patricia Luna',  'dpluna@correo.test',   '3156784321', 'Cali',     'Avenida 6N # 23-12');

-- -----------------------------------------------------------------------------
-- Verificacion rapida
-- -----------------------------------------------------------------------------
SELECT 'usuarios'  AS tabla, COUNT(*) AS registros FROM usuarios
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'productos',  COUNT(*) FROM productos
UNION ALL SELECT 'clientes',   COUNT(*) FROM clientes;
