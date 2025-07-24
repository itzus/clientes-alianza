package com.alianza.clientes.infrastructure.adapter.persistence;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.domain.port.spi.ClientePersistencePort;
import com.alianza.clientes.infrastructure.adapter.persistence.entity.ClienteEntity;
import com.alianza.clientes.infrastructure.adapter.persistence.repository.ClienteJpaRepository;
import com.alianza.clientes.infrastructure.adapter.persistence.repository.ClienteSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia que implementa el puerto ClientePersistencePort.
 * Actúa como un puente entre el dominio y la infraestructura de persistencia.
 */
@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClientePersistencePort {

    private final ClienteJpaRepository clienteRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Cliente saveCliente(Cliente cliente) {
        ClienteEntity entity = mapToEntity(cliente);
        ClienteEntity savedEntity = clienteRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Cliente> findBySharedKey(String sharedKey) {
        return clienteRepository.findBySharedKey(sharedKey)
                .map(this::mapToDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsBySharedKey(String sharedKey) {
        return clienteRepository.existsBySharedKey(sharedKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<Cliente> findAllClientes(int page, int size, String sortBy, String sortDir) {
        Sort sort = createSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ClienteEntity> clientePage = clienteRepository.findAll(pageable);
        return createPageResponse(clientePage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<Cliente> findClientesByFilter(ClienteFilter filter, int page, int size, String sortBy, String sortDir) {
        Sort sort = createSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        org.springframework.data.jpa.domain.Specification<ClienteEntity> spec = 
                ClienteSpecification.buildSpecification(filter);
        
        Page<ClienteEntity> clientePage = clienteRepository.findAll(spec, pageable);
        return createPageResponse(clientePage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Cliente> findAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Crea un objeto Sort basado en el campo y dirección proporcionados
     * 
     * @param sortBy Campo por el cual ordenar
     * @param sortDir Dirección de ordenamiento (asc o desc)
     * @return Objeto Sort configurado
     */
    private Sort createSort(String sortBy, String sortDir) {
        return sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }

    /**
     * Crea una respuesta paginada a partir de una página de entidades JPA
     * 
     * @param clientePage Página de entidades ClienteEntity
     * @return PageResponse con objetos de dominio Cliente
     */
    private PageResponse<Cliente> createPageResponse(Page<ClienteEntity> clientePage) {
        List<Cliente> clienteDTOs = clientePage.getContent().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());

        return PageResponse.<Cliente>builder()
                .content(clienteDTOs)
                .pageNumber(clientePage.getNumber())
                .pageSize(clientePage.getSize())
                .totalElements(clientePage.getTotalElements())
                .totalPages(clientePage.getTotalPages())
                .last(clientePage.isLast())
                .build();
    }

    /**
     * Convierte un objeto de dominio Cliente a una entidad JPA ClienteEntity
     * 
     * @param domain Objeto de dominio
     * @return Entidad JPA
     */
    private ClienteEntity mapToEntity(Cliente domain) {
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
    private Cliente mapToDomain(ClienteEntity entity) {
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