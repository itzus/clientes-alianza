package com.alianza.clientes.infrastructure.adapter.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.domain.port.spi.ClientePersistencePort;
import com.alianza.clientes.infrastructure.adapter.persistence.entity.ClienteEntity;
import com.alianza.clientes.infrastructure.adapter.persistence.mapper.ClienteMapper;
import com.alianza.clientes.infrastructure.adapter.persistence.mapper.CommonMapper;
import com.alianza.clientes.infrastructure.adapter.persistence.repository.ClienteJpaRepository;
import com.alianza.clientes.infrastructure.adapter.persistence.repository.ClienteSpecification;

import lombok.RequiredArgsConstructor;

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
        return ClienteMapper.mapToDomain(clienteRepository.save(ClienteMapper.mapToEntity(cliente)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Cliente> findBySharedKey(String sharedKey) {
        return clienteRepository.findBySharedKey(sharedKey)
                .map(ClienteMapper::mapToDomain);
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
        return ClienteMapper.createPageResponse(
                clienteRepository.findAll(PageRequest.of(page, size, CommonMapper.createSort(sortBy, sortDir))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<Cliente> findClientesByFilter(ClienteFilter filter, int page, int size, String sortBy,
            String sortDir) {
        Page<ClienteEntity> clientePage = clienteRepository.findAll(ClienteSpecification
                .buildSpecification(filter), PageRequest.of(page, size, CommonMapper.createSort(sortBy, sortDir)));
        return ClienteMapper.createPageResponse(clientePage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Cliente> findAllClientes() {
        return clienteRepository.findAll().stream()
                .map(ClienteMapper::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Cliente> findTopByOrderByIdDesc() {
        return clienteRepository.findTopByOrderByIdDesc().map(ClienteMapper::mapToDomain);
    }

}