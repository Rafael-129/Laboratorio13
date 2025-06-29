package com.tecsup.laboratorio13.repository;

import com.tecsup.laboratorio13.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}