package com.librolink.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.librolink.model.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
	
	Optional<Usuario> findByCorreo(String correo);
	
	Optional<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
	
	Optional<Usuario> findByTelefono(String telefono);
	
	@Query("select u from Usuario u where u.correo = :correo and u.passwordHash = :password")
	Usuario loginUsuario(@Param("correo") String correo, @Param("password") String password);
	
	@Query("""
				select u 
				from Usuario as u 
				where 
				(:nombre is null or u.nombre like %:nombre%) 
				and 
				(:apellido is null or u.apellido like %:apellido%) 
				and 
				(:correo is null or u.correo like %:correo%) 
				and 
				(:documento is null or u.documentoIdentidad = :documento) 
				and 
				(:telefono is null or u.telefono = :telefono) 
				and 
				(:fechaDesde is null or u.fechaNacimiento >= :fechaDesde) 
				and 
				(:fechaHasta is null or u.fechaNacimiento <= :fechaHasta)
				""")
	List<Usuario> findAllByAdminFilters(
		@Param("nombre") String nombre,
		@Param("apellido") String apellido,
		@Param("correo") String correo,
		@Param("documento") String documento,
		@Param("telefono") String telefono,
		@Param("fechaDesde") LocalDate fechaDesde,
		@Param("fechaHasta") LocalDate fechaHasta
	);
}