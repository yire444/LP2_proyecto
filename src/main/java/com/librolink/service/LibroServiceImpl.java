package com.librolink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.librolink.dto.LibroFilter;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Libro;
import com.librolink.repository.ILibroRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl implements ILibroService {

	private final ILibroRepository libroRepo;
	
	// R: LISTAR LIBROS
	@Override
	public List<Libro> listarLibros() {
		return libroRepo.findAllByOrderByIdLibroDesc();
	}
	
	@Override
	public List<Libro> listarLibrosActivos() {
		return libroRepo.findByActivoTrueOrderByIdLibroDesc();
	}
	
	// BUSCAR LIBRO POR LOS FILTROS DEL REPO
	@Override
	public List<Libro> buscarLibrosPorFiltros(LibroFilter filter) {
	    var tit = (filter.getTitulo() != null && !filter.getTitulo().trim().isEmpty()) ? filter.getTitulo().trim() : null;
	    var aut = (filter.getAutor() != null && !filter.getAutor().trim().isEmpty()) ? filter.getAutor().trim() : null;
	    Integer cat = (filter.getIdCategoria() != null && filter.getIdCategoria() > 0) ? filter.getIdCategoria() : null;
	    
	    if ("ASC".equals(filter.getOrdenPrecio())) {
	        return libroRepo.findAllByFiltersPrecioAsc(cat, tit, aut);
	    } else if ("DESC".equals(filter.getOrdenPrecio())) {
	        return libroRepo.findAllByFiltersPrecioDesc(cat, tit, aut);
	    } else {
	        if (tit == null && aut == null && cat == null) {
	            return this.listarLibros(); 
	        }
	        
	        return libroRepo.findAllByFilters(cat, tit, aut); 
	    }
	}
	
	// C: REGISTRAR LIBRO
	@Override
	public ResultadoResponse registrarLibro(Libro libro) {
		if (libro.getPrecio() == null || libro.getPrecio().doubleValue() <= 0 || libro.getStock() == null || libro.getStock() < 0) {
			return ResultadoResponse.error("El precio del libro debe ser mayor a cero y el stock no puede ser un número negativo.");
		}

		try {
			libro.setTitulo(libro.getTitulo().trim());
			libro.setAutor(libro.getAutor().trim());
			libro.setDescripcion(libro.getDescripcion().trim());
			
			var registro = libroRepo.save(libro);
			return ResultadoResponse.exito("Libro", registro.getIdLibro(), "registrado");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
	
	// BUSCAR LIBRO POR ID
	@Override
	public Libro buscarLibroPorId(Integer idLibro) {
		return libroRepo.findById(idLibro).orElseThrow();
	}
	
	// U: ACTUALIZAR LIBRO
	@Override
	public ResultadoResponse actualizarLibro(Libro libroData) {
		if (libroData.getPrecio() == null || libroData.getPrecio().doubleValue() <= 0 || libroData.getStock() == null || libroData.getStock() < 0) {
			return ResultadoResponse.error("El precio del libro debe ser mayor a cero y el stock no puede ser un número negativo.");
		}

		try {
			var libroBD = this.buscarLibroPorId(libroData.getIdLibro());

			libroBD.setTitulo(libroData.getTitulo().trim());
			libroBD.setAutor(libroData.getAutor().trim());
			libroBD.setDescripcion(libroData.getDescripcion().trim());
			libroBD.setPrecio(libroData.getPrecio());
			libroBD.setStock(libroData.getStock());
			libroBD.setCategoria(libroData.getCategoria());
			
			if (libroData.getImagen() != null && !libroData.getImagen().trim().isEmpty()) {
			    libroBD.setImagen(libroData.getImagen().trim());
			}
			libroBD.setImagen(libroData.getImagen().trim());

			var registro = libroRepo.save(libroBD);
			return ResultadoResponse.exito("Libro", registro.getIdLibro(), "actualizado");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
	
	// D: ELIMINAR LIBRO
	@Override
	@Transactional
	public ResultadoResponse eliminarLibro(Integer idLibro) {
		var libro = this.buscarLibroPorId(idLibro);
		try {
			libro.setActivo(!libro.getActivo());
			libroRepo.save(libro);
			
			var estado = libro.getActivo() ? "activado" : "desactivado";
			return ResultadoResponse.exito("Libro", libro.getIdLibro(), estado);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
}