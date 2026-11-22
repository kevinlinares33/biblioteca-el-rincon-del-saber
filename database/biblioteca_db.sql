-- Base de datos del proyecto Biblioteca "El Rincón del Saber" (ejecutar en DBeaver)
CREATE DATABASE IF NOT EXISTS biblioteca_db;
USE biblioteca_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(80) NOT NULL,
    usuario  VARCHAR(40) NOT NULL UNIQUE,
    clave    VARCHAR(60) NOT NULL,          -- cifrada con BCrypt
    intentos_fallidos INT NOT NULL DEFAULT 0 -- se bloquea con 3
);

CREATE TABLE IF NOT EXISTS libros (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo    VARCHAR(120) NOT NULL,
    autor     VARCHAR(80)  NOT NULL,
    anio      INT,
    cantidad  INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS prestamos (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    libro_id          BIGINT NOT NULL,
    persona           VARCHAR(80) NOT NULL,
    fecha_prestamo    DATE NOT NULL,
    fecha_devolucion  DATE,
    devuelto          BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (libro_id) REFERENCES libros(id)
);

-- Usuario de prueba: admin / Admin2026 (la aplicación cifra la clave con BCrypt al arrancar)
INSERT IGNORE INTO usuarios (nombre, usuario, clave) VALUES ('Administrador', 'admin', 'Admin2026');

-- Libros de ejemplo
INSERT INTO libros (titulo, autor, anio, cantidad) VALUES
('Cien años de soledad', 'Gabriel García Márquez', 1967, 3),
('Don Quijote de la Mancha', 'Miguel de Cervantes', 1605, 2);
