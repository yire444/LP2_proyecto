package com.librolink.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.librolink.model.Categoria;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {
	
	// LISTAR CATEGORÍAS ALFABÉTICAMENTE POR NOMBRE (A - Z)
	List<Categoria> findAllByOrderByDescripcionAsc();
	
	// LISTAR POR FECHA/INGRESO (Del más antiguo al más nuevo)
	List<Categoria> findAllByOrderByIdCategoriaAsc();
	
	// LISTAR POR FECHA/INGRESO (Del más nuevo al más antiguo)
	List<Categoria> findAllByOrderByIdCategoriaDesc();
}