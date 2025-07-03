package com.tecsup.laboratorio13.controller;

import com.tecsup.laboratorio13.model.Categoria;
import com.tecsup.laboratorio13.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.tecsup.laboratorio13.repository.CategoriaRepository;
import com.tecsup.laboratorio13.repository.ProductoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Endpoint de prueba para verificar el formato JSON
    @PostMapping("/test")
    public Map<String, Object> testProducto(@RequestBody(required = false) Producto producto) {
        Map<String, Object> response = new HashMap<>();
        if (producto == null) {
            response.put("error", "El cuerpo de la solicitud está vacío");
            response.put("ayuda", "Asegúrate de enviar un JSON válido en el cuerpo de la solicitud");
            response.put("ejemplo", "{ \"nombre\": \"Laptop\", \"precio\": 1499.99, \"categoria\": { \"id\": 1 } }");
        } else {
            response.put("recibido", true);
            response.put("nombre", producto.getNombre());
            response.put("precio", producto.getPrecio());

            if (producto.getCategoria() != null) {
                response.put("categoriaId", producto.getCategoria().getId());
            } else {
                response.put("categoria", "No especificada");
            }
        }
        return response;
    }

    // Crear un producto
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProducto(@Valid @RequestBody(required = false) Producto producto) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (producto == null) {
                response.put("error", "El cuerpo de la solicitud está vacío");
                response.put("ayuda", "Asegúrate de enviar un JSON válido en el formato correcto");
                response.put("ejemplo", "{ \"nombre\": \"Laptop\", \"precio\": 1499.99, \"categoria\": { \"id\": 1 } }");
                return ResponseEntity.badRequest().body(response);
            }
            // Buscar la categoría por ID y asociarla al producto
            Optional<Categoria> categoriaOpt = categoriaRepository.findById(producto.getCategoria().getId());
            if (!categoriaOpt.isPresent()) {
                response.put("error", "La categoría especificada no existe");
                return ResponseEntity.badRequest().body(response);
            }
            producto.setCategoria(categoriaOpt.get());
            Producto guardado = productoRepository.save(producto);
            response.put("mensaje", "Producto guardado exitosamente");
            response.put("id", guardado.getId());
            response.put("nombre", guardado.getNombre());
            response.put("precio", guardado.getPrecio());
            response.put("categoriaId", guardado.getCategoria().getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al guardar el producto");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        List<Producto> productos = productoRepository.findAll();
        // Forzar la carga de la categoría para cada producto
        productos.forEach(p -> {
            if (p.getCategoria() != null) {
                p.getCategoria().getNombre();
            }
        });
        return productos;
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public Producto obtenerUnProducto(@PathVariable Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProducto(@PathVariable Integer id, @Valid @RequestBody Producto productoActualizado) {
        Map<String, Object> response = new HashMap<>();
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setNombre(productoActualizado.getNombre());
            producto.setPrecio(productoActualizado.getPrecio());
            // Buscar la categoría por ID y asociarla al producto
            if (productoActualizado.getCategoria() != null) {
                Optional<Categoria> cat = categoriaRepository.findById(productoActualizado.getCategoria().getId());
                cat.ifPresent(producto::setCategoria);
            } else {
                producto.setCategoria(null);
            }
            productoRepository.save(producto);
            response.put("mensaje", "Producto actualizado");
            response.put("id", producto.getId());
            response.put("nombre", producto.getNombre());
            response.put("precio", producto.getPrecio());
            response.put("categoriaId", producto.getCategoria() != null ? producto.getCategoria().getId() : null);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Producto no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return "Producto eliminado";
        } else {
            return "Producto no encontrado";
        }
    }

    // Obtener todas las etiquetas de un producto
    @GetMapping("/{id}/etiquetas")
    public ResponseEntity<?> obtenerEtiquetasDeProducto(@PathVariable Integer id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (!productoOpt.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Producto no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(productoOpt.get().getEtiquetas());
    }
}