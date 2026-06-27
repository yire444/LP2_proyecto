package com.librolink.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDto {

	@NotBlank(message = "Este campo no puede estar vacío")
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
	@Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
	private String nombre;

	@NotBlank(message = "Este campo no puede estar vacío")
	@Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras")
	private String apellido;

	@NotBlank(message = "Este campo no puede estar vacío")
	@Pattern(regexp = "^\\d{8,12}$", message = "El documento debe tener entre 8 y 12 dígitos numéricos")
	private String documentoIdentidad;

	@NotNull(message = "La fecha de nacimiento es obligatoria")
	@Past(message = "La fecha de nacimiento debe ser una fecha pasada")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaNacimiento;

	@NotBlank(message = "Este campo no puede estar vacío")
	@Email(message = "El formato del correo no es válido")
	private String correo;

	@NotBlank(message = "Este campo no puede estar vacío")
	@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
	private String password;

	@NotBlank(message = "Este campo no puede estar vacío")
	private String confirmarPassword;

	@NotBlank(message = "Este campo no puede estar vacío")
	@Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe empezar con 9 y tener 9 dígitos")
	private String telefono;
}
