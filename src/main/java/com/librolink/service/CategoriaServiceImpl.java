package com.librolink.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.librolink.dto.CategoriaFilter;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Categoria;
import com.librolink.repository.ICategoriaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

	private final ICategoriaRepository categoriaRepo;
	
	//R: LISTAR CATEGORIAS
	@Override
	public List<Categoria> listarCategorias() {
		return categoriaRepo.findAllByOrderByDescripcionAsc();
	}

	//BUSCAR CATEGORIA POR NOMBRE
	@Override
	public List<Categoria> buscarCategoriaPorNombre(CategoriaFilter filter) {
		if (filter != null && filter.getNombre() != null && !filter.getNombre().trim().isEmpty()) {
			var nombreBuscar = filter.getNombre().trim().toLowerCase();
			
			return this.listarCategorias().stream()
					.filter(c -> c.getNombre().toLowerCase().contains(nombreBuscar))
					.toList();
		}
		return this.listarCategorias();
	}
	
	//C: REGISTRAR CATEGORIA
	@Override
	public ResultadoResponse registrarCategoria(Categoria categoria) {
		boolean existe = categoriaRepo.findAll().stream()
				.anyMatch(c -> c.getNombre().equalsIgnoreCase(categoria.getNombre().trim()));
				
		if (existe) {
			return ResultadoResponse.error("Esa categoría ya se encuentra registrada.");
		}

		try {
			categoria.setNombre(categoria.getNombre().trim());
			if (categoria.getDescripcion() != null) {
				categoria.setDescripcion(categoria.getDescripcion().trim());
			}
			
			var registro = categoriaRepo.save(categoria);
			return ResultadoResponse.exito("Categoría", registro.getIdCategoria(), "registrada");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
	
	//BUSCAR CATEGORIA POR ID
	@Override
	public Categoria buscarCategoriaPorId(Integer idCategoria) {
		return categoriaRepo.findById(idCategoria).orElseThrow();
	}
	
	//U: ACTUALIZAR CATEGORIA
	@Override
	public ResultadoResponse actualizarCategoria(Categoria categoria) {
		try {
			var categoriaBD = this.buscarCategoriaPorId(categoria.getIdCategoria());
			
			boolean duplicado = categoriaRepo.findAll().stream()
					.anyMatch(c -> c.getNombre().equalsIgnoreCase(categoria.getNombre().trim()) 
							&& !c.getIdCategoria().equals(categoriaBD.getIdCategoria()));
			
			if (duplicado) {
				return ResultadoResponse.error("Ya existe otra categoría con ese nombre.");
			}

			categoriaBD.setNombre(categoria.getNombre().trim());
			categoriaBD.setDescripcion(categoria.getDescripcion() != null ? categoria.getDescripcion().trim() : null);
			
			var registro = categoriaRepo.save(categoriaBD);
			return ResultadoResponse.exito("Categoría", registro.getIdCategoria(), "actualizada");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
	
	//D: ELIMINAR CATEGORIA
	@Override
	@Transactional
	public ResultadoResponse eliminarCategoria(Integer idCategoria) {
		var categoria = this.buscarCategoriaPorId(idCategoria);
		try {
			categoria.setActivo(!categoria.getActivo());
			categoriaRepo.save(categoria);
			
			var estado = categoria.getActivo() ? "activada" : "desactivada";
			return ResultadoResponse.exito("Categoría", categoria.getIdCategoria(), estado);
		} catch (Exception e) {
		    e.printStackTrace();
		    return ResultadoResponse.errorTransaccion();
		}
	}
}