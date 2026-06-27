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

import com.librolink.dto.AuthDto;
import com.librolink.dto.LibroFilter;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Libro;
import com.librolink.repository.ICategoriaRepository;
import com.librolink.service.ILibroService;
import com.librolink.service.IUsuarioService;
import com.librolink.util.Alert;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final IUsuarioService usuarioService;
	private final ILibroService libroService;
	private final ICategoriaRepository categoriaRepo;

	// ==========================================
	// 🏠 RUTA PRINCIPAL: CATÁLOGO DE LA TIENDA (CLIENTE)
	// ==========================================
	@GetMapping("/")
	public String index(@RequestParam(name = "autor", required = false) String autor,
			@RequestParam(name = "idCategoria", required = false) Integer idCategoria,
			@RequestParam(name = "ordenPrecio", required = false) String ordenPrecio, Model model) {

		// 1. Armamos el DTO de filtros con lo que viene del HTML
		LibroFilter filtro = new LibroFilter();
		filtro.setTitulo(autor);
		filtro.setAutor(autor);
		filtro.setIdCategoria(idCategoria);
		filtro.setOrdenPrecio(ordenPrecio);

		// 2. 🌟 LLAMAMOS AL SERVICIO: Recuerda que tu LibroServiceImpl ya sabe si mandar activos o filtrar
		List<Libro> listaFiltrada = libroService.buscarLibrosPorFiltros(filtro);

		// 3. Enviamos los datos ordenados a la vista pública
		model.addAttribute("lstLibros", listaFiltrada);
		model.addAttribute("categorias", categoriaRepo.findAll());

		return "index";
	}

	// DASHBOARD ADMIN
	@GetMapping("/dashboard")
	public String dashboard(HttpSession session) {
		if (session.getAttribute("idUsuario") == null || !"ADMIN".equals(session.getAttribute("rol"))) {
			return "redirect:/";
		}
		return "dashboard";
	}

	// NOSOTROS HTML
	@GetMapping("/nosotros")
	public String nosotros() {
		return "nosotros";
	}

	// CONTACTO HTML
	@GetMapping("/contacto")
	public String contacto() {
		return "contacto";
	}

	@PostMapping("/contacto/enviar")
	public String enviarContacto(@RequestParam("nombre") String nombre, @RequestParam("email") String email,
			@RequestParam("asunto") String asunto, @RequestParam("mensaje") String mensaje, RedirectAttributes flash) {

		System.out.println("Mensaje recibido de: " + nombre + " (" + email + ") - Asunto: " + asunto);
		flash.addFlashAttribute("alert",
				Alert.sweetAlertSuccess("¡Mensaje enviado! Nos comunicaremos contigo al correo registrado."));
		return "redirect:/contacto";
	}

	// REGISTRO HTML
	@GetMapping("/registro")
	public String mostrarRegistro(Model model) {
		model.addAttribute("authDto", new AuthDto());
		return "registro";
	}

	// DETALLE LIBRO HTML
	@GetMapping("/libro/detalle")
	public String verDetalle(@RequestParam("idLibro") Integer idLibro, Model model) {
		Libro libro = libroService.buscarLibroPorId(idLibro);

		// Filtramos usando 'listarLibrosActivos()' para evitar sugerir libros borrados de la tienda
		List<Libro> similares = libroService.listarLibrosActivos().stream()
				.filter(l -> l.getCategoria().getIdCategoria().equals(libro.getCategoria().getIdCategoria())
						&& !l.getIdLibro().equals(idLibro))
				.limit(4).toList();

		model.addAttribute("libro", libro);
		model.addAttribute("listaSimilares", similares);

		return "libro_detalle";
	}

	// VER MI PERFIL HTML
	@GetMapping("/usuario/mi-perfil")
	public String verMiPerfil(HttpSession session, Model model, RedirectAttributes flash) {
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");

		if (idUsuario == null) {
			flash.addFlashAttribute("alert", Alert.sweetAlertError("Por favor, inicia sesión para ver tu perfil."));
			return "redirect:/login/iniciar-sesion";
		}

		var usuario = usuarioService.buscarUsuarioPorId(idUsuario);
		model.addAttribute("usuario", usuario);

		return "mi_perfil";
	}

	// C: REGISTRAR USUARIO
	@PostMapping("/registrar")
	public String registrarUsuario(@Valid @ModelAttribute("authDto") AuthDto authDto, BindingResult result,
			RedirectAttributes flash, Model model) {

		if (result.hasErrors()) {
			return "registro";
		}

		ResultadoResponse respuesta = usuarioService.registrarUsuario(authDto);

		if (!respuesta.success()) {
			model.addAttribute("alert", Alert.sweetAlertError(respuesta.mensaje()));
			return "registro";
		}

		flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Registro exitoso! Ya puedes iniciar sesión."));
		return "redirect:/login/iniciar-sesion";
	}
}