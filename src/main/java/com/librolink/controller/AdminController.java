package com.librolink.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Categoria;
import com.librolink.model.Libro;
import com.librolink.model.Pedido;
import com.librolink.model.Usuario;
import com.librolink.repository.ICategoriaRepository;
import com.librolink.service.ICategoriaService;
import com.librolink.service.ILibroService;
import com.librolink.service.IPedidoService;
import com.librolink.service.IUsuarioService;
import com.librolink.util.Alert;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {

	private final IUsuarioService usuarioService;
	private final ILibroService libroService;
	private final IPedidoService pedidoService;
	private final ICategoriaRepository categoriaRepo;
	private final ICategoriaService categoriaService;

	// SEGURIDAD: Verifica si es ADMIN
	private boolean esAdmin(HttpSession session) {
		String rol = (String) session.getAttribute("rol");
		return session.getAttribute("idUsuario") != null && "ADMIN".equals(rol);
	}

	// ==========================================
	// 👥 GESTIÓN DE USUARIOS
	// ==========================================
	@GetMapping("/usuario/listado")
	public String listarUsuarios(HttpSession session, Model model) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		List<Usuario> listaUsuarios = usuarioService.getAll(); 
		model.addAttribute("lstUsuarios", listaUsuarios);
		
		// 🌟 Cambiado para apuntar a la carpeta admin
		return "admin/admin_usuarios_listado";
	}
	
	@PostMapping("/usuario/eliminar")
	public String cambiarEstadoUsuario(@RequestParam("idUsuario") Integer idUsuario, RedirectAttributes flash, HttpSession session) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		
		// 🌟 Llamamos a tu método existente que invierte el estado lógico
		ResultadoResponse respuesta = usuarioService.eliminarUsuario(idUsuario);
		
		if (!respuesta.success()) {
			flash.addFlashAttribute("alert", Alert.sweetAlertError(respuesta.mensaje()));
			return "redirect:/usuario/listado";
		}
		
		flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("El estado del usuario se actualizó con éxito."));
		return "redirect:/usuario/listado";
	}
	
	// ==========================================
	// 🧾 GESTIÓN DE VENTAS
	// ==========================================
	@GetMapping("/pedido/listado")
	public String listarVentasGlobales(HttpSession session, Model model) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		List<Pedido> todasLasVentas = pedidoService.listarTodosPedidos();
		model.addAttribute("pedidosGlobales", todasLasVentas);
		
		return "admin/historial_ventas_admin";
	}
	
	@PostMapping("/pedido/anular")
	public String anularPedido(@RequestParam("idPedido") Integer idPedido, RedirectAttributes flash, HttpSession session) {
	    // 1. Doble check de seguridad
	    String rol = (String) session.getAttribute("rol");
	    if (session.getAttribute("idUsuario") == null || !"ADMIN".equals(rol)) {
	        return "redirect:/";
	    }
	    
	    // 2. Ejecutar la lógica de tu servicio (devuelve stock y cambia estado)
	    ResultadoResponse respuesta = pedidoService.anularPedido(idPedido);
	    
	    // 3. Evaluar respuesta del Service
	    if (!respuesta.success()) {
	        flash.addFlashAttribute("alert", Alert.sweetAlertError(respuesta.mensaje()));
	        return "redirect:/pedido/listado";
	    }
	    
	    // 4. Éxito absoluto
	    flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("El pedido #" + idPedido + " fue anulado correctamente y el stock regresó al inventario."));
	    return "redirect:/pedido/listado";
	}

	// ==========================================
	// 📚 GESTIÓN DE LIBROS (MANTENIMIENTO)
	// ==========================================
	
	// 1. Listar Libros
	@GetMapping("/libro/listado")
	public String listarLibros(HttpSession session, Model model) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		List<Libro> listaLibros = libroService.listarLibros(); 
		model.addAttribute("lstLibrosAdmin", listaLibros);
		
		return "admin/admin_libros_listado";
	}
	
	//Registrar Libro
	@GetMapping("/libro/nuevo")
	public String nuevoLibroForm(HttpSession session, Model model) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		model.addAttribute("libro", new Libro());
		// 🌟 CAMBIADO: Usamos el servicio ordenado en vez del repo plano
		model.addAttribute("categorias", categoriaService.listarCategorias());
		
		return "admin/admin_libro_formulario";
	}

	// 3. Formulario Editar Libro
	@GetMapping("/libro/editar")
	public String editarLibroForm(@RequestParam("idLibro") Integer idLibro, HttpSession session, Model model) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		Libro libroExistente = libroService.buscarLibroPorId(idLibro);
		model.addAttribute("libro", libroExistente);
		// 🌟 CAMBIADO: Usamos el servicio ordenado en vez del repo plano
		model.addAttribute("categorias", categoriaService.listarCategorias());
		
		return "admin/admin_libro_formulario";
	}

	// 4. Procesar Guardar/Actualizar
	@PostMapping("/libro/guardar")
	public String guardarLibro(@Valid @ModelAttribute("libro") Libro libro, BindingResult result,
			HttpSession session, RedirectAttributes flash, Model model) {
		
		if (!esAdmin(session)) {
			return "redirect:/";
		}

		if (result.hasErrors()) {
			// 🌟 CAMBIADO: Usamos el servicio ordenado aquí también por si falla la validación
			model.addAttribute("categorias", categoriaService.listarCategorias());
			return "admin/admin_libro_formulario";
		}

		ResultadoResponse respuesta;

		if (libro.getIdLibro() != null) {
			respuesta = libroService.actualizarLibro(libro);
		} else {
			respuesta = libroService.registrarLibro(libro);
		}

		if (!respuesta.success()) {
			model.addAttribute("alert", Alert.sweetAlertError(respuesta.mensaje()));
			// 🌟 CAMBIADO: Usamos el servicio ordenado
			model.addAttribute("categorias", categoriaService.listarCategorias());

			return "admin/admin_libro_formulario";
		}

		flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Catálogo actualizado con éxito!"));
		return "redirect:/libro/listado";
	}
	
	@PostMapping("/libro/eliminar")
	public String eliminarLibro(@RequestParam("idLibro") Integer idLibro, RedirectAttributes flash, HttpSession session) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		
		// Ejecuta tu servicio existente
		ResultadoResponse respuesta = libroService.eliminarLibro(idLibro);
		
		if (!respuesta.success()) {
			flash.addFlashAttribute("alert", Alert.sweetAlertError(respuesta.mensaje()));
			return "redirect:/libro/listado";
		}
		
		flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("El libro fue retirado del catálogo con éxito."));
		return "redirect:/libro/listado";
	}
	
	// ==========================================
		// 🏷️ GESTIÓN DE CATEGORÍAS
		// ==========================================

		@GetMapping("/categoria/listado")
		public String listarCategorias(HttpSession session, Model model) {
			if (session.getAttribute("idUsuario") == null || !"ADMIN".equals(session.getAttribute("rol"))) {
				return "redirect:/";
			}
			model.addAttribute("lstCategoriasAdmin", categoriaService.listarCategorias());
			// Objeto vacío para el mini-formulario de registrar/editar en la misma vista o modal
			model.addAttribute("categoriaObj", new Categoria()); 
			return "admin/admin_categorias_listado";
		}

		@PostMapping("/categoria/guardar")
		public String guardarCategoria(@ModelAttribute("categoriaObj") Categoria categoria, 
				HttpSession session, RedirectAttributes flash) {
			if (session.getAttribute("idUsuario") == null || !"ADMIN".equals(session.getAttribute("rol"))) {
				return "redirect:/";
			}

			ResultadoResponse respuesta;
			if (categoria.getIdCategoria() != null) {
				respuesta = categoriaService.actualizarCategoria(categoria);
			} else {
				respuesta = categoriaService.registrarCategoria(categoria);
			}

			if (!respuesta.success()) {
				flash.addFlashAttribute("alert", Alert.sweetAlertError(respuesta.mensaje()));
				return "redirect:/categoria/listado";
			}

			flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Estructura de categorías actualizada!"));
			return "redirect:/categoria/listado";
		}

		@PostMapping("/categoria/eliminar")
		public String cambiarEstadoCategoria(@RequestParam("idCategoria") Integer idCategoria, 
				RedirectAttributes flash, HttpSession session) {
			if (session.getAttribute("idUsuario") == null || !"ADMIN".equals(session.getAttribute("rol"))) {
				return "redirect:/";
			}

			ResultadoResponse respuesta = categoriaService.eliminarCategoria(idCategoria);
			if (!respuesta.success()) {
				flash.addFlashAttribute("alert", Alert.sweetAlertError(respuesta.mensaje()));
				return "redirect:/categoria/listado";
			}

			flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("El estado de la categoría fue modificado con éxito."));
			return "redirect:/categoria/listado";
		}
	
}