package com.librolink.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.librolink.dto.CarritoDto;
import com.librolink.dto.PedidoRequest;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Pedido;
import com.librolink.service.ICarritoService;
import com.librolink.service.IPedidoService;
import com.librolink.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PedidoController {

	private final IPedidoService pedidoService;
	private final ICarritoService carritoService;

	// PROCESAR COMPRA DESDE LA PASARELA DE PAGO
	@PostMapping("/pedido/confirmar")
	public String confirmarPago(HttpSession session, RedirectAttributes flash) {
		CarritoDto carrito = carritoService.obtenerCarrito(session);

		if (carrito == null || carrito.getItems().isEmpty()) {
			return "redirect:/";
		}

		PedidoRequest request = new PedidoRequest();
		request.setIdUsuario((Integer) session.getAttribute("idUsuario"));
		request.setItems(carrito.getItems());

		// GUARDAR PEDIDO
		ResultadoResponse resultado = pedidoService.registrarPedido(request);

		if (!resultado.success()) {
			flash.addFlashAttribute("alert", Alert.sweetAlertError(resultado.mensaje()));
			return "redirect:/carrito";
		}

		// LIMPIAR EL CARRITO
		carrito.getItems().clear();
		session.setAttribute("carrito", carrito);

		flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Compra realizada con éxito! Revisa tu historial."));
		return "redirect:/";
	}

	@GetMapping("/pedido/mis-compras")
	public String verMisCompras(HttpSession session, Model model, RedirectAttributes flash) {
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");

		if (idUsuario == null) {
			flash.addFlashAttribute("alert", Alert.sweetAlertError("Inicia sesión para ver tus compras."));
			return "redirect:/login/iniciar-sesion";
		}

		List<Pedido> misCompras = pedidoService.listarPedidosPorUsuario(idUsuario);

		List<Map<String, Object>> pedidosPlanos = new ArrayList<>();

		for (Pedido p : misCompras) {
			Map<String, Object> dto = new HashMap<>();
			dto.put("idPedido", p.getIdPedido());
			dto.put("fechaCompra", p.getFechaCompra());
			dto.put("total", p.getTotal());
			dto.put("estado", p.getEstado());
			pedidosPlanos.add(dto);
		}

		model.addAttribute("pedidos", pedidosPlanos);

		return "mis_compras";
	}

	@GetMapping("/pedido/detalle-json")
	@ResponseBody
	@Transactional(readOnly = true)
	public ResponseEntity<?> obtenerDetalleJson(@RequestParam("idPedido") Integer idPedido) {
		List<Map<String, Object>> respuesta = new ArrayList<>();
		try {
			Pedido pedido = pedidoService.buscarPedidoPorId(idPedido);

			if (pedido == null || pedido.getLstDetallePedido() == null) {
				return ResponseEntity.ok(respuesta);
			}

			for (var detalle : pedido.getLstDetallePedido()) {
				Map<String, Object> item = new HashMap<>();

				if (detalle.getLibro() != null) {
					item.put("titulo", detalle.getLibro().getTitulo());
					item.put("autor", detalle.getLibro().getAutor());
				} else {
					item.put("titulo", "Libro no disponible");
					item.put("autor", "Desconocido");
				}

				item.put("precio", detalle.getPrecioUnitario());
				item.put("cantidad", detalle.getCantidad());
				respuesta.add(item);
			}

			return ResponseEntity.ok(respuesta);

		} catch (Exception e) {
			System.err.println("ERROR EN /pedido/detalle-json para ID: " + idPedido);
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error interno en el servidor al recuperar los detalles del pedido.");
		}
	}
}