package com.alianza.clientes.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteFilterDTO {

    private String sharedKey;
    private String nombre;
    private String telefono;
    private String email;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
}