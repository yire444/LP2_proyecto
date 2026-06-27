package com.librolink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.librolink.dto.AuthDto;
import com.librolink.dto.ResultadoResponse;
import com.librolink.dto.UsuarioFilter;
import com.librolink.model.Usuario;
import com.librolink.repository.IUsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

	private final IUsuarioRepository usuarioRepo;

	// R: LISTAR USUARIOS
	@Override
	public List<Usuario> getAll() {
		return usuarioRepo.findAll();
	}

	@Override
	public List<Usuario> search(UsuarioFilter filter) {
		var nom = (filter.getNombre() != null && !filter.getNombre().trim().isEmpty()) ? filter.getNombre().trim()
				: null;
		var ape = (filter.getApellido() != null && !filter.getApellido().trim().isEmpty()) ? filter.getApellido().trim()
				: null;
		var corr = (filter.getCorreo() != null && !filter.getCorreo().trim().isEmpty()) ? filter.getCorreo().trim()
				: null;
		var doc = (filter.getDocumento() != null && !filter.getDocumento().trim().isEmpty())
				? filter.getDocumento().trim()
				: null;
		var tel = (filter.getTelefono() != null && !filter.getTelefono().trim().isEmpty()) ? filter.getTelefono().trim()
				: null;

		return usuarioRepo.findAllByAdminFilters(nom, ape, corr, doc, tel, filter.getFechaDesde(),
				filter.getFechaHasta());
	}

	// C: REGISTRAR USUARIO
	@Override
	public ResultadoResponse registrarUsuario(AuthDto dto) {

		// VALIDACIONES:
		if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
			return ResultadoResponse.error("Las contraseñas no coinciden.");
		} else if (usuarioRepo.findByCorreo(dto.getCorreo()).isPresent()) {
			return ResultadoResponse.error("El correo electrónico ya está registrado.");
		} else if (usuarioRepo.findByDocumentoIdentidad(dto.getDocumentoIdentidad()).isPresent()) {
			return ResultadoResponse.error("El documento de identidad ya se encuentra registrado.");
		} else if (usuarioRepo.findByTelefono(dto.getTelefono()).isPresent()) {
			return ResultadoResponse.error("El teléfono ya se encuentra registrado.");
		}

		try {
			var nuevo = new Usuario();
			nuevo.setNombre(dto.getNombre());
			nuevo.setApellido(dto.getApellido());
			nuevo.setDocumentoIdentidad(dto.getDocumentoIdentidad());
			nuevo.setFechaNacimiento(dto.getFechaNacimiento());
			nuevo.setCorreo(dto.getCorreo());
			nuevo.setPasswordHash(dto.getPassword());
			nuevo.setTelefono(dto.getTelefono());

			var registro = usuarioRepo.save(nuevo);
			return ResultadoResponse.exito("Usuario", registro.getIdUsuario(), "registrado");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}

	// LOGUIN DEL USUARIO
	@Override
	public Usuario loginUsuario(String correo, String password) {
		var usuario = usuarioRepo.loginUsuario(correo, password);

		if (usuario == null) {
			return null;
		}

		return usuario;
	}

	// BUSCAR USUARIO POR ID
	@Override
	public Usuario buscarUsuarioPorId(Integer idUsuario) {
		return usuarioRepo.findById(idUsuario).orElseThrow();
	}

	// U: ACTUALIZAR USUARIO
	@Override
	public ResultadoResponse actualizarUsuario(Usuario usuarioData) {
		try {
			var usuarioBD = this.buscarUsuarioPorId(usuarioData.getIdUsuario());

			if (!usuarioBD.getTelefono().equals(usuarioData.getTelefono())) {
				if (usuarioRepo.findByTelefono(usuarioData.getTelefono()).isPresent()) {
					return ResultadoResponse.error("El teléfono ya se encuentra registrado por otro usuario.");
				}
			}

			usuarioBD.setNombre(usuarioData.getNombre());
			usuarioBD.setApellido(usuarioData.getApellido());
			usuarioBD.setTelefono(usuarioData.getTelefono());
			usuarioBD.setCorreo(usuarioData.getCorreo());
			usuarioBD.setPasswordHash(usuarioData.getPasswordHash());

			var registro = usuarioRepo.save(usuarioBD);
			return ResultadoResponse.exito("Usuario", registro.getIdUsuario(), "actualizado");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}

	// D: ELIMINAR USUARIO
	@Override
	@Transactional
	public ResultadoResponse eliminarUsuario(Integer idUsuario) {
		var usuario = this.buscarUsuarioPorId(idUsuario);
		try {
			usuario.setActivo(!usuario.getActivo());
			usuarioRepo.save(usuario);

			var estado = usuario.getActivo() ? "activado" : "desactivada";
			return ResultadoResponse.exito("Usuario", usuario.getIdUsuario(), estado);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
}