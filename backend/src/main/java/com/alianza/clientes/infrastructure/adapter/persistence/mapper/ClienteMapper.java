package com.alianza.clientes.infrastructure.adapter.persistence.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.infrastructure.adapter.persistence.entity.ClienteEntity;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    /**
     * Crea una respuesta paginada a partir de una página de entidades JPA
     * 
     * @param clientePage Página de entidades ClienteEntity
     * @return PageResponse con objetos de dominio Cliente
     */
    public static PageResponse<Cliente> createPageResponse(Page<ClienteEntity> clientePage) {
        List<Cliente> clienteDTOs = clientePage.getContent().stream()
                .map(ClienteMapper::mapToDomain)
                .collect(Collectors.toList());

        return PageResponse.<Cliente>builder()
                .content(clienteDTOs)
                .pageNumber(clientePage.getNumber())
                .pageSize(clientePage.getSize())
                .totalElements(clientePage.getTotalElements())
                .totalPages(clientePage.getTotalPages())
                .last(clientePage.isLast())
                .first(clientePage.isFirst())
                .empty(clientePage.isEmpty())
                .build();
    }

    /**
     * Convierte un objeto de dominio Cliente a una entidad JPA ClienteEntity
     * 
     * @param domain Objeto de dominio
     * @return Entidad JPA
     */
    public static ClienteEntity mapToEntity(Cliente domain) {
        return ClienteEntity.builder()
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
     * Convierte una entidad JPA ClienteEntity a un objeto de dominio Cliente
     * 
     * @param entity Entidad JPA
     * @return Objeto de dominio
     */
    public static Cliente mapToDomain(ClienteEntity entity) {
        return Cliente.builder()
                .id(entity.getId())
                .sharedKey(entity.getSharedKey())
                .nombre(entity.getNombre())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

}
