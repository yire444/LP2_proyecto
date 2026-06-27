package com.librolink.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CarritoDto {
	
	private Integer idUsuario;
	private List<LibroSeleccionado> items = new ArrayList<>();
	
	public Double getTotal() {
		return items.stream()
					.mapToDouble(LibroSeleccionado::getSubtotal)
					.sum();
	}
}