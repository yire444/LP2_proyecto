package com.librolink.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.librolink.model.Pedido;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {
	
	List<Pedido> findAllByOrderByIdPedidoDesc();
	
	@Query("SELECT p FROM Pedido p " +
	       "LEFT JOIN FETCH p.lstDetallePedido d " +
	       "LEFT JOIN FETCH d.libro " +
	       "WHERE p.idPedido = :idPedido")
	Optional<Pedido> encontrarPedidoConDetalles(@Param("idPedido") Integer idPedido);
	
	List<Pedido> findByUsuarioIdUsuarioOrderByIdPedidoDesc(Integer idUsuario);
	
	@Query("""
			SELECT p
			FROM Pedido p
			WHERE 
				(:idUsuario IS NULL OR p.usuario.idUsuario = :idUsuario)
				AND
				(:fecha IS NULL OR CAST(p.fechaCompra AS LocalDate) = :fecha)
			""")
	List<Pedido> findAllByFilters(
		@Param("idUsuario") Integer idUsuario,
		@Param("fecha") LocalDate fecha
	);
}