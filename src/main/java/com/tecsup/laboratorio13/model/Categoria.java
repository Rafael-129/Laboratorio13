package com.tecsup.laboratorio13.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    // Constructor sin argumentos requerido por JPA
    public Categoria() {
    }

    // Constructor con nombre
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}