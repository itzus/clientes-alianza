package com.alianza.clientes.infrastructure.adapter.rest.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para transferir criterios de filtrado de clientes
 * entre la API REST y la capa de aplicación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Criterios de filtrado para búsqueda de clientes")
public class ClienteFilterDTO {

    /**
     * Clave compartida para filtrar clientes
     */
    @Schema(description = "Filtrar por shared key específico", example = "jdoe123")
    private String sharedKey;

    /**
     * Nombre para filtrar clientes
     */
    @Schema(description = "Filtrar por nombre (búsqueda parcial)", example = "Juan")
    private String nombre;

    /**
     * Teléfono para filtrar clientes
     */
    @Schema(description = "Filtrar por número de teléfono", example = "3001234567")
    private String telefono;

    /**
     * Email para filtrar clientes
     */
    @Schema(description = "Filtrar por correo electrónico", example = "juan@email.com")
    private String email;

    /**
     * Fecha de inicio para filtrar clientes
     */
    @Schema(description = "Filtrar por fecha de inicio desde", example = "2024-01-01", type = "string", format = "date")
    private LocalDate fechaInicio;

    /**
     * Fecha de fin para filtrar clientes
     */
    @Schema(description = "Filtrar por fecha de fin hasta", example = "2024-12-31", type = "string", format = "date")
    private LocalDate fechaFin;
}