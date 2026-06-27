package com.librolink.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.librolink.dto.PedidoFilter;
import com.librolink.dto.PedidoRequest;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.DetallePedido;
import com.librolink.model.Pedido;
import com.librolink.repository.ILibroRepository;
import com.librolink.repository.IPedidoRepository;
import com.librolink.repository.IUsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {
	
	private final IPedidoRepository pedidoRepo;
	private final IUsuarioRepository usuarioRepo;
	private final ILibroRepository libroRepo;
	
	// R: LISTAR TODOS LOS PEDIDOS (ADMIN)
	@Override
	public List<Pedido> listarTodosPedidos() {
		return pedidoRepo.findAllByOrderByIdPedidoDesc();
	}
	
	// BUSCAR PEDIDOS FILTROS (Mantenido para búsquedas complejas)
	@Override
	public List<Pedido> buscarPedidosPorFiltros(PedidoFilter filter) {
		return pedidoRepo.findAllByFilters(filter.getIdUsuario(), filter.getFecha());
	}
	
	// BUSCAR PEDIDO POR USUARIO 
	@Override
	public List<Pedido> listarPedidosPorUsuario(Integer idUsuario) {
		// 🌟 SOLUCIÓN APLICADA: Usamos la consulta optimizada por convención de nombres
		// Evita los cortes de flujo (ERR_INCOMPLETE_CHUNKED_ENCODING) al renderizar la vista
		return pedidoRepo.findByUsuarioIdUsuarioOrderByIdPedidoDesc(idUsuario);
	}
	
	// BUSCAR PEDIDOS POR ID (Para alimentar el modal AJAX de detalles)
	@Override
	public Pedido buscarPedidoPorId(Integer idPedido) {
		return pedidoRepo.encontrarPedidoConDetalles(idPedido)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + idPedido));
	}
	
	// C: REGISTRAR PEDIDOS
	@Override
	@Transactional
	public ResultadoResponse registrarPedido(PedidoRequest request) {
		if (request.getItems() == null || request.getItems().isEmpty()) {
			return ResultadoResponse.error("El carrito de compras no contiene productos.");
		}

		try {
			var usuarioBD = usuarioRepo.findById(request.getIdUsuario())
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			var nuevoPedido = new Pedido();
			nuevoPedido.setUsuario(usuarioBD);
			nuevoPedido.setFechaCompra(LocalDateTime.now());
			nuevoPedido.setEstado("PAGADO");
			
			double totalCarrito = 0.0;
			List<DetallePedido> detalles = new ArrayList<>();

			for (var item : request.getItems()) {
				var libroBD = libroRepo.findById(item.getIdLibro())
						.orElseThrow(() -> new RuntimeException("Libro no encontrado"));

				if (libroBD.getStock() < item.getCantidad()) {
					return ResultadoResponse.error(String.format("Stock insuficiente para el libro '%s'. Disponible: %s", libroBD.getTitulo(), libroBD.getStock()));
				}

				libroBD.setStock(libroBD.getStock() - item.getCantidad());
				libroRepo.save(libroBD); 

				var detalle = new DetallePedido();
				detalle.setPedido(nuevoPedido);
				detalle.setLibro(libroBD);
				detalle.setCantidad(item.getCantidad());
				detalle.setPrecioUnitario(BigDecimal.valueOf(item.getPrecio())); 
				detalles.add(detalle);
				
				totalCarrito += item.getSubtotal();
			}

			nuevoPedido.setTotal(BigDecimal.valueOf(totalCarrito));
			nuevoPedido.setLstDetallePedido(detalles);

			var registro = pedidoRepo.save(nuevoPedido);
			return ResultadoResponse.exito("Pedido", registro.getIdPedido(), "procesado con éxito");

		} catch (Exception e) {
			e.printStackTrace();
			return ResultadoResponse.errorTransaccion();
		}
	}
	
	// ANULAR PEDIDO
	@Override
    @Transactional
    public ResultadoResponse anularPedido(Integer idPedido) {
        try {
            var pedidoBD = this.buscarPedidoPorId(idPedido);

            if ("ANULADO".equalsIgnoreCase(pedidoBD.getEstado())) {
                return ResultadoResponse.error("Este pedido ya se encuentra anulado.");
            }

            for (var detalle : pedidoBD.getLstDetallePedido()) {
                var libroBD = detalle.getLibro();
                libroBD.setStock(libroBD.getStock() + detalle.getCantidad());
                libroRepo.save(libroBD);
            }

            pedidoBD.setEstado("ANULADO");
            pedidoRepo.save(pedidoBD);

            return ResultadoResponse.exito("Pedido", pedidoBD.getIdPedido(), "ANULADO correctamente y stock devuelto");

        } catch (Exception e) {
            e.printStackTrace();
            return ResultadoResponse.errorTransaccion();
        }
    }

}
