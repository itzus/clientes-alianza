package com.alianza.clientes.infrastructure.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * DTO (Data Transfer Object) para transferir información de clientes entre
 * la API REST y la capa de aplicación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    /**
     * Identificador único del cliente
     */
    private Long id;

    /**
     * Clave compartida única que identifica al cliente
     */
    @NotBlank(message = "El shared key es obligatorio")
    private String sharedKey;

    /**
     * Nombre completo del cliente
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Número de teléfono del cliente
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    private String telefono;

    /**
     * Dirección de correo electrónico del cliente
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    /**
     * Fecha de inicio de la relación con el cliente
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    /**
     * Fecha de finalización de la relación con el cliente
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    /**
     * Fecha de creación del registro del cliente
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCreacion;
}