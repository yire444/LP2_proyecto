package com.librolink.service;

import java.util.List;
import com.librolink.dto.AuthDto;
import com.librolink.dto.ResultadoResponse;
import com.librolink.dto.UsuarioFilter;
import com.librolink.model.Usuario;

public interface IUsuarioService {
	
	//R: LISTAR TODOS LOS USUARIOS
	List<Usuario> getAll();
	
	//R: BUSCAR USUARIOS
	List<Usuario> search(UsuarioFilter filter);
	
	//C: REGISTRAR USUARIO
	ResultadoResponse registrarUsuario(AuthDto dto);
	
	//LOGUIEN DEL USUARIO
	Usuario loginUsuario(String correo, String password);
	
	//BUSCAR USUARIO
	Usuario buscarUsuarioPorId(Integer idUsuario);
	
	//U:ACTUALIZAR USUARIO
	ResultadoResponse actualizarUsuario(Usuario usuarioData);
	
	//D: ELIMINAR USUARIO
	ResultadoResponse eliminarUsuario(Integer idUsuario);
}