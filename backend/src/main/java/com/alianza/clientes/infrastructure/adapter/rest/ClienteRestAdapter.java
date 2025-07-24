package com.alianza.clientes.infrastructure.adapter.rest;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.domain.port.api.ClienteServicePort;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteFilterDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador REST que expone las operaciones de clientes a través de una API HTTP.
 * Actúa como un adaptador primario que convierte las peticiones HTTP en llamadas
 * al puerto de entrada del dominio.
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clientes", description = "API para la gestión de clientes")
public class ClienteRestAdapter {

    private final ClienteServicePort clienteServicePort;

    /**
     * Crea un nuevo cliente
     * 
     * @param clienteDTO Datos del cliente a crear
     * @return Cliente creado
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo cliente", description = "Crea un nuevo cliente con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de cliente inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un cliente con el mismo shared key")
    })
    public ResponseEntity<ClienteDTO> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("Recibida solicitud para crear cliente: {}", clienteDTO.getSharedKey());
        
        Cliente cliente = mapToDomain(clienteDTO);
        Cliente savedCliente = clienteServicePort.saveCliente(cliente);
        ClienteDTO responseDTO = mapToDTO(savedCliente);
        
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Obtiene todos los clientes con paginación
     * 
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortBy Campo de ordenamiento
     * @param sortDir Dirección de ordenamiento
     * @return Página de clientes
     */
    @GetMapping
    @Operation(summary = "Obtener todos los clientes", description = "Obtiene todos los clientes con paginación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = PageResponseDTO.class)))
    })
    public ResponseEntity<PageResponseDTO<ClienteDTO>> getAllClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recibida solicitud para obtener todos los clientes: página {}, tamaño {}", page, size);
        
        PageResponse<Cliente> response = clienteServicePort.findAllClientes(page, size, sortBy, sortDir);
        PageResponseDTO<ClienteDTO> responseDTO = mapToPageResponseDTO(response);
        
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Busca un cliente por su shared key
     * 
     * @param sharedKey Clave compartida del cliente
     * @return Cliente encontrado
     */
    @GetMapping("/search/shared-key/{sharedKey}")
    @Operation(summary = "Buscar cliente por shared key", description = "Busca un cliente por su shared key único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteDTO> getClienteBySharedKey(@PathVariable String sharedKey) {
        log.info("Recibida solicitud para buscar cliente por sharedKey: {}", sharedKey);
        
        Cliente cliente = clienteServicePort.findBySharedKey(sharedKey);
        ClienteDTO responseDTO = mapToDTO(cliente);
        
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Busca clientes aplicando filtros
     * 
     * @param filter Filtros de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortBy Campo de ordenamiento
     * @param sortDir Dirección de ordenamiento
     * @return Página de clientes filtrados
     */
    @PostMapping("/search")
    @Operation(summary = "Buscar clientes por filtros", description = "Busca clientes aplicando filtros dinámicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = PageResponseDTO.class)))
    })
    public ResponseEntity<PageResponseDTO<ClienteDTO>> searchClientes(
            @RequestBody ClienteFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recibida solicitud para buscar clientes por filtros: {}", filter);
        
        ClienteFilter domainFilter = mapToFilterDomain(filter);
        PageResponse<Cliente> response = clienteServicePort.findClientesByFilter(domainFilter, page, size, sortBy, sortDir);
        PageResponseDTO<ClienteDTO> responseDTO = mapToPageResponseDTO(response);
        
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Exporta todos los clientes a CSV
     * 
     * @param response Respuesta HTTP
     * @throws IOException Si hay error al escribir el CSV
     */
    @GetMapping("/export/csv")
    @Operation(summary = "Exportar clientes a CSV", description = "Exporta todos los clientes a un archivo CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo CSV generado exitosamente")
    })
    public void exportClientesToCsv(HttpServletResponse response) throws IOException {
        log.info("Recibida solicitud para exportar clientes a CSV");
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=clientes.csv");
        
        List<Cliente> clientes = clienteServicePort.exportClientesToCsv();
        
        // Escribir encabezados CSV
        String csvHeader = "ID,Shared Key,Nombre,Teléfono,Email,Fecha Inicio,Fecha Fin,Fecha Creación\n";
        response.getWriter().write(csvHeader);
        
        // Escribir datos
        for (Cliente cliente : clientes) {
            String line = String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                    cliente.getId(),
                    cliente.getSharedKey(),
                    cliente.getNombre(),
                    cliente.getTelefono(),
                    cliente.getEmail(),
                    cliente.getFechaInicio(),
                    cliente.getFechaFin(),
                    cliente.getFechaCreacion());
            
            response.getWriter().write(line);
        }
        
        response.getWriter().flush();
    }

    /**
     * Convierte un DTO a un objeto de dominio
     * 
     * @param dto DTO a convertir
     * @return Objeto de dominio
     */
    private Cliente mapToDomain(ClienteDTO dto) {
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
    private ClienteDTO mapToDTO(Cliente domain) {
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
     * Convierte un DTO de filtro a un objeto de dominio de filtro
     * 
     * @param dto DTO de filtro a convertir
     * @return Objeto de dominio de filtro
     */
    private ClienteFilter mapToFilterDomain(ClienteFilterDTO dto) {
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
     * Convierte una respuesta paginada de dominio a un DTO de respuesta paginada
     * 
     * @param pageResponse Respuesta paginada de dominio
     * @return DTO de respuesta paginada
     */
    private PageResponseDTO<ClienteDTO> mapToPageResponseDTO(PageResponse<Cliente> pageResponse) {
        List<ClienteDTO> clienteDTOs = pageResponse.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ClienteDTO>builder()
                .content(clienteDTOs)
                .pageNumber(pageResponse.getPageNumber())
                .pageSize(pageResponse.getPageSize())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .last(pageResponse.isLast())
                .build();
    }
}