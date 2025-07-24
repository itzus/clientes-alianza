package com.alianza.clientes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Clase de dominio que representa los criterios de filtrado para búsqueda de clientes.
 * Esta clase es independiente de frameworks y tecnologías específicas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteFilter {

    /**
     * Clave compartida para filtrar clientes
     */
    private String sharedKey;
    
    /**
     * Nombre para filtrar clientes
     */
    private String nombre;
    
    /**
     * Teléfono para filtrar clientes
     */
    private String telefono;
    
    /**
     * Email para filtrar clientes
     */
    private String email;
    
    /**
     * Fecha de inicio para filtrar clientes
     */
    private LocalDate fechaInicio;
    
    /**
     * Fecha de fin para filtrar clientes
     */
    private LocalDate fechaFin;
}