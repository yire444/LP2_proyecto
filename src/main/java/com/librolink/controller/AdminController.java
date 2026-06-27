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

import com.librolink.model.Libro;
import com.librolink.model.Pedido;
import com.librolink.model.Usuario;
import com.librolink.service.ILibroService;
import com.librolink.service.IPedidoService;
import com.librolink.service.IUsuarioService;
import com.librolink.repository.ICategoriaRepository;
import com.librolink.dto.ResultadoResponse;
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
		
		// 🌟 Cambiado para apuntar a la carpeta admin
		return "admin/historial_ventas_admin";
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
		
		// 🌟 Cambiado para apuntar a la carpeta admin
		return "admin/admin_libros_listado";
	}

	// 2. Formulario Nuevo Libro
	@GetMapping("/libro/nuevo")
	public String nuevoLibroForm(HttpSession session, Model model) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		model.addAttribute("libro", new Libro());
		model.addAttribute("categorias", categoriaRepo.findAll());
		
		// 🌟 Cambiado para apuntar a la carpeta admin
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
		model.addAttribute("categorias", categoriaRepo.findAll());
		
		// 🌟 Cambiado para apuntar a la carpeta admin
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
			model.addAttribute("categorias", categoriaRepo.findAll());
			// 🌟 Cambiado para apuntar a la carpeta admin si hay errores de validación
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
			model.addAttribute("categorias", categoriaRepo.findAll());
			// 🌟 Cambiado para apuntar a la carpeta admin
			return "admin/admin_libro_formulario";
		}

		flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Catálogo actualizado con éxito!"));
		return "redirect:/libro/listado";
	}
}