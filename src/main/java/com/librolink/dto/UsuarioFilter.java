package com.librolink.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UsuarioFilter {
	
	private String nombre;
	private String apellido;
	private String correo;
	private String documento;
	private String telefono;
	private LocalDate fechaDesde;
	private LocalDate fechaHasta;
}
