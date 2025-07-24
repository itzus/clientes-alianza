package com.alianza.clientes.service.impl;

import com.alianza.clientes.exception.ResourceNotFoundException;
import com.alianza.clientes.exception.UniqueConstraintViolationException;
import com.alianza.clientes.model.Cliente;
import com.alianza.clientes.model.dto.ClienteDTO;
import com.alianza.clientes.model.dto.ClienteFilterDTO;
import com.alianza.clientes.model.dto.PageResponseDTO;
import com.alianza.clientes.repository.ClienteRepository;
import com.alianza.clientes.repository.ClienteSpecification;
import com.alianza.clientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClienteDTO saveCliente(ClienteDTO clienteDTO) {
        log.info("Guardando cliente con sharedKey: {}", clienteDTO.getSharedKey());
        
        if (clienteRepository.existsBySharedKey(clienteDTO.getSharedKey())) {
            log.error("Error al guardar cliente: sharedKey {} ya existe", clienteDTO.getSharedKey());
            throw new UniqueConstraintViolationException("Ya existe un cliente con el sharedKey: " + clienteDTO.getSharedKey());
        }
        
        Cliente cliente = mapToEntity(clienteDTO);
        Cliente savedCliente = clienteRepository.save(cliente);
        log.info("Cliente guardado exitosamente con ID: {}", savedCliente.getId());
        
        return mapToDTO(savedCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findBySharedKey(String sharedKey) {
        log.info("Buscando cliente por sharedKey: {}", sharedKey);
        
        Cliente cliente = clienteRepository.findBySharedKey(sharedKey)
                .orElseThrow(() -> {
                    log.error("Cliente no encontrado con sharedKey: {}", sharedKey);
                    return new ResourceNotFoundException("Cliente no encontrado con sharedKey: " + sharedKey);
                });
        
        log.info("Cliente encontrado con sharedKey: {}", sharedKey);
        return mapToDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ClienteDTO> findAllClientes(Pageable pageable) {
        log.info("Obteniendo todos los clientes con paginación: página {}, tamaño {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Cliente> clientePage = clienteRepository.findAll(pageable);
        return createPageResponse(clientePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ClienteDTO> findClientesByFilter(ClienteFilterDTO filter, Pageable pageable) {
        log.info("Buscando clientes por filtros: {}", filter);
        
        Specification<Cliente> spec = ClienteSpecification.buildSpecification(filter);
        Page<Cliente> clientePage = clienteRepository.findAll(spec, pageable);
        
        log.info("Se encontraron {} clientes con los filtros aplicados", clientePage.getTotalElements());
        return createPageResponse(clientePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> exportClientesToCsv() {
        log.info("Exportando todos los clientes a CSV");
        
        List<Cliente> clientes = clienteRepository.findAll();
        log.info("Se exportarán {} clientes a CSV", clientes.size());
        
        return clientes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PageResponseDTO<ClienteDTO> createPageResponse(Page<Cliente> clientePage) {
        List<ClienteDTO> clienteDTOs = clientePage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ClienteDTO>builder()
                .content(clienteDTOs)
                .pageNumber(clientePage.getNumber())
                .pageSize(clientePage.getSize())
                .totalElements(clientePage.getTotalElements())
                .totalPages(clientePage.getTotalPages())
                .last(clientePage.isLast())
                .build();
    }

    private Cliente mapToEntity(ClienteDTO dto) {
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

    private ClienteDTO mapToDTO(Cliente entity) {
        return ClienteDTO.builder()
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