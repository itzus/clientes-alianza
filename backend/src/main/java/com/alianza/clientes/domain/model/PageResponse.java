package com.alianza.clientes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Clase de dominio que representa una respuesta paginada genérica.
 * Esta clase es independiente de frameworks y tecnologías específicas.
 *
 * @param <T> Tipo de contenido de la página
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

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
    
    /**
     * Indica si es la primera página
     */
    private boolean first;
    
    /**
     * Indica si la página está vacía
     */
    private boolean empty;
    
    /**
     * Método de conveniencia para verificar si es la primera página
     */
    public boolean isFirst() {
        return pageNumber == 0;
    }
    
    /**
     * Método de conveniencia para verificar si es la última página
     */
    public boolean isLast() {
        return last;
    }
    
    /**
     * Método de conveniencia para verificar si la página está vacía
     */
    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}