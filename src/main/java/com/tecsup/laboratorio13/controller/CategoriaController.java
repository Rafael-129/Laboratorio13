package com.tecsup.laboratorio13.controller;

import com.tecsup.laboratorio13.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.tecsup.laboratorio13.repository.CategoriaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Endpoint de prueba para verificar el formato JSON
    @PostMapping("/test")
    public Map<String, Object> testCategoria(@RequestBody(required = false) Categoria categoria) {
        Map<String, Object> response = new HashMap<>();
        if (categoria == null) {
            response.put("error", "El cuerpo de la solicitud está vacío");
            response.put("ayuda", "Asegúrate de enviar un JSON válido en el cuerpo de la solicitud");
            response.put("ejemplo", "{ \"nombre\": \"Electrónicos\" }");
        } else {
            response.put("recibido", true);
            response.put("nombre", categoria.getNombre());
            if (categoria.getId() > 0) {
                response.put("id", categoria.getId());
            }
        }
        return response;
    }

    // 🟢 Crear una categoría
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCategoria(@RequestBody(required = false) Categoria categoria) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar si la categoría es nula
            if (categoria == null) {
                response.put("error", "El cuerpo de la solicitud está vacío");
                response.put("ayuda", "Asegúrate de enviar un JSON válido en el formato correcto");
                response.put("ejemplo", "{ \"nombre\": \"Electrónicos\" }");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar que la categoría tenga un nombre
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                response.put("error", "El nombre de la categoría es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }

            // Guardar la categoría
            Categoria guardada = categoriaRepository.save(categoria);

            response.put("mensaje", "Categoría guardada exitosamente");
            response.put("id", guardada.getId());
            response.put("nombre", guardada.getNombre());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al guardar la categoría");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 🔵 Listar todas las categorías
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // 🟡 Obtener una categoría por ID
    @GetMapping("/{id}")
    public Categoria obtenerCategoria(@PathVariable Integer id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    // 🟠 Actualizar una categoría
    @PutMapping("/{id}")
    public String actualizarCategoria(@PathVariable Integer id, @RequestBody Categoria categoriaActualizada) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isPresent()) {
            Categoria c = categoria.get();
            c.setNombre(categoriaActualizada.getNombre());
            categoriaRepository.save(c);
            return "Categoría actualizada";
        } else {
            return "Categoría no encontrada";
        }
    }

    // 🔴 Eliminar una categoría
    @DeleteMapping("/{id}")
    public String eliminarCategoria(@PathVariable Integer id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return "Categoría eliminada";
        } else {
            return "Categoría no encontrada";
        }
    }
}