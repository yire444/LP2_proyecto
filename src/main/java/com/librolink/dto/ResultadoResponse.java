package com.librolink.dto;

public record ResultadoResponse(boolean success, String mensaje) {

	// 1. Método estático para éxitos genéricos
	public static ResultadoResponse exito(String entidad, Integer id, String accion) {
		return new ResultadoResponse(true, String.format("%s con ID %s %s correctamente.", entidad, id, accion));
	}

	// 2. Método estático para errores de base de datos / catch
	public static ResultadoResponse errorTransaccion() {
		return new ResultadoResponse(false, "Hubo un error en la transacción del sistema.");
	}
	
	// 3. Método estático para errores de validación rápidos
	public static ResultadoResponse error(String mensajeError) {
		return new ResultadoResponse(false, mensajeError);
	}
}