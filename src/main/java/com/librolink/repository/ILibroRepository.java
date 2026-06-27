package com.librolink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.librolink.model.Libro;

@Repository
public interface ILibroRepository extends JpaRepository<Libro, Integer> {
	
	// Traer solo libros activos para la tienda del cliente
		List<Libro> findByActivoTrueOrderByIdLibroDesc();
		
		List<Libro> findAllByOrderByIdLibroDesc();

		@Query("""
				select l
				from Libro as l
				where l.activo = true and
					(:idCategoria is null or l.categoria.idCategoria = :idCategoria)
					and
					(:titulo is null or l.titulo like concat('%', :titulo, '%') or l.autor like concat('%', :autor, '%'))
				""")
		List<Libro> findAllByFilters(
			@Param("idCategoria") Integer idCategoria,
			@Param("titulo") String titulo,
			@Param("autor") String autor
		); 

		@Query("""
				select l
				from Libro as l
				where l.activo = true and
					(:idCategoria is null or l.categoria.idCategoria = :idCategoria)
					and
					(:titulo is null or l.titulo like concat('%', :titulo, '%') or l.autor like concat('%', :autor, '%'))
				order by l.precio asc
				""")
		List<Libro> findAllByFiltersPrecioAsc(
			@Param("idCategoria") Integer idCategoria,
			@Param("titulo") String titulo,
			@Param("autor") String autor
		);

		@Query("""
				select l
				from Libro as l
				where l.activo = true and
					(:idCategoria is null or l.categoria.idCategoria = :idCategoria)
					and
					(:titulo is null or l.titulo like concat('%', :titulo, '%') or l.autor like concat('%', :autor, '%'))
				order by l.precio desc
				""")
		List<Libro> findAllByFiltersPrecioDesc(
			@Param("idCategoria") Integer idCategoria,
			@Param("titulo") String titulo,
			@Param("autor") String autor
		);
}