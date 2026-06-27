package com.librolink.dto;

import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarLibroDto {
	
	//PARA EDITAR SACA EL ID DE LA LISTA
	private Integer idLibro;
	
	@NotBlank(message = "El título no puede estar vacío")
	private String titulo;
	
	@NotBlank(message = "El autor no puede estar vacío")
	private String autor;
	
	@NotNull(message = "El precio no puede estar vacío")
	@Min(value = 0, message = "El precio no puede ser negativo")
	private BigDecimal precio;
	
	@NotNull(message = "El stock no puede estar vacío")
	@Min(value = 0, message = "El stock no puede ser negativo")
	private Integer stock;
	
	@NotNull(message = "La descripción no puede estar vacía")
	private String descripcion;
	
	//PARA ESCOGER EN COMBOBOX AL REGISTRAR
	private Integer idCategoria;
	
	// 📸 ¡La magia para subir la foto desde la pantalla!
	private MultipartFile archivoImagen;
}