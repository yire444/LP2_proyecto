package com.librolink.dto;

import lombok.Data;

@Data
public class LibroFilter {
	private Integer idCategoria;
	private String titulo;
	private String autor;
	private String ordenPrecio;
}