package com.alianza.clientes.infrastructure.adapter.rest.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Respuesta especializada para errores de validación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta especializada para errores de validación de campos")
public class ValidationErrorResponse {

    /**
     * Timestamp cuando ocurrió el error
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora cuando ocurrió el error de validación", example = "2024-01-15 10:30:45")
    private LocalDateTime timestamp;
    
    /**
     * Código de estado HTTP
     */
    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;
    
    /**
     * Tipo de error
     */
    @Schema(description = "Tipo de error HTTP", example = "Validation Failed")
    private String error;
    
    /**
     * Mensaje descriptivo del error
     */
    @Schema(description = "Mensaje general del error de validación", example = "Error de validación en los datos enviados")
    private String message;
    
    /**
     * Ruta donde ocurrió el error
     */
    @Schema(description = "Ruta del endpoint donde ocurrió el error", example = "/clientes")
    private String path;
    
    /**
     * Mapa de errores de validación por campo
     */
    @Schema(description = "Mapa de errores específicos por campo", example = "{\"nombre\": \"El nombre es obligatorio\", \"email\": \"El formato del email no es válido\"}")
    private Map<String, String> validationErrors;
}