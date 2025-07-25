package com.alianza.clientes.infrastructure.adapter.rest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.domain.port.api.ClienteServicePort;
import com.alianza.clientes.infrastructure.adapter.rest.constants.RestConstants;
import com.alianza.clientes.infrastructure.adapter.rest.converters.ClienteConverter;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteFilterDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.PageResponseDTO;
import com.alianza.clientes.infrastructure.adapter.rest.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador REST para la gestión de clientes
 * Expone los endpoints de la API REST y maneja la conversión entre DTOs y
 * objetos de dominio
 */
@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Clientes", description = "API para la gestión completa de clientes")
public class ClienteRestAdapter {

        private final ClienteServicePort clienteServicePort;

        /**
         * Crea un nuevo cliente
         */
        @PostMapping
        @Operation(summary = "Crear nuevo cliente", description = "Crea un nuevo cliente en el sistema. El shared key debe ser único y se asigna automáticamente la fecha de creación.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente", content = @Content(schema = @Schema(implementation = ClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o shared key duplicado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<ClienteDTO> createCliente(
                        @Parameter(description = "Datos del cliente a crear", required = true) @Valid @RequestBody ClienteDTO clienteDTO) {
                log.info("Creando cliente con shared key: {}", clienteDTO.getSharedKey());

                Cliente cliente = ClienteConverter.toDomain(clienteDTO);
                Cliente clienteCreado = clienteServicePort.saveCliente(cliente);
                ClienteDTO response = ClienteConverter.toDTO(clienteCreado);
                log.info("Cliente creado exitosamente con ID: {}", clienteCreado.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Obtiene todos los clientes con paginación
         */
        @GetMapping
        @Operation(summary = "Obtener todos los clientes", description = "Retorna una lista paginada de todos los clientes registrados en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente", content = @Content(schema = @Schema(implementation = PageResponseDTO.class)))
        })
        public ResponseEntity<PageResponseDTO<ClienteDTO>> getAllClientes(
                        @Parameter(description = "Número de página (base 0)", example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Tamaño de página", example = "10") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Campo por el cual ordenar", example = "id") @RequestParam(defaultValue = "id") String sortBy,
                        @Parameter(description = "Dirección de ordenamiento", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {
                log.info("Obteniendo clientes - página: {}, tamaño: {}, ordenado por: {} {}", page, size, sortBy,
                                sortDir);

                PageResponse<Cliente> pageResponse = clienteServicePort.findAllClientes(page, size, sortBy, sortDir);
                PageResponseDTO<ClienteDTO> response = ClienteConverter.toPageResponseDTO(pageResponse);

                log.info("Retornando {} clientes de {} total", response.getContent().size(),
                                response.getTotalElements());
                return ResponseEntity.ok(response);
        }

        /**
         * Obtiene un cliente por su shared key
         */
        @GetMapping("/{sharedKey}")
        @Operation(summary = "Obtener cliente por shared key", description = "Busca y retorna un cliente específico utilizando su shared key único")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente", content = @Content(schema = @Schema(implementation = ClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente no encontrado con el shared key proporcionado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<ClienteDTO> getClienteBySharedKey(
                        @Parameter(description = "Shared key único del cliente", required = true, example = "jdoe123") @PathVariable String sharedKey) {
                log.info("Buscando cliente con shared key: {}", sharedKey);

                try {
                        Cliente cliente = clienteServicePort.findBySharedKey(sharedKey);
                        log.info("Cliente encontrado: {}", cliente.getId());
                        return ResponseEntity.ok(ClienteConverter.toDTO(cliente));
                } catch (IllegalArgumentException e) {
                        log.warn("Cliente no encontrado con shared key: {}", sharedKey);
                        return ResponseEntity.notFound().build();
                }
        }

        /**
         * Filtra clientes según criterios específicos
         */
        @PostMapping("/filter")
        @Operation(summary = "Filtrar clientes", description = "Filtra clientes según criterios específicos como nombre, email, teléfono o rango de fechas")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Clientes filtrados exitosamente", content = @Content(schema = @Schema(implementation = ClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Criterios de filtro inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<PageResponseDTO<ClienteDTO>> filterClientes(
                        @Parameter(description = "Criterios de filtrado", required = true) @Valid @RequestBody ClienteFilterDTO filterDTO) {
                log.info("Filtrando clientes con criterios: {}", filterDTO);
                ClienteFilter filter = ClienteConverter.toFilter(filterDTO);
                PageResponse<Cliente> clientes = clienteServicePort
                                .findClientesByFilter(filter, 0, Integer.MAX_VALUE, "id", "asc");
                PageResponseDTO<ClienteDTO> response = ClienteConverter.toPageResponseDTO(clientes);
                log.info("Encontrados {} clientes que cumplen los criterios", response.getContent().size());
                return ResponseEntity.ok(response);
        }

        /**
         * Exporta clientes a CSV
         */
        @GetMapping("/export/csv")
        @Operation(summary = "Exportar clientes a CSV", description = "Genera y descarga un archivo CSV con todos los clientes registrados en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Archivo CSV generado y descargado exitosamente", content = @Content(mediaType = "application/octet-stream")),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor al generar el CSV", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<byte[]> exportClientesToCsv() {
                log.info("Exportando clientes a CSV");
                try {
                        List<Cliente> clientes = clienteServicePort.exportClientesToCsv();
                        String csvContent = ClienteConverter.generateCsvContent(clientes);
                        byte[] csvData = csvContent.getBytes(StandardCharsets.UTF_8);
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        headers.setContentDispositionFormData("attachment", RestConstants.FILENAME_CSV);
                        log.info("CSV generado exitosamente con {} bytes", csvData.length);
                        return ResponseEntity.ok()
                                        .headers(headers)
                                        .body(csvData);
                } catch (Exception e) {
                        log.error("Error al exportar clientes a CSV", e);
                        return ResponseEntity.internalServerError().build();
                }
        }

}