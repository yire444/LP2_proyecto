package com.librolink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.librolink.dto.CarritoDto;
import com.librolink.dto.LibroSeleccionado;
import com.librolink.model.Libro;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements ICarritoService {

    private final ILibroService libroService;

    @Override
    public CarritoDto obtenerCarrito(HttpSession session) {
        CarritoDto carrito = (CarritoDto) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new CarritoDto();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    @Override
    public void agregarLibro(Integer idLibro, HttpSession session) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        Libro libro = libroService.buscarLibroPorId(idLibro);
        CarritoDto carrito = obtenerCarrito(session);
        carrito.setIdUsuario(idUsuario);

        LibroSeleccionado existente = carrito.getItems().stream()
                .filter(item -> item.getIdLibro().equals(idLibro))
                .findFirst().orElse(null);

        if (existente != null) {
            if (existente.getCantidad() < libro.getStock()) {
                existente.setCantidad(existente.getCantidad() + 1);
            }
        } else if (libro.getStock() > 0) {
            LibroSeleccionado nuevoItem = new LibroSeleccionado();
            nuevoItem.setIdLibro(libro.getIdLibro());
            nuevoItem.setTitulo(libro.getTitulo());
            nuevoItem.setAutor(libro.getAutor());
            nuevoItem.setImagen(libro.getImagen());
            nuevoItem.setPrecio(libro.getPrecio().doubleValue());
            nuevoItem.setCantidad(1);
            carrito.getItems().add(nuevoItem);
        }
        session.setAttribute("carrito", carrito);
    }

    @Override
    public boolean actualizarCantidad(Integer idLibro, Integer cantidad, HttpSession session) {
        CarritoDto carrito = obtenerCarrito(session);
        for (LibroSeleccionado item : carrito.getItems()) {
            if (item.getIdLibro().equals(idLibro)) {
                item.setCantidad(cantidad > 0 ? cantidad : 1);
                session.setAttribute("carrito", carrito);
                return true;
            }
        }
        return false;
    }

    @Override
    public String validarYProcesarStock(List<Integer> ids, List<Integer> cantidades, HttpSession session) {
        CarritoDto carrito = obtenerCarrito(session);
        
        // Sincronizar primero
        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            Integer cant = cantidades.get(i);
            for (LibroSeleccionado item : carrito.getItems()) {
                if (item.getIdLibro().equals(id)) {
                    item.setCantidad(cant);
                    break;
                }
            }
        }
        
        // Validar stock después
        for (LibroSeleccionado item : carrito.getItems()) {
            Libro libroReal = libroService.buscarLibroPorId(item.getIdLibro());
            if (item.getCantidad() > libroReal.getStock()) {
                return "Stock insuficiente para '" + item.getTitulo() + "'. Disponibles: " + libroReal.getStock();
            }
        }
        
        session.setAttribute("carrito", carrito);
        return null; // Todo conforme
    }
}