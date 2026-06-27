package com.librolink.service;

import java.util.List;
import com.librolink.dto.CarritoDto;
import jakarta.servlet.http.HttpSession;

public interface ICarritoService {
	
    CarritoDto obtenerCarrito(HttpSession session);
    void agregarLibro(Integer idLibro, HttpSession session);
    boolean actualizarCantidad(Integer idLibro, Integer cantidad, HttpSession session);
    String validarYProcesarStock(List<Integer> ids, List<Integer> cantidades, HttpSession session);
    
}
