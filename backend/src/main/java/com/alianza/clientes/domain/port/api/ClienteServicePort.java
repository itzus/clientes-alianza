package com.alianza.clientes.domain.port.api;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;

import java.util.List;

/**
 * Puerto de entrada (API) que define las operaciones disponibles para la gestión de clientes.
 * Este puerto es implementado por la capa de aplicación y utilizado por los adaptadores primarios (controladores).
 */
public interface ClienteServicePort {

    /**
     * Guarda un nuevo cliente en el sistema
     * 
     * @param cliente El cliente a guardar
     * @return El cliente guardado con su ID asignado
     * @throws IllegalArgumentException si ya existe un cliente con el mismo sharedKey
     */
    Cliente saveCliente(Cliente cliente);

    /**
     * Busca un cliente por su sharedKey
     * 
     * @param sharedKey La clave compartida única del cliente
     * @return El cliente encontrado
     * @throws IllegalArgumentException si no existe un cliente con el sharedKey proporcionado
     */
    Cliente findBySharedKey(String sharedKey);

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
     * Exporta todos los clientes para generar un CSV
     * 
     * @return Lista de todos los clientes
     */
    List<Cliente> exportClientesToCsv();
}