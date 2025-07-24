package com.alianza.clientes.infrastructure.adapter.persistence;

import com.alianza.clientes.domain.model.Cliente;
import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.domain.model.PageResponse;
import com.alianza.clientes.infrastructure.adapter.persistence.entity.ClienteEntity;
import com.alianza.clientes.infrastructure.adapter.persistence.repository.ClienteJpaRepository;
import com.alianza.clientes.infrastructure.adapter.persistence.repository.ClienteSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClientePersistenceAdapter
 */
@ExtendWith(MockitoExtension.class)
class ClientePersistenceAdapterTest {

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    @InjectMocks
    private ClientePersistenceAdapter clientePersistenceAdapter;

    private Cliente clienteDominio;
    private ClienteEntity clienteEntity;
    private ClienteFilter filtro;

    @BeforeEach
    void setUp() {
        clienteDominio = Cliente.builder()
                .id(1L)
                .sharedKey("CLI001")
                .nombre("Juan Pérez")
                .telefono("+57 300 123 4567")
                .email("juan.perez@email.com")
                .fechaInicio(LocalDate.of(2024, 1, 15))
                .fechaFin(LocalDate.of(2024, 12, 31))
                .fechaCreacion(LocalDate.now())
                .build();

        clienteEntity = new ClienteEntity();
        clienteEntity.setId(1L);
        clienteEntity.setSharedKey("CLI001");
        clienteEntity.setNombre("Juan Pérez");
        clienteEntity.setTelefono("+57 300 123 4567");
        clienteEntity.setEmail("juan.perez@email.com");
        clienteEntity.setFechaInicio(LocalDate.of(2024, 1, 15));
        clienteEntity.setFechaFin(LocalDate.of(2024, 12, 31));
        clienteEntity.setFechaCreacion(LocalDate.now());

        filtro = ClienteFilter.builder()
                .nombre("Juan")
                .email("juan")
                .build();
    }

    @Test
    void testSave() {
        // Given
        when(clienteJpaRepository.save(any(ClienteEntity.class))).thenReturn(clienteEntity);

        // When
        Cliente resultado = clientePersistenceAdapter.saveCliente(clienteDominio);

        // Then
        assertNotNull(resultado);
        assertEquals("CLI001", resultado.getSharedKey());
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("+57 300 123 4567", resultado.getTelefono());
        assertEquals("juan.perez@email.com", resultado.getEmail());

        verify(clienteJpaRepository).save(any(ClienteEntity.class));
    }

    @Test
    void testFindBySharedKey_Found() {
        // Given
        when(clienteJpaRepository.findBySharedKey("CLI001")).thenReturn(Optional.of(clienteEntity));

        // When
        Optional<Cliente> resultado = clientePersistenceAdapter.findBySharedKey("CLI001");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("CLI001", resultado.get().getSharedKey());
        assertEquals("Juan Pérez", resultado.get().getNombre());

        verify(clienteJpaRepository).findBySharedKey("CLI001");
    }

    @Test
    void testFindBySharedKey_NotFound() {
        // Given
        when(clienteJpaRepository.findBySharedKey("CLI999")).thenReturn(Optional.empty());

        // When
        Optional<Cliente> resultado = clientePersistenceAdapter.findBySharedKey("CLI999");

        // Then
        assertFalse(resultado.isPresent());

        verify(clienteJpaRepository).findBySharedKey("CLI999");
    }

    @Test
    void testExistsBySharedKey_True() {
        // Given
        when(clienteJpaRepository.existsBySharedKey("CLI001")).thenReturn(true);

        // When
        boolean resultado = clientePersistenceAdapter.existsBySharedKey("CLI001");

        // Then
        assertTrue(resultado);

        verify(clienteJpaRepository).existsBySharedKey("CLI001");
    }

    @Test
    void testExistsBySharedKey_False() {
        // Given
        when(clienteJpaRepository.existsBySharedKey("CLI999")).thenReturn(false);

        // When
        boolean resultado = clientePersistenceAdapter.existsBySharedKey("CLI999");

        // Then
        assertFalse(resultado);

        verify(clienteJpaRepository).existsBySharedKey("CLI999");
    }

    @Test
    void testFindAll_WithoutPagination() {
        // Given
        List<ClienteEntity> entities = Arrays.asList(clienteEntity);
        when(clienteJpaRepository.findAll()).thenReturn(entities);

        // When
        List<Cliente> resultado = clientePersistenceAdapter.findAllClientes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("CLI001", resultado.get(0).getSharedKey());

        verify(clienteJpaRepository).findAll();
    }

    @Test
    void testFindAll_WithPagination() {
        // Given
        List<ClienteEntity> entities = Arrays.asList(clienteEntity);
        Page<ClienteEntity> page = new PageImpl<>(entities, PageRequest.of(0, 10), 1);
        when(clienteJpaRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        PageResponse<Cliente> resultado = clientePersistenceAdapter.findAllClientes(0, 10, null, null);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(0, resultado.getPageNumber());
        assertEquals(10, resultado.getPageSize());
        assertEquals(1L, resultado.getTotalElements());
        assertEquals(1, resultado.getTotalPages());
        assertTrue(resultado.isLast());

        verify(clienteJpaRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindByFilter() {
        // Given
        List<ClienteEntity> entities = Arrays.asList(clienteEntity);
        Page<ClienteEntity> page = new PageImpl<>(entities, PageRequest.of(0, 10), 1);

        try (MockedStatic<ClienteSpecification> mockedStatic = mockStatic(ClienteSpecification.class)) {
            Specification<ClienteEntity> mockSpec = mock(Specification.class);
            mockedStatic.when(() -> ClienteSpecification.buildSpecification(filtro))
                    .thenReturn(mockSpec);

            when(clienteJpaRepository.findAll(eq(mockSpec), any(Pageable.class))).thenReturn(page);

            // When
            PageResponse<Cliente> resultado = clientePersistenceAdapter.findClientesByFilter(filtro, 0, 10, null, null);

            // Then
            assertNotNull(resultado);
            assertEquals(1, resultado.getContent().size());
            assertEquals("CLI001", resultado.getContent().get(0).getSharedKey());

            verify(clienteJpaRepository).findAll(eq(mockSpec), any(Pageable.class));
        }
    }

}