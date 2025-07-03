CREATE DATABASE IF NOT EXISTS semana_13 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE semana_13;

CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    categoria_id INT,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS etiqueta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS producto_etiqueta (
    producto_id INT NOT NULL,
    etiqueta_id INT NOT NULL,
    PRIMARY KEY (producto_id, etiqueta_id),
    CONSTRAINT fk_pe_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE,
    CONSTRAINT fk_pe_etiqueta FOREIGN KEY (etiqueta_id) REFERENCES etiqueta(id) ON DELETE CASCADE
);
