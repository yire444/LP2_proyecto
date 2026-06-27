package com.librolink.model;

import java.time.LocalDate;
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
@Table(name = "tbl_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false, unique = true)
    private Integer idUsuario;

    @Column(name = "documento_identidad", nullable = false, unique = true, length = 12)
    private String documentoIdentidad; 

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
    
    @Column(name = "telefono", nullable = false, unique = true, length = 9)
    private String telefono;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol = "USER";

    @Column(name = "codigo_verificacion", nullable = true, length = 6)
    private String codigoVerificacion;

    @Column(name = "activo", nullable = true)
    private Boolean activo = true;

    public String getFullName() {
        return String.format("%s %s", nombre, apellido);
    }
}