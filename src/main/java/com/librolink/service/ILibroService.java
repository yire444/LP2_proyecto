package com.librolink.service;

import java.util.List;
import com.librolink.dto.LibroFilter; // Usa el nombre exacto de tu clase actual
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Libro;

public interface ILibroService {

	// R: LISTAR TODOS LOS LIBROS
	List<Libro> listarLibros();
	
	// R: BUSCAR LIBROS 
	List<Libro> buscarLibrosPorFiltros(LibroFilter filter);
	
	// C: REGISTRAR LIBRO
	ResultadoResponse registrarLibro(Libro libro);
	
	// BUSCAR LIBRO POR ID
	Libro buscarLibroPorId(Integer idLibro);
	
	// U: ACTUALIZAR LIBRO
	ResultadoResponse actualizarLibro(Libro libroData);
	
	// D: ELIMINAR LIBRO 
	ResultadoResponse eliminarLibro(Integer idLibro);
}