package com.alianza.clientes.infrastructure.adapter.rest.converters;

import java.util.List;
import java.util.stream.Collectors;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.infrastructure.adapter.rest.constants.RestConstants;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteFilterDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.PageResponseDTO;

/**
 * Converter para las principales operciones del dominio Cliente
 */
public class ClienteConverter {

    private ClienteConverter() {
        // Evita la instanciación de la clase utilitaria
    }

    /**
     * Convierte un DTO de filtro a un objeto de dominio de filtro
     * 
     * @param dto DTO de filtro a convertir
     * @return Objeto de dominio de filtro
     */
    public static ClienteFilter toFilter(ClienteFilterDTO dto) {
        return ClienteFilter.builder()
                .sharedKey(dto.getSharedKey())
                .nombre(dto.getNombre())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .build();
    }

    /**
     * Convierte un DTO a un objeto de dominio
     * 
     * @param dto DTO a convertir
     * @return Objeto de dominio
     */
    public static Cliente toDomain(ClienteDTO dto) {
        return Cliente.builder()
                .id(dto.getId())
                .sharedKey(dto.getSharedKey())
                .nombre(dto.getNombre())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .fechaCreacion(dto.getFechaCreacion())
                .build();
    }

    /**
     * Convierte un objeto de dominio a un DTO
     * 
     * @param domain Objeto de dominio a convertir
     * @return DTO
     */
    public static ClienteDTO toDTO(Cliente domain) {
        return ClienteDTO.builder()
                .id(domain.getId())
                .sharedKey(domain.getSharedKey())
                .nombre(domain.getNombre())
                .telefono(domain.getTelefono())
                .email(domain.getEmail())
                .fechaInicio(domain.getFechaInicio())
                .fechaFin(domain.getFechaFin())
                .fechaCreacion(domain.getFechaCreacion())
                .build();
    }

    /**
     * Convierte una respuesta paginada de dominio a un DTO de respuesta paginada
     * 
     * @param pageResponse Respuesta paginada de dominio
     * @return DTO de respuesta paginada
     */
    public static PageResponseDTO<ClienteDTO> toPageResponseDTO(PageResponse<Cliente> pageResponse) {
        List<ClienteDTO> clienteDTOs = pageResponse.getContent().stream()
                .map(ClienteConverter::toDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ClienteDTO>builder()
                .content(clienteDTOs)
                .pageNumber(pageResponse.getPageNumber())
                .pageSize(pageResponse.getPageSize())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .first(pageResponse.isFirst())
                .last(pageResponse.isLast())
                .empty(pageResponse.isEmpty())
                .build();
    }

    /**
     * Genera el contenido CSV a partir de una lista de clientes
     * 
     * @param clientes Lista de clientes a convertir
     * @return Contenido CSV como String
     */
    public static String generateCsvContent(List<Cliente> clientes) {
        StringBuilder csv = new StringBuilder();
        // Encabezados
        csv.append(RestConstants.HEADERS_CSV_CLIENT);
        // Datos
        clientes.forEach(cliente -> {
            csv.append(cliente.getSharedKey()).append(",")
                    .append(cliente.getNombre()).append(",")
                    .append(cliente.getTelefono()).append(",")
                    .append(cliente.getEmail()).append(",")
                    .append(cliente.getFechaInicio()).append(",")
                    .append(cliente.getFechaFin()).append(",")
                    .append(cliente.getFechaCreacion()).append("\n");
        });
        return csv.toString();
    }

}
