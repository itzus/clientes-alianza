package com.alianza.clientes.infrastructure.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) para transferir criterios de filtrado de clientes
 * entre la API REST y la capa de aplicación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteFilterDTO {

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    
    /**
     * Fecha de fin para filtrar clientes
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
}