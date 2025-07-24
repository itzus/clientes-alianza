package com.alianza.clientes.integration;

import com.alianza.clientes.infrastructure.adapter.persistence.entity.ClienteEntity;
import com.alianza.clientes.infrastructure.adapter.persistence.repository.ClienteJpaRepository;
import com.alianza.clientes.infrastructure.adapter.rest.dto.ClienteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para el sistema completo de gestión de clientes
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@Transactional
class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;
    private ClienteEntity clienteEntity;

    @BeforeEach
    void setUp() {
        clienteJpaRepository.deleteAll();

        clienteDTO = new ClienteDTO();
        clienteDTO.setSharedKey("CLI001");
        clienteDTO.setNombre("Juan Pérez");
        clienteDTO.setTelefono("+57 300 123 4567");
        clienteDTO.setEmail("juan.perez@email.com");
        clienteDTO.setFechaInicio(LocalDate.of(2024, 1, 15));
        clienteDTO.setFechaFin(LocalDate.of(2024, 12, 31));

        clienteEntity = new ClienteEntity();
        clienteEntity.setSharedKey("CLI002");
        clienteEntity.setNombre("María García");
        clienteEntity.setTelefono("+57 300 987 6543");
        clienteEntity.setEmail("maria.garcia@email.com");
        clienteEntity.setFechaInicio(LocalDate.of(2024, 2, 1));
        clienteEntity.setFechaFin(LocalDate.of(2024, 12, 31));
        clienteEntity.setFechaCreacion(LocalDate.now());
    }

    @Test
    void testCompleteClienteWorkflow() throws Exception {
        // 1. Crear cliente
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sharedKey").value("CLI001"))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.fechaCreacion").exists());

        // 2. Buscar cliente por shared key
        mockMvc.perform(get("/clientes/CLI001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sharedKey").value("CLI001"))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));

        // 3. Listar todos los clientes
        mockMvc.perform(get("/clientes")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].sharedKey").value("CLI001"));

        // 4. Filtrar clientes
        mockMvc.perform(get("/clientes/filter")
                .param("nombre", "Juan")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan Pérez"));

        // 5. Exportar a CSV
        mockMvc.perform(get("/clientes/export/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(containsString("CLI001")))
                .andExpect(content().string(containsString("Juan Pérez")));
    }

    @Test
    void testCreateClienteWithDuplicateSharedKey() throws Exception {
        // Crear primer cliente
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated());

        // Intentar crear segundo cliente con mismo shared key
        ClienteDTO duplicateDTO = new ClienteDTO();
        duplicateDTO.setSharedKey("CLI001"); // Mismo shared key
        duplicateDTO.setNombre("Otro Cliente");
        duplicateDTO.setTelefono("+57 300 999 8888");
        duplicateDTO.setEmail("otro@email.com");
        duplicateDTO.setFechaInicio(LocalDate.of(2024, 3, 1));
        duplicateDTO.setFechaFin(LocalDate.of(2024, 12, 31));

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Ya existe un cliente con el shared key: CLI001")));
    }

    @Test
    void testValidationErrors() throws Exception {
        ClienteDTO invalidDTO = new ClienteDTO();
        invalidDTO.setSharedKey(""); // Vacío
        invalidDTO.setNombre(""); // Vacío
        invalidDTO.setTelefono(""); // Vacío
        invalidDTO.setEmail("email-invalido"); // Formato inválido
        // fechas nulas

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void testFilterByMultipleCriteria() throws Exception {
        // Crear varios clientes
        clienteJpaRepository.save(clienteEntity); // María García
        
        ClienteEntity cliente3 = new ClienteEntity();
        cliente3.setSharedKey("CLI003");
        cliente3.setNombre("Carlos López");
        cliente3.setTelefono("+57 300 555 1234");
        cliente3.setEmail("carlos.lopez@email.com");
        cliente3.setFechaInicio(LocalDate.of(2024, 1, 1));
        cliente3.setFechaFin(LocalDate.of(2024, 6, 30));
        cliente3.setFechaCreacion(LocalDate.now());
        clienteJpaRepository.save(cliente3);

        // Crear cliente a través de API
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated());

        // Filtrar por nombre
        mockMvc.perform(get("/clientes/filter")
                .param("nombre", "María")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre").value("María García"));

        // Filtrar por email
        mockMvc.perform(get("/clientes/filter")
                .param("email", "carlos")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("carlos.lopez@email.com"));

        // Filtrar por rango de fechas
        mockMvc.perform(get("/clientes/filter")
                .param("fechaInicio", "2024-01-01")
                .param("fechaFin", "2024-06-30")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testPagination() throws Exception {
        // Crear múltiples clientes
        for (int i = 1; i <= 25; i++) {
            ClienteEntity cliente = new ClienteEntity();
            cliente.setSharedKey("CLI" + String.format("%03d", i));
            cliente.setNombre("Cliente " + i);
            cliente.setTelefono("+57 300 " + String.format("%03d", i) + " 0000");
            cliente.setEmail("cliente" + i + "@email.com");
            cliente.setFechaInicio(LocalDate.of(2024, 1, 1));
            cliente.setFechaFin(LocalDate.of(2024, 12, 31));
            cliente.setFechaCreacion(LocalDate.now());
            clienteJpaRepository.save(cliente);
        }

        // Probar primera página
        mockMvc.perform(get("/clientes")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.last").value(false));

        // Probar última página
        mockMvc.perform(get("/clientes")
                .param("page", "2")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.pageNumber").value(2))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void testClienteNotFound() throws Exception {
        mockMvc.perform(get("/clientes/CLI999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Cliente no encontrado")));
    }

    @Test
    void testCsvExportWithMultipleClientes() throws Exception {
        // Crear clientes
        clienteJpaRepository.save(clienteEntity);
        
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated());

        // Exportar CSV
        mockMvc.perform(get("/clientes/export/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=clientes.csv"))
                .andExpect(content().string(containsString("Shared Key,Nombre,Teléfono,Email")))
                .andExpect(content().string(containsString("CLI001,Juan Pérez")))
                .andExpect(content().string(containsString("CLI002,María García")));
    }
}