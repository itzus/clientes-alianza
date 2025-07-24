package com.alianza.clientes.application.service;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.domain.port.spi.ClientePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClienteService
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClientePersistencePort clientePersistencePort;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteTest;
    private ClienteFilter filtroTest;

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

        filtroTest = ClienteFilter.builder()
                .nombre("Juan")
                .email("juan")
                .build();
    }

    @Test
    void testSaveCliente_Success() {
        // Given
        when(clientePersistencePort.existsBySharedKey("CLI001")).thenReturn(false);
        when(clientePersistencePort.saveCliente(any(Cliente.class))).thenReturn(clienteTest);

        // When
        Cliente resultado = clienteService.saveCliente(clienteTest);

        // Then
        assertNotNull(resultado);
        assertEquals("CLI001", resultado.getSharedKey());
        assertEquals("Juan Pérez", resultado.getNombre());
        assertNotNull(resultado.getFechaCreacion());

        verify(clientePersistencePort).existsBySharedKey("CLI001");
        verify(clientePersistencePort).saveCliente(any(Cliente.class));
    }

    @Test
    void testSaveCliente_SharedKeyAlreadyExists() {
        // Given
        when(clientePersistencePort.existsBySharedKey("CLI001")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.saveCliente(clienteTest));

        assertEquals("Ya existe un cliente con el shared key: CLI001", exception.getMessage());
        verify(clientePersistencePort).existsBySharedKey("CLI001");
        verify(clientePersistencePort, never()).saveCliente(any(Cliente.class));
    }

    @Test
    void testFindBySharedKey_Found() {
        // Given
        when(clientePersistencePort.findBySharedKey("CLI001")).thenReturn(Optional.of(clienteTest));

        // When
        Cliente resultado = clienteService.findBySharedKey("CLI001");

        // Then
        assertNotNull(resultado);
        assertEquals("CLI001", resultado.getSharedKey());
        verify(clientePersistencePort).findBySharedKey("CLI001");
    }

    @Test
    void testFindBySharedKey_NotFound() {
        // Given
        when(clientePersistencePort.findBySharedKey("CLI999")).thenReturn(Optional.empty());

        // When
        Cliente resultado = clienteService.findBySharedKey("CLI999");

        // Then
        assertNull(resultado);
        verify(clientePersistencePort).findBySharedKey("CLI999");
    }

    @Test
    void testFindAll_WithPagination() {
        // Given
        List<Cliente> clientes = Arrays.asList(clienteTest);
        PageResponse<Cliente> pageResponse = PageResponse.<Cliente>builder()
                .content(clientes)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();

        when(clientePersistencePort.findAllClientes(0, 10, null, null)).thenReturn(pageResponse);

        // When
        PageResponse<Cliente> resultado = clienteService.findAllClientes(0, 10, null, null);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(0, resultado.getPageNumber());
        assertEquals(10, resultado.getPageSize());
        assertEquals(1L, resultado.getTotalElements());
        assertTrue(resultado.isLast());

        verify(clientePersistencePort).findAllClientes(0, 10, null, null);
    }

    @Test
    void testFindByFilter() {
        // Given
        List<Cliente> clientes = Arrays.asList(clienteTest);
        PageResponse<Cliente> pageResponse = PageResponse.<Cliente>builder()
                .content(clientes)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();

        when(clientePersistencePort.findClientesByFilter(filtroTest, 0, 10, null, null)).thenReturn(pageResponse);

        // When
        PageResponse<Cliente> resultado = clienteService.findClientesByFilter(filtroTest, 0, 10, null, null);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("Juan Pérez", resultado.getContent().get(0).getNombre());

        verify(clientePersistencePort).findClientesByFilter(filtroTest, 0, 10, null, null);
    }

    @Test
    void testExportToCsv() {
        // Given
        List<Cliente> clientes = Arrays.asList(clienteTest);
        when(clientePersistencePort.findAllClientes()).thenReturn(clientes);

        // When
        List<Cliente> clientesCsv = clienteService.exportClientesToCsv();

        // Then
        assertArrayEquals(clientes.toArray(), clientesCsv.toArray());
        verify(clientePersistencePort).findAllClientes();
    }

    @Test
    void testExportToCsv_EmptyList() {
        // Given
        when(clientePersistencePort.findAllClientes()).thenReturn(Arrays.asList());

        // When
        List<Cliente> csv = clienteService.exportClientesToCsv();

        // Then
        assertNotNull(csv);
        verify(clientePersistencePort).findAllClientes();
    }

    @Test
    void testSaveCliente_SetsCreationDate() {
        // Given
        Cliente clienteSinFecha = Cliente.builder()
                .sharedKey("CLI002")
                .nombre("María García")
                .telefono("+57 300 987 6543")
                .email("maria.garcia@email.com")
                .fechaInicio(LocalDate.of(2024, 2, 1))
                .fechaFin(LocalDate.of(2024, 12, 31))
                .build();

        when(clientePersistencePort.existsBySharedKey("CLI002")).thenReturn(false);
        when(clientePersistencePort.saveCliente(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            return Cliente.builder().id(2L).build();
        });

        // When
        Cliente resultado = clienteService.saveCliente(clienteSinFecha);

        // Then
        assertNotNull(resultado.getFechaCreacion());
        assertEquals(LocalDate.now(), resultado.getFechaCreacion());

        verify(clientePersistencePort).saveCliente(argThat(cliente -> cliente.getFechaCreacion() != null &&
                cliente.getFechaCreacion().equals(LocalDate.now())));
    }
}