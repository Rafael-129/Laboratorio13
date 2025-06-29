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
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody(required = false) Producto producto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar si el producto es nulo
            if (producto == null) {
                response.put("error", "El cuerpo de la solicitud está vacío");
                response.put("ayuda", "Asegúrate de enviar un JSON válido en el formato correcto");
                response.put("ejemplo", "{ \"nombre\": \"Laptop\", \"precio\": 1499.99, \"categoria\": { \"id\": 1 } }");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar que el producto tenga un nombre
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                response.put("error", "El nombre del producto es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar que el producto tenga una categoría
            if (producto.getCategoria() == null) {
                response.put("error", "Debe especificar una categoría para el producto");
                response.put("formato", "La categoría debe incluirse como { \"categoria\": { \"id\": 1 } }");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar que la categoría exista
            if (!categoriaRepository.existsById(producto.getCategoria().getId())) {
                response.put("error", "La categoría especificada no existe");
                response.put("categoríaId", producto.getCategoria().getId());
                response.put("sugerencia", "Primero crea una categoría con POST /api/categorias");
                return ResponseEntity.badRequest().body(response);
            }

            // Guardar el producto
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
        return productoRepository.findAll();
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public Producto obtenerUnProducto(@PathVariable Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public String actualizarProducto(@PathVariable Integer id, @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoActualizado.getNombre());
            producto.setPrecio(productoActualizado.getPrecio());

            if (productoActualizado.getCategoria() != null) {
                Optional<Categoria> cat = categoriaRepository.findById(productoActualizado.getCategoria().getId());
                cat.ifPresent(producto::setCategoria);
            }

            productoRepository.save(producto);
            return "Producto actualizado";
        }).orElse("Producto no encontrado");
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
}