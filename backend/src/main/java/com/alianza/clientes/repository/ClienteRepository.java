package com.alianza.clientes.repository;

import com.alianza.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    
    Optional<Cliente> findBySharedKey(String sharedKey);
    
    boolean existsBySharedKey(String sharedKey);
}