package com.librolink.service;

import java.util.List;
import com.librolink.dto.PedidoFilter;
import com.librolink.dto.PedidoRequest;
import com.librolink.dto.ResultadoResponse;
import com.librolink.model.Pedido;

public interface IPedidoService {
	
	// R: LISTAR TODOS LOS PEDIDOS (ADMIN)
	List<Pedido> listarTodosPedidos();
	
	List<Pedido> listarPedidosPorUsuario(Integer idUsuario);
	
	List<Pedido> buscarPedidosPorFiltros(PedidoFilter filter);
	ResultadoResponse registrarPedido(PedidoRequest request);
	Pedido buscarPedidoPorId(Integer idPedido);
	ResultadoResponse anularPedido(Integer idPedido);
}