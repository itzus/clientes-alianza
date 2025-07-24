package com.alianza.clientes.infrastructure.adapter.rest.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Clase que representa una respuesta de error estándar para la API REST.
 * Proporciona información detallada sobre errores que ocurren en el sistema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp cuando ocurrió el error
     */
    private LocalDateTime timestamp;
    
    /**
     * Código de estado HTTP
     */
    private int status;
    
    /**
     * Tipo de error
     */
    private String error;
    
    /**
     * Mensaje descriptivo del error
     */
    private String message;
    
    /**
     * Ruta donde ocurrió el error
     */
    private String path;
}