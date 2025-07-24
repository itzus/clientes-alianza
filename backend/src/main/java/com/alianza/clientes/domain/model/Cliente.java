package com.alianza.clientes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad de dominio que representa un cliente en el sistema.
 * Contiene toda la información relevante de un cliente sin dependencias
 * de frameworks o tecnologías específicas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    /**
     * Identificador único del cliente
     */
    private Long id;
    
    /**
     * Clave compartida única que identifica al cliente
     */
    private String sharedKey;
    
    /**
     * Nombre completo del cliente
     */
    private String nombre;
    
    /**
     * Número de teléfono del cliente
     */
    private String telefono;
    
    /**
     * Dirección de correo electrónico del cliente
     */
    private String email;
    
    /**
     * Fecha de inicio de la relación con el cliente
     */
    private LocalDate fechaInicio;
    
    /**
     * Fecha de finalización de la relación con el cliente
     */
    private LocalDate fechaFin;
    
    /**
     * Fecha de creación del registro del cliente
     */
    private LocalDate fechaCreacion;
}