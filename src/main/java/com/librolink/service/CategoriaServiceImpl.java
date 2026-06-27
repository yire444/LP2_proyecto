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

	@Override
	public List<Categoria> listarCategorias() {
		// Retorna el listado alfabético (puedes cambiarlo al método que gustes del repo)
		return categoriaRepo.findAllByOrderByDescripcionAsc();
	}

	@Override
	public List<Categoria> buscarCategoriaPorNombre(CategoriaFilter filter) {
		if (filter.getNombre() == null || filter.getNombre().trim().isEmpty()) {
			return this.listarCategorias();
		}
		return categoriaRepo.findByNombreContainingIgnoreCase(filter.getNombre().trim());
	}

	@Override
	public ResultadoResponse registrarCategoria(Categoria categoria) {
		if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
			return ResultadoResponse.error("El nombre de la categoría no puede estar vacío.");
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

	@Override
	public Categoria buscarCategoriaPorId(Integer idCategoria) {
		return categoriaRepo.findById(idCategoria).orElseThrow();
	}

	@Override
	public ResultadoResponse actualizarCategoria(Categoria categoriaData) {
		if (categoriaData.getNombre() == null || categoriaData.getNombre().trim().isEmpty()) {
			return ResultadoResponse.error("El nombre de la categoría no puede estar vacío.");
		}
		try {
			var catBD = this.buscarCategoriaPorId(categoriaData.getIdCategoria());
			catBD.setNombre(categoriaData.getNombre().trim());
			if (categoriaData.getDescripcion() != null) {
				catBD.setDescripcion(categoriaData.getDescripcion().trim());
			}
			var registro = categoriaRepo.save(catBD);
			return ResultadoResponse.exito("Categoría", registro.getIdCategoria(), "actualizada");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}

	@Override
	@Transactional
	public ResultadoResponse eliminarCategoria(Integer idCategoria) {
		try {
			var categoria = this.buscarCategoriaPorId(idCategoria);
			categoria.setActivo(!categoria.getActivo()); // Baja / Alta lógica
			categoriaRepo.save(categoria);
			
			var estado = categoria.getActivo() ? "activada" : "desactivada";
			return ResultadoResponse.exito("Categoría", categoria.getIdCategoria(), estado);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
}