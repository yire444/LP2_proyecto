package com.librolink.service;

import java.util.List;
import com.librolink.dto.LibroFilter;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Libro;

public interface ILibroService {

	// R: LISTAR TODOS LOS LIBROS (Para el administrador)
	List<Libro> listarLibros();
	
	// 🌟 LISTAR LIBROS ACTIVOS (Para las vitrinas de los clientes)
	List<Libro> listarLibrosActivos();

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