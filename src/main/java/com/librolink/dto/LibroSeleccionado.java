package com.librolink.dto;

import lombok.Data;

@Data
public class LibroSeleccionado {
    private Integer idLibro;
    private String titulo;
    private String autor;
    private String imagen;
    private Double precio;
    private Integer cantidad;

    // 🌟 REEMPLAZA EL ATRIBUTO FIJO POR ESTE MÉTODO DINÁMICO:
    public Double getSubtotal() {
        if (this.precio == null || this.cantidad == null) {
            return 0.0;
        }
        return this.precio * this.cantidad;
    }
}