package com.alianza.clientes.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad JPA que representa un cliente en la base de datos.
 * Esta clase pertenece a la capa de infraestructura y contiene
 * anotaciones específicas de JPA y validación.
 */
@Entity
@Table(name = "clientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {

    /**
     * Identificador único del cliente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Clave compartida única que identifica al cliente
     */
    @NotBlank
    @Column(unique = true)
    private String sharedKey;

    /**
     * Nombre completo del cliente
     */
    @NotBlank
    private String nombre;

    /**
     * Número de teléfono del cliente
     */
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    private String telefono;

    /**
     * Dirección de correo electrónico del cliente
     */
    @NotBlank
    @Email
    private String email;

    /**
     * Fecha de inicio de la relación con el cliente
     */
    @NotNull
    private LocalDate fechaInicio;

    /**
     * Fecha de finalización de la relación con el cliente
     */
    @NotNull
    private LocalDate fechaFin;

    /**
     * Fecha de creación del registro del cliente
     */
    private LocalDate fechaCreacion;

    /**
     * Método que se ejecuta antes de persistir la entidad
     * para establecer la fecha de creación si no está definida
     */
    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
    }
}