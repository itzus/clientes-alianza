package com.alianza.clientes.infrastructure.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO (Data Transfer Object) para transferir respuestas paginadas
 * entre la API REST y la capa de aplicación.
 *
 * @param <T> Tipo de contenido de la página
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta paginada que contiene una lista de elementos y metadatos de paginación")
public class PageResponseDTO<T> {

    /**
     * Contenido de la página
     */
    @Schema(description = "Lista de elementos en la página actual")
    private List<T> content;
    
    /**
     * Número de la página actual (0-indexed)
     */
    @Schema(description = "Número de página actual (base 0)", example = "0")
    private int pageNumber;
    
    /**
     * Tamaño de la página
     */
    @Schema(description = "Tamaño de página solicitado", example = "10")
    private int pageSize;
    
    /**
     * Número total de elementos
     */
    @Schema(description = "Número total de elementos disponibles", example = "100")
    private long totalElements;
    
    /**
     * Número total de páginas
     */
    @Schema(description = "Número total de páginas disponibles", example = "10")
    private int totalPages;
    
    /**
     * Indica si es la última página
     */
    @Schema(description = "Indica si es la última página", example = "false")
    private boolean last;
    
    /**
     * Indica si es la primera página
     */
    @Schema(description = "Indica si es la primera página", example = "true")
    private boolean first;
    
    /**
     * Indica si la página está vacía
     */
    @Schema(description = "Indica si la página está vacía", example = "false")
    private boolean empty;
}