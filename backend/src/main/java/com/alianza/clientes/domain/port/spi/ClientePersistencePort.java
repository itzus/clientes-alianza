package com.alianza.clientes.domain.port.spi;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (SPI) que define las operaciones de persistencia para clientes.
 * Este puerto es implementado por los adaptadores secundarios (repositorios) y
 * utilizado por la capa de aplicación.
 */
public interface ClientePersistencePort {

    /**
     * Guarda un cliente en el sistema de persistencia
     * 
     * @param cliente El cliente a guardar
     * @return El cliente guardado con su ID asignado
     */
    Cliente saveCliente(Cliente cliente);

    /**
     * Busca un cliente por su sharedKey
     * 
     * @param sharedKey La clave compartida única del cliente
     * @return Optional con el cliente si existe, vacío si no
     */
    Optional<Cliente> findBySharedKey(String sharedKey);

    /**
     * Verifica si existe un cliente con el sharedKey proporcionado
     * 
     * @param sharedKey La clave compartida única a verificar
     * @return true si existe, false si no
     */
    boolean existsBySharedKey(String sharedKey);

    /**
     * Obtiene todos los clientes con paginación
     * 
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sortBy Campo por el cual ordenar
     * @param sortDir Dirección de ordenamiento (asc o desc)
     * @return Respuesta paginada con los clientes
     */
    PageResponse<Cliente> findAllClientes(int page, int size, String sortBy, String sortDir);

    /**
     * Busca clientes aplicando filtros con paginación
     * 
     * @param filter Filtros a aplicar
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sortBy Campo por el cual ordenar
     * @param sortDir Dirección de ordenamiento (asc o desc)
     * @return Respuesta paginada con los clientes filtrados
     */
    PageResponse<Cliente> findClientesByFilter(ClienteFilter filter, int page, int size, String sortBy, String sortDir);

    /**
     * Obtiene todos los clientes
     * 
     * @return Lista de todos los clientes
     */
    List<Cliente> findAllClientes();
}