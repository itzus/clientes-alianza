package com.alianza.clientes.infrastructure.adapter.rest;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.domain.port.api.ClienteServicePort;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteFilterDTO;
import com.alianza.clientes.infrastructure.adapter.rest.dto.PageResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para ClienteRestAdapter
 */
@WebMvcTest(ClienteRestAdapter.class)
class ClienteRestAdapterTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ClienteServicePort clienteServicePort;

        @Autowired
        private ObjectMapper objectMapper;

        private Cliente clienteTest;
        private List<Cliente> clientes;
        private ClienteDTO clienteDTO;
        private PageResponse<Cliente> pageResponse;

        @BeforeEach
        void setUp() {
                clienteTest = Cliente.builder()
                                .id(1L)
                                .sharedKey("CLI001")
                                .nombre("Juan Pérez")
                                .telefono("+57 300 123 4567")
                                .email("juan.perez@email.com")
                                .fechaInicio(LocalDate.of(2024, 1, 15))
                                .fechaFin(LocalDate.of(2024, 12, 31))
                                .fechaCreacion(LocalDate.now())
                                .build();

                clienteDTO = new ClienteDTO();
                clienteDTO.setSharedKey("CLI001");
                clienteDTO.setNombre("Juan Pérez");
                clienteDTO.setTelefono("+57 300 123 4567");
                clienteDTO.setEmail("juan.perez@email.com");
                clienteDTO.setFechaInicio(LocalDate.of(2024, 1, 15));
                clienteDTO.setFechaFin(LocalDate.of(2024, 12, 31));

                clientes = new ArrayList<>();
                clientes.add(clienteTest);

                pageResponse = PageResponse.<Cliente>builder()
                                .content(Arrays.asList(clienteTest))
                                .pageNumber(0)
                                .pageSize(10)
                                .totalElements(1L)
                                .totalPages(1)
                                .last(true)
                                .build();
        }

        @Test
        void testCreateCliente_Success() throws Exception {
                // Given
                when(clienteServicePort.saveCliente(any(Cliente.class))).thenReturn(clienteTest);

                // When & Then
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.sharedKey").value("CLI001"))
                                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                                .andExpect(jsonPath("$.telefono").value("+57 300 123 4567"))
                                .andExpect(jsonPath("$.email").value("juan.perez@email.com"));

                verify(clienteServicePort).saveCliente(any(Cliente.class));
        }

        @Test
        void testCreateCliente_ValidationError() throws Exception {
                // Given
                ClienteDTO invalidDTO = new ClienteDTO();
                invalidDTO.setSharedKey(""); // Invalid empty shared key
                invalidDTO.setNombre(""); // Invalid empty name
                invalidDTO.setEmail("invalid-email"); // Invalid email format

                // When & Then
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest());

                verify(clienteServicePort, never()).saveCliente(any(Cliente.class));
        }

        @Test
        void testGetClienteBySharedKey_Found() throws Exception {
                // Given
                when(clienteServicePort.findBySharedKey("CLI001")).thenReturn(clienteTest);

                // When & Then
                mockMvc.perform(get("/clientes/CLI001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sharedKey").value("CLI001"))
                                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                                .andExpect(jsonPath("$.telefono").value("+57 300 123 4567"))
                                .andExpect(jsonPath("$.email").value("juan.perez@email.com"));

                verify(clienteServicePort).findBySharedKey("CLI001");
        }

        @Test
        void testGetClienteBySharedKey_NotFound() throws Exception {
                // Given
                when(clienteServicePort.findBySharedKey("CLI999")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/clientes/CLI999"))
                                .andExpect(status().isNotFound());

                verify(clienteServicePort).findBySharedKey("CLI999");
        }

        @Test
        void testGetAllClientes() throws Exception {
                // Given
                when(clienteServicePort.findAllClientes(0, 10, null, null)).thenReturn(pageResponse);

                // When & Then
                mockMvc.perform(get("/clientes")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].sharedKey").value("CLI001"))
                                .andExpect(jsonPath("$.pageNumber").value(0))
                                .andExpect(jsonPath("$.pageSize").value(10))
                                .andExpect(jsonPath("$.totalElements").value(1))
                                .andExpect(jsonPath("$.totalPages").value(1))
                                .andExpect(jsonPath("$.last").value(true));

                verify(clienteServicePort).findAllClientes(0, 10, null, null);
        }

        @Test
        void testGetClientesFiltered() throws Exception {
                // Given
                when(clienteServicePort.findClientesByFilter(any(ClienteFilter.class), eq(0), eq(10), null, null))
                                .thenReturn(pageResponse);

                // When & Then
                mockMvc.perform(get("/clientes/filter")
                                .param("nombre", "Juan")
                                .param("email", "juan")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].sharedKey").value("CLI001"))
                                .andExpect(jsonPath("$.pageNumber").value(0))
                                .andExpect(jsonPath("$.pageSize").value(10));

                verify(clienteServicePort).findClientesByFilter(any(ClienteFilter.class), eq(0), eq(10), null, null);
        }

        @Test
        void testExportClientesToCsv() throws Exception {
                // Given
                String csvContent = "Shared Key,Nombre,Teléfono,Email,Fecha Inicio,Fecha Fin,Fecha Creación\n" +
                                "CLI001,Juan Pérez,+57 300 123 4567,juan.perez@email.com,2024-01-15,2024-12-31,2024-01-10\n";

                when(clienteServicePort.exportClientesToCsv()).thenReturn(clientes);

                // When & Then
                mockMvc.perform(get("/clientes/export/csv"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("text/csv"))
                                .andExpect(header().string("Content-Disposition", "attachment; filename=clientes.csv"))
                                .andExpect(content().string(csvContent));

                verify(clienteServicePort).exportClientesToCsv();
        }

        @Test
        void testCreateCliente_DuplicateSharedKey() throws Exception {
                // Given
                when(clienteServicePort.saveCliente(any(Cliente.class)))
                                .thenThrow(new IllegalArgumentException(
                                                "Ya existe un cliente con el shared key: CLI001"));

                // When & Then
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("Ya existe un cliente con el shared key: CLI001"));

                verify(clienteServicePort).saveCliente(any(Cliente.class));
        }

        @Test
        void testGetAllClientes_DefaultPagination() throws Exception {
                // Given
                when(clienteServicePort.findAllClientes(0, 20, null, null)).thenReturn(pageResponse);

                // When & Then
                mockMvc.perform(get("/clientes"))
                                .andExpect(status().isOk());

                verify(clienteServicePort).findAllClientes(0, 20, null, null);
        }

        @Test
        void testGetClientesFiltered_WithDateRange() throws Exception {
                // Given
                when(clienteServicePort.findClientesByFilter(any(ClienteFilter.class), eq(0), eq(10), null, null))
                                .thenReturn(pageResponse);

                // When & Then
                mockMvc.perform(get("/clientes/filter")
                                .param("fechaInicio", "2024-01-01")
                                .param("fechaFin", "2024-12-31")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk());

                verify(clienteServicePort).findClientesByFilter(any(ClienteFilter.class), eq(0), eq(10), null, null);
        }

        @Test
        void testGetClientesFiltered_EmptyFilter() throws Exception {
                // Given
                when(clienteServicePort.findClientesByFilter(any(ClienteFilter.class), eq(0), eq(10), null, null))
                                .thenReturn(pageResponse);

                // When & Then
                mockMvc.perform(get("/clientes/filter")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk());

                verify(clienteServicePort).findClientesByFilter(any(ClienteFilter.class), eq(0), eq(10), null, null);
        }
}