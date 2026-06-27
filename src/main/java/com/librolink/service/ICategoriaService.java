package com.librolink.service;

import java.util.List;

import com.librolink.dto.CategoriaFilter;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Categoria;

public interface ICategoriaService {

	// R: LISTAR TODAS (Ordenadas por descripción A-Z)
	List<Categoria> listarCategorias();
	
	// R: BUSCAR POR NOMBRE
	List<Categoria> buscarCategoriaPorNombre(CategoriaFilter filter);
	
	// C: REGISTRAR CATEGORÍA
	ResultadoResponse registrarCategoria(Categoria categoria);
	
	// OBTENER UNA POR ID
	Categoria buscarCategoriaPorId(Integer idCategoria);
	
	// U: ACTUALIZAR CATEGORÍA
	ResultadoResponse actualizarCategoria(Categoria categoria);
	
	// D: ELIMINAR CATEGORÍA 
	ResultadoResponse eliminarCategoria(Integer idCategoria);
}