package com.librolink.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class PedidoFilter {
	private Integer idUsuario; 
	private LocalDate fecha;  
}