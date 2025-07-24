package com.alianza.clientes.service;

import com.alianza.clientes.exception.ResourceNotFoundException;
import com.alianza.clientes.exception.UniqueConstraintViolationException;
import com.alianza.clientes.model.Cliente;
import com.alianza.clientes.model.dto.ClienteDTO;
import com.alianza.clientes.model.dto.ClienteFilterDTO;
import com.alianza.clientes.model.dto.PageResponseDTO;
import com.alianza.clientes.repository.ClienteRepository;
import com.alianza.clientes.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        cliente = Cliente.builder()
                .id(1L)
                .sharedKey("CLI123")
                .nombre("Cliente Prueba")
                .telefono("1234567890")
                .email("cliente@test.com")
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(6))
                .fechaCreacion(LocalDate.now())
                .build();

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
    }

    @Test
    void saveCliente_Success() {
        // Configurar comportamiento del mock
        when(clienteRepository.existsBySharedKey(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Ejecutar método a probar
        ClienteDTO result = clienteService.saveCliente(clienteDTO);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(clienteDTO.getSharedKey(), result.getSharedKey());
        assertEquals(clienteDTO.getNombre(), result.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void saveCliente_DuplicateSharedKey_ThrowsException() {
        // Configurar comportamiento del mock
        when(clienteRepository.existsBySharedKey(anyString())).thenReturn(true);

        // Verificar que se lanza la excepción
        assertThrows(UniqueConstraintViolationException.class, () -> {
            clienteService.saveCliente(clienteDTO);
        });

        // Verificar que no se llamó al método save
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void findBySharedKey_Success() {
        // Configurar comportamiento del mock
        when(clienteRepository.findBySharedKey(anyString())).thenReturn(Optional.of(cliente));

        // Ejecutar método a probar
        ClienteDTO result = clienteService.findBySharedKey("CLI123");

        // Verificar resultados
        assertNotNull(result);
        assertEquals(clienteDTO.getSharedKey(), result.getSharedKey());
        verify(clienteRepository, times(1)).findBySharedKey(anyString());
    }

    @Test
    void findBySharedKey_NotFound_ThrowsException() {
        // Configurar comportamiento del mock
        when(clienteRepository.findBySharedKey(anyString())).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción
        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.findBySharedKey("CLI999");
        });
    }

    @Test
    void findAllClientes_Success() {
        // Configurar datos de prueba
        List<Cliente> clientes = Arrays.asList(cliente);
        Page<Cliente> clientePage = new PageImpl<>(clientes);
        Pageable pageable = PageRequest.of(0, 10);

        // Configurar comportamiento del mock
        when(clienteRepository.findAll(pageable)).thenReturn(clientePage);

        // Ejecutar método a probar
        PageResponseDTO<ClienteDTO> result = clienteService.findAllClientes(pageable);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(clienteDTO.getSharedKey(), result.getContent().get(0).getSharedKey());
        verify(clienteRepository, times(1)).findAll(pageable);
    }

    @Test
    void findClientesByFilter_Success() {
        // Configurar datos de prueba
        List<Cliente> clientes = Arrays.asList(cliente);
        Page<Cliente> clientePage = new PageImpl<>(clientes);
        Pageable pageable = PageRequest.of(0, 10);
        ClienteFilterDTO filter = new ClienteFilterDTO();
        filter.setSharedKey("CLI");

        // Configurar comportamiento del mock
        when(clienteRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(clientePage);

        // Ejecutar método a probar
        PageResponseDTO<ClienteDTO> result = clienteService.findClientesByFilter(filter, pageable);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(clienteDTO.getSharedKey(), result.getContent().get(0).getSharedKey());
        verify(clienteRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void exportClientesToCsv_Success() {
        // Configurar datos de prueba
        List<Cliente> clientes = Arrays.asList(cliente);

        // Configurar comportamiento del mock
        when(clienteRepository.findAll()).thenReturn(clientes);

        // Ejecutar método a probar
        List<ClienteDTO> result = clienteService.exportClientesToCsv();

        // Verificar resultados
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(clienteDTO.getSharedKey(), result.get(0).getSharedKey());
        verify(clienteRepository, times(1)).findAll();
    }
}