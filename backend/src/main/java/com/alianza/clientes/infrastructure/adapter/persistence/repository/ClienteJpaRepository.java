package com.alianza.clientes.infrastructure.adapter.persistence.repository;

import com.alianza.clientes.infrastructure.adapter.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad ClienteEntity.
 * Extiende JpaRepository para operaciones CRUD básicas y
 * JpaSpecificationExecutor
 * para consultas dinámicas con especificaciones.
 */
@Repository
public interface ClienteJpaRepository
        extends JpaRepository<ClienteEntity, Long>, JpaSpecificationExecutor<ClienteEntity> {

    /**
     * Busca un cliente por su sharedKey
     * 
     * @param sharedKey La clave compartida única del cliente
     * @return Optional con el cliente si existe, vacío si no
     */
    Optional<ClienteEntity> findBySharedKey(String sharedKey);

    /**
     * Verifica si existe un cliente con el sharedKey proporcionado
     * 
     * @param sharedKey La clave compartida única a verificar
     * @return true si existe, false si no
     */
    boolean existsBySharedKey(String sharedKey);

    /**
     * Busca el último cliente registrado por ID
     * 
     * @return Optional con el cliente si existe, vacío si no
     */
    Optional<ClienteEntity> findTopByOrderByIdDesc();

}