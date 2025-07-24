package com.alianza.clientes.infrastructure.adapter.rest.dto;

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
public class PageResponseDTO<T> {

    /**
     * Contenido de la página
     */
    private List<T> content;
    
    /**
     * Número de la página actual (0-indexed)
     */
    private int pageNumber;
    
    /**
     * Tamaño de la página
     */
    private int pageSize;
    
    /**
     * Número total de elementos
     */
    private long totalElements;
    
    /**
     * Número total de páginas
     */
    private int totalPages;
    
    /**
     * Indica si es la última página
     */
    private boolean last;
}