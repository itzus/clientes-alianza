package com.alianza.clientes.controller;

import com.alianza.clientes.model.dto.ClienteDTO;
import com.alianza.clientes.model.dto.ClienteFilterDTO;
import com.alianza.clientes.model.dto.PageResponseDTO;
import com.alianza.clientes.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;
    private PageResponseDTO<ClienteDTO> pageResponse;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        clienteDTO = ClienteDTO.builder()
                .id(1L)
                .sharedKey("CLI123")
                .nombre("Cliente Prueba")
                .telefono("1234567890")
                .email("cliente@test.com")
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(6))
                .fechaCreacion(LocalDate.now())
                .build();

        List<ClienteDTO> clientes = Arrays.asList(clienteDTO);
        pageResponse = PageResponseDTO.<ClienteDTO>builder()
                .content(clientes)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();
    }

    @Test
    void createCliente_Success() throws Exception {
        // Configurar comportamiento del mock
        when(clienteService.saveCliente(any(ClienteDTO.class))).thenReturn(clienteDTO);

        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(clienteDTO.getId()))
                .andExpect(jsonPath("$.sharedKey").value(clienteDTO.getSharedKey()))
                .andExpect(jsonPath("$.nombre").value(clienteDTO.getNombre()));
    }

    @Test
    void getAllClientes_Success() throws Exception {
        // Configurar comportamiento del mock
        when(clienteService.findAllClientes(any(Pageable.class))).thenReturn(pageResponse);

        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(get("/clientes")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].sharedKey").value(clienteDTO.getSharedKey()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getClienteBySharedKey_Success() throws Exception {
        // Configurar comportamiento del mock
        when(clienteService.findBySharedKey(anyString())).thenReturn(clienteDTO);

        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(get("/clientes/search/shared-key/{sharedKey}", "CLI123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteDTO.getId()))
                .andExpect(jsonPath("$.sharedKey").value(clienteDTO.getSharedKey()));
    }

    @Test
    void searchClientes_Success() throws Exception {
        // Configurar comportamiento del mock
        when(clienteService.findClientesByFilter(any(ClienteFilterDTO.class), any(Pageable.class)))
                .thenReturn(pageResponse);

        ClienteFilterDTO filter = new ClienteFilterDTO();
        filter.setSharedKey("CLI");

        // Ejecutar solicitud y verificar respuesta
        mockMvc.perform(post("/clientes/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter))
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].sharedKey").value(clienteDTO.getSharedKey()));
    }

    @Test
    void exportClientesToCsv_Success() throws Exception {
        // Configurar datos de prueba
        List<ClienteDTO> clientes = Arrays.asList(clienteDTO);

        // Configurar comportamiento del mock
        when(clienteService.exportClientesToCsv()).thenReturn(clientes);

        // Ejecutar solicitud y verificar respuesta
        MvcResult result = mockMvc.perform(get("/clientes/export/csv"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=clientes.csv", response.getHeader("Content-Disposition"));
        
        String content = response.getContentAsString();
        assertTrue(content.contains("ID,Shared Key,Nombre,Teléfono,Email,Fecha Inicio,Fecha Fin,Fecha Creación"));
        assertTrue(content.contains(clienteDTO.getSharedKey()));
    }
}