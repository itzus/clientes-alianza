package com.alianza.clientes.service;

import com.alianza.clientes.model.Cliente;
import com.alianza.clientes.model.dto.ClienteDTO;
import com.alianza.clientes.model.dto.ClienteFilterDTO;
import com.alianza.clientes.model.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClienteService {

    /**
     * Guarda un nuevo cliente
     * @param clienteDTO datos del cliente a guardar
     * @return el cliente guardado
     */
    ClienteDTO saveCliente(ClienteDTO clienteDTO);

    /**
     * Busca un cliente por su sharedKey
     * @param sharedKey clave única del cliente
     * @return el cliente encontrado
     */
    ClienteDTO findBySharedKey(String sharedKey);

    /**
     * Obtiene todos los clientes con paginación
     * @param pageable información de paginación
     * @return página de clientes
     */
    PageResponseDTO<ClienteDTO> findAllClientes(Pageable pageable);

    /**
     * Busca clientes por filtros
     * @param filter filtros de búsqueda
     * @param pageable información de paginación
     * @return página de clientes filtrados
     */
    PageResponseDTO<ClienteDTO> findClientesByFilter(ClienteFilterDTO filter, Pageable pageable);

    /**
     * Exporta todos los clientes a CSV
     * @return lista de clientes para exportar
     */
    List<ClienteDTO> exportClientesToCsv();
}