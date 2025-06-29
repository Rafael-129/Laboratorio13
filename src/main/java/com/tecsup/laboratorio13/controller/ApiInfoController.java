package com.tecsup.laboratorio13.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiInfoController {

    @GetMapping("/")
    public String home() {
        return "API REST para gestión de productos y categorías";
    }

    @GetMapping("/api")
    public Map<String, Object> apiInfo() {
        Map<String, Object> info = new HashMap<>();

        // Información general
        info.put("nombre", "API de Productos y Categorías");
        info.put("version", "1.0.0");

        // Endpoints disponibles
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/categorias", "Listar todas las categorías");
        endpoints.put("POST /api/categorias", "Crear una nueva categoría");
        endpoints.put("GET /api/categorias/{id}", "Obtener una categoría por ID");
        endpoints.put("PUT /api/categorias/{id}", "Actualizar una categoría");
        endpoints.put("DELETE /api/categorias/{id}", "Eliminar una categoría");

        endpoints.put("GET /api/productos", "Listar todos los productos");
        endpoints.put("POST /api/productos", "Crear un nuevo producto");
        endpoints.put("GET /api/productos/{id}", "Obtener un producto por ID");
        endpoints.put("PUT /api/productos/{id}", "Actualizar un producto");
        endpoints.put("DELETE /api/productos/{id}", "Eliminar un producto");

        info.put("endpoints", endpoints);

        // Ejemplos de formatos JSON
        Map<String, Object> ejemplos = new HashMap<>();

        // Ejemplo para crear categoría
        ejemplos.put("POST /api/categorias", Map.of("nombre", "Electrónicos"));

        // Ejemplo para crear producto
        Map<String, Object> productoEjemplo = new HashMap<>();
        productoEjemplo.put("nombre", "Laptop");
        productoEjemplo.put("precio", 1499.99);
        productoEjemplo.put("categoria", Map.of("id", 1));
        ejemplos.put("POST /api/productos", productoEjemplo);

        info.put("ejemplos", ejemplos);

        return info;
    }
}
