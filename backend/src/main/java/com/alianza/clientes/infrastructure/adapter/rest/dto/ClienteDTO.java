package com.alianza.clientes.infrastructure.adapter.rest.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para transferir información de clientes entre
 * la API REST y la capa de aplicación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos del cliente para operaciones de API")
public class ClienteDTO {

    /**
     * Identificador único del cliente
     */
    @Schema(description = "Identificador único del cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    /**
     * Clave compartida única que identifica al cliente
     */
    @Schema(description = "Clave compartida única del cliente", example = "jdoe123", required = true)
    private String sharedKey;

    /**
     * Nombre completo del cliente
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez", required = true)
    private String nombre;

    /**
     * Número de teléfono del cliente
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    @Schema(description = "Número de teléfono del cliente", example = "3001234567", required = true)
    private String telefono;

    /**
     * Dirección de correo electrónico del cliente
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Schema(description = "Correo electrónico del cliente", example = "juan.perez@email.com", required = true)
    private String email;

    /**
     * Fecha de inicio de la relación con el cliente
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(description = "Fecha de inicio de la relación comercial", example = "2024-01-15", type = "string", format = "date", required = true)
    private LocalDate fechaInicio;

    /**
     * Fecha de finalización de la relación con el cliente
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    @Schema(description = "Fecha de fin de la relación comercial", example = "2024-12-31", type = "string", format = "date", required = true)
    private LocalDate fechaFin;

    /**
     * Fecha de creación del registro del cliente
     */
    @Schema(description = "Fecha de creación del registro", example = "2024-01-01", type = "string", format = "date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate fechaCreacion;
}