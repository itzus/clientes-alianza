package com.alianza.clientes.controller;

import com.alianza.clientes.model.dto.ClienteDTO;
import com.alianza.clientes.model.dto.ClienteFilterDTO;
import com.alianza.clientes.model.dto.PageResponseDTO;
import com.alianza.clientes.service.ClienteService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clientes", description = "API para la gestión de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente", description = "Crea un nuevo cliente con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de cliente inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un cliente con el mismo shared key")
    })
    public ResponseEntity<ClienteDTO> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("Recibida solicitud para crear cliente: {}", clienteDTO.getSharedKey());
        ClienteDTO savedCliente = clienteService.saveCliente(clienteDTO);
        return new ResponseEntity<>(savedCliente, HttpStatus.CREATED);
    }

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
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponseDTO<ClienteDTO> response = clienteService.findAllClientes(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/shared-key/{sharedKey}")
    @Operation(summary = "Buscar cliente por shared key", description = "Busca un cliente por su shared key único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteDTO> getClienteBySharedKey(@PathVariable String sharedKey) {
        log.info("Recibida solicitud para buscar cliente por sharedKey: {}", sharedKey);
        ClienteDTO clienteDTO = clienteService.findBySharedKey(sharedKey);
        return ResponseEntity.ok(clienteDTO);
    }

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
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponseDTO<ClienteDTO> response = clienteService.findClientesByFilter(filter, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Exportar clientes a CSV", description = "Exporta todos los clientes a un archivo CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo CSV generado exitosamente")
    })
    public void exportClientesToCsv(HttpServletResponse response) throws IOException {
        log.info("Recibida solicitud para exportar clientes a CSV");
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=clientes.csv");
        
        List<ClienteDTO> clientes = clienteService.exportClientesToCsv();
        
        // Escribir encabezados CSV
        String csvHeader = "ID,Shared Key,Nombre,Teléfono,Email,Fecha Inicio,Fecha Fin,Fecha Creación\n";
        response.getWriter().write(csvHeader);
        
        // Escribir datos
        for (ClienteDTO cliente : clientes) {
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
}