package com.alianza.clientes.application.service;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.domain.port.api.ClienteServicePort;
import com.alianza.clientes.domain.port.spi.ClientePersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del puerto de entrada ClienteServicePort que orquesta las
 * operaciones
 * de negocio relacionadas con clientes utilizando el puerto de persistencia.
 */
@Slf4j
@RequiredArgsConstructor
public class ClienteService implements ClienteServicePort {

    private final ClientePersistencePort clientePersistencePort;

    /**
     * Genera una nueva sharedKey para un cliente.
     * 
     * @return Una sharedKey única para el cliente.
     */
    private String generateSharedKey() {
        Optional<Cliente> clienteMaxId = clientePersistencePort.findTopByOrderByIdDesc();
        Long idSharedKey = clienteMaxId.isPresent() ? clienteMaxId.get().getId() + 1 : 1;
        if (idSharedKey < 10)
            return "CLI" + "00" + idSharedKey;
        else if (idSharedKey < 100)
            return "CLI" + "0" + idSharedKey;
        return "CLI" + idSharedKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cliente saveCliente(Cliente cliente) {
        cliente.setSharedKey(generateSharedKey());
        log.info("Guardando cliente con sharedKey: {}", cliente.getSharedKey());
        if (clientePersistencePort.existsBySharedKey(cliente.getSharedKey())) {
            log.error("Error al guardar cliente: sharedKey {} ya existe", cliente.getSharedKey());
            throw new IllegalArgumentException("Ya existe un cliente con el sharedKey: " + cliente.getSharedKey());
        }
        cliente.setFechaCreacion(LocalDate.now());
        Cliente savedCliente = clientePersistencePort.saveCliente(cliente);
        log.info("Cliente guardado exitosamente con ID: {}", savedCliente.getId());
        return savedCliente;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cliente findBySharedKey(String sharedKey) {
        log.info("Buscando cliente por sharedKey: {}", sharedKey);
        return clientePersistencePort.findBySharedKey(sharedKey)
                .orElseThrow(() -> {
                    log.error("Cliente no encontrado con sharedKey: {}", sharedKey);
                    return new IllegalArgumentException("Cliente no encontrado con sharedKey: " + sharedKey);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<Cliente> findAllClientes(int page, int size, String sortBy, String sortDir) {
        log.info("Obteniendo todos los clientes con paginación: página {}, tamaño {}", page, size);
        return clientePersistencePort.findAllClientes(page, size, sortBy, sortDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<Cliente> findClientesByFilter(ClienteFilter filter, int page, int size, String sortBy,
            String sortDir) {
        log.info("Buscando clientes por filtros: {}", filter);
        return clientePersistencePort.findClientesByFilter(filter, page, size, sortBy, sortDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Cliente> exportClientesToCsv() {
        log.info("Exportando todos los clientes a CSV");
        List<Cliente> clientes = clientePersistencePort.findAllClientes();
        log.info("Se exportarán {} clientes a CSV", clientes.size());
        return clientes;
    }
}