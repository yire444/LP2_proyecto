package com.librolink.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.librolink.dto.CarritoDto;
import com.librolink.service.ICarritoService;
import com.librolink.util.Alert;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CarritoController {

	private final ICarritoService carritoService;

	// 1. VER EL CARRITO DE COMPRAS
	@GetMapping("/carrito")
	public String verCarrito(HttpSession session, Model model, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);

		CarritoDto carrito = carritoService.obtenerCarrito(session);
		
		model.addAttribute("items", carrito.getItems());
		model.addAttribute("total", carrito.getTotal());
		return "carrito";
	}

	// 2. AGREGAR UN ITEM AL CARRITO
	@GetMapping("/carrito/agregar")
	public String agregarAlCarrito(@RequestParam("idLibro") Integer idLibro, HttpSession session, RedirectAttributes flash) {
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		if (idUsuario == null) {
			flash.addFlashAttribute("alert", Alert.sweetAlertError("Debes contar con una sesión activa para añadir libros."));
			return "redirect:/login/iniciar-sesion";
		}

		carritoService.agregarLibro(idLibro, session);
		return "redirect:/carrito";
	}
	
	// 3. PROCESAR, ACTUALIZAR CANTIDADES Y VALIDAR STOCK ANTES DEL PAGO
	@PostMapping("/carrito/procesar")
	public String procesarCarrito(@RequestParam("idLibro") List<Integer> ids, 
								  @RequestParam("cantidad") List<Integer> cantidades, 
								  HttpSession session, 
								  RedirectAttributes flash) {
		
		String errorStock = carritoService.validarYProcesarStock(ids, cantidades, session);
		
		if (errorStock != null) {
			flash.addFlashAttribute("alert", Alert.sweetAlertError(errorStock));
			return "redirect:/carrito";
		}
		
		return "redirect:/pedido/checkout";
	}
	
	// 4. SINCRONIZAR CANTIDAD EN SEGUNDO PLANO (Fetch de JS)
	@GetMapping("/carrito/actualizar-cantidad")
	@ResponseBody
	public ResponseEntity<?> actualizarCantidadSesion(@RequestParam("idLibro") Integer idLibro, 
													  @RequestParam("cantidad") Integer cantidad, 
													  HttpSession session) {
		
		carritoService.actualizarCantidad(idLibro, cantidad, session);
		return ResponseEntity.ok().body("{\"status\":\"ok\"}");
	}
	
	// 5. ELIMINAR UN ITEM DEL CARRITO
	@GetMapping("/carrito/eliminar")
	public String eliminarDelCarrito(@RequestParam("idLibro") Integer idLibro, HttpSession session) {
		CarritoDto carrito = carritoService.obtenerCarrito(session);
		carrito.getItems().removeIf(item -> item.getIdLibro().equals(idLibro));
		session.setAttribute("carrito", carrito);
		return "redirect:/carrito";
	}

	// 6. MOSTRAR PASARELA DE PAGO (CHECKOUT)
	@GetMapping("/pedido/checkout")
	public String mostrarPasarela(HttpSession session, Model model) {
		CarritoDto carrito = carritoService.obtenerCarrito(session);
		
		if (carrito == null || carrito.getItems().isEmpty()) {
			return "redirect:/";
		}
		
		model.addAttribute("total", carrito.getTotal());
		return "pasarela_pago"; 
	}
}