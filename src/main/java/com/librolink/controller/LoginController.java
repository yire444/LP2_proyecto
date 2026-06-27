package com.librolink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.librolink.dto.AuthDto;
import com.librolink.service.IUsuarioService;
import com.librolink.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/login") 
@RequiredArgsConstructor
public class LoginController {

	private final IUsuarioService usuarioService;
	
	
	@GetMapping("/iniciar-sesion") 
	public String mostrarLogin(Model model) {
		model.addAttribute("authDto", new AuthDto());
		return "login"; 
	}
	
	// INICIAR SESIÓN
	@PostMapping("/iniciar-sesion")
	public String iniciarSesion(
			@ModelAttribute AuthDto filter, 
			Model model, 
			RedirectAttributes flash,
			HttpSession session
	) {

		var usuario = usuarioService.loginUsuario(filter.getCorreo(), filter.getPassword());

		if (usuario == null) {
			model.addAttribute("alert", Alert.sweetAlertError("Cuenta y/o clave incorrectos."));
			model.addAttribute("authDto", filter);
			return "login";
		}
		
		if (Boolean.FALSE.equals(usuario.getActivo())) {
			model.addAttribute("alert", Alert.sweetAlertError("La cuenta se encuentra inactiva. Comuníquese con soporte."));
			model.addAttribute("authDto", filter);
			return "login";
		}
		
		session.setAttribute("idUsuario", usuario.getIdUsuario());
		session.setAttribute("fullName", usuario.getFullName());
		session.setAttribute("rol", usuario.getRol());
		
		session.setMaxInactiveInterval(300);

		return "redirect:/";
	}
	
	// CERRAR SESIÓN
	@GetMapping("/cerrar-sesion")
	public String cerrarSesion(HttpSession session) {
		session.invalidate(); 
		return "redirect:/"; 
	}
}