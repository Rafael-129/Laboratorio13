package com.tecsup.laboratorio13.controller;

import com.tecsup.laboratorio13.model.Etiqueta;
import com.tecsup.laboratorio13.model.Producto;
import com.tecsup.laboratorio13.repository.EtiquetaRepository;
import com.tecsup.laboratorio13.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/etiquetas")
public class EtiquetaController {

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Crear una etiqueta
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEtiqueta(@RequestBody(required = false) Etiqueta etiqueta) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar si la etiqueta es nula
            if (etiqueta == null) {
                response.put("error", "El cuerpo de la solicitud está vacío");
                response.put("ayuda", "Asegúrate de enviar un JSON válido en el formato correcto");
                response.put("ejemplo", "{ \"nombre\": \"Oferta\" }");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar que la etiqueta tenga un nombre
            if (etiqueta.getNombre() == null || etiqueta.getNombre().trim().isEmpty()) {
                response.put("error", "El nombre de la etiqueta es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }

            // Guardar la etiqueta
            Etiqueta guardada = etiquetaRepository.save(etiqueta);

            response.put("mensaje", "Etiqueta guardada exitosamente");
            response.put("id", guardada.getId());
            response.put("nombre", guardada.getNombre());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al guardar la etiqueta");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Listar todas las etiquetas
    @GetMapping
    public List<Etiqueta> listarEtiquetas() {
        return etiquetaRepository.findAll();
    }

    // Obtener una etiqueta por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEtiqueta(@PathVariable Integer id) {
        Optional<Etiqueta> etiqueta = etiquetaRepository.findById(id);
        if (etiqueta.isPresent()) {
            return ResponseEntity.ok(etiqueta.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Etiqueta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Actualizar una etiqueta
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarEtiqueta(@PathVariable Integer id, @RequestBody Etiqueta etiquetaActualizada) {
        Map<String, Object> response = new HashMap<>();

        return etiquetaRepository.findById(id)
            .map(etiqueta -> {
                etiqueta.setNombre(etiquetaActualizada.getNombre());
                Etiqueta guardada = etiquetaRepository.save(etiqueta);

                response.put("mensaje", "Etiqueta actualizada exitosamente");
                response.put("id", guardada.getId());
                response.put("nombre", guardada.getNombre());

                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("error", "Etiqueta no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    // Eliminar una etiqueta
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarEtiqueta(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (etiquetaRepository.existsById(id)) {
            etiquetaRepository.deleteById(id);
            response.put("mensaje", "Etiqueta eliminada exitosamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Etiqueta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Agregar una etiqueta a un producto
    @PostMapping("/{etiquetaId}/productos/{productoId}")
    public ResponseEntity<Map<String, Object>> agregarEtiquetaAProducto(
            @PathVariable Integer etiquetaId, 
            @PathVariable Integer productoId) {

        Map<String, Object> response = new HashMap<>();

        Optional<Etiqueta> etiquetaOpt = etiquetaRepository.findById(etiquetaId);
        Optional<Producto> productoOpt = productoRepository.findById(productoId);

        if (!etiquetaOpt.isPresent()) {
            response.put("error", "Etiqueta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (!productoOpt.isPresent()) {
            response.put("error", "Producto no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Producto producto = productoOpt.get();
        Etiqueta etiqueta = etiquetaOpt.get();

        producto.agregarEtiqueta(etiqueta);
        productoRepository.save(producto);

        response.put("mensaje", "Etiqueta agregada al producto exitosamente");
        response.put("productoId", producto.getId());
        response.put("etiquetaId", etiqueta.getId());

        return ResponseEntity.ok(response);
    }

    // Eliminar una etiqueta de un producto
    @DeleteMapping("/{etiquetaId}/productos/{productoId}")
    public ResponseEntity<Map<String, Object>> eliminarEtiquetaDeProducto(
            @PathVariable Integer etiquetaId, 
            @PathVariable Integer productoId) {

        Map<String, Object> response = new HashMap<>();

        Optional<Etiqueta> etiquetaOpt = etiquetaRepository.findById(etiquetaId);
        Optional<Producto> productoOpt = productoRepository.findById(productoId);

        if (!etiquetaOpt.isPresent()) {
            response.put("error", "Etiqueta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (!productoOpt.isPresent()) {
            response.put("error", "Producto no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Producto producto = productoOpt.get();
        Etiqueta etiqueta = etiquetaOpt.get();

        producto.eliminarEtiqueta(etiqueta);
        productoRepository.save(producto);

        response.put("mensaje", "Etiqueta eliminada del producto exitosamente");
        response.put("productoId", producto.getId());
        response.put("etiquetaId", etiqueta.getId());

        return ResponseEntity.ok(response);
    }

    // Obtener todos los productos con una etiqueta específica
    @GetMapping("/{etiquetaId}/productos")
    public ResponseEntity<?> obtenerProductosPorEtiqueta(@PathVariable Integer etiquetaId) {
        Optional<Etiqueta> etiquetaOpt = etiquetaRepository.findById(etiquetaId);

        if (!etiquetaOpt.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Etiqueta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(etiquetaOpt.get().getProductos());
    }
}
