package com.librolink.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "tbl_libros")
public class Libro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_libro")
	private Integer idLibro;

	@Column(name = "titulo", nullable = false, length = 150)
	private String titulo;

	@Column(name = "autor", nullable = false, length = 100)
	private String autor;

	@Column(name = "descripcion", length = 255, nullable = false)
	private String descripcion;

	@Column(name = "precio", nullable = false, precision = 10, scale = 2)
	private BigDecimal precio;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@Column(name = "imagen", nullable = false, length = 255)
	private String imagen = "default.jpg";

	@Column(name = "activo", nullable = false)
	private Boolean activo = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;

	public String getActivoDescripcion() {
		return activo ? "Activo" : "Inactivo";
	}
}