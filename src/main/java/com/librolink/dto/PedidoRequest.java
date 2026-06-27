package com.librolink.dto;

import java.util.List;
import lombok.Data;

@Data
public class PedidoRequest {
	private Integer idUsuario;
	private List<LibroSeleccionado> items; 
}