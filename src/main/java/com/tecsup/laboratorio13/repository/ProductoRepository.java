package com.tecsup.laboratorio13.repository;

import com.tecsup.laboratorio13.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}