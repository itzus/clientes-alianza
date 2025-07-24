package com.alianza.clientes.infrastructure.adapter.rest.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Clase que representa una respuesta de error de validación para la API REST.
 * Extiende ErrorResponse para incluir detalles específicos de errores de validación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {

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
    
    /**
     * Mapa de errores de validación por campo
     */
    private Map<String, String> validationErrors;
}