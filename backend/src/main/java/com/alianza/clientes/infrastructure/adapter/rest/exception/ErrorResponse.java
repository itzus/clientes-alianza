package com.alianza.clientes.infrastructure.adapter.rest.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Respuesta estándar para errores de la API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta estándar para errores de la API")
public class ErrorResponse {

    /**
     * Timestamp cuando ocurrió el error
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora cuando ocurrió el error", example = "2024-01-15 10:30:45")
    private LocalDateTime timestamp;
    
    /**
     * Código de estado HTTP
     */
    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;
    
    /**
     * Tipo de error
     */
    @Schema(description = "Tipo de error HTTP", example = "Bad Request")
    private String error;
    
    /**
     * Mensaje descriptivo del error
     */
    @Schema(description = "Mensaje descriptivo del error", example = "El shared key es obligatorio")
    private String message;
    
    /**
     * Ruta donde ocurrió el error
     */
    @Schema(description = "Ruta del endpoint donde ocurrió el error", example = "/clientes")
    private String path;
}