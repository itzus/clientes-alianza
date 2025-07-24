package com.alianza.clientes.infrastructure.config;

import com.alianza.clientes.application.service.ClienteService;
import com.alianza.clientes.domain.port.api.ClienteServicePort;
import com.alianza.clientes.domain.port.spi.ClientePersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring para la arquitectura hexagonal.
 * Define los beans necesarios para conectar los puertos y adaptadores.
 */
@Configuration
public class HexagonalConfig {

    /**
     * Configura el servicio de aplicación que implementa el puerto de entrada
     * 
     * @param clientePersistencePort Puerto de persistencia implementado por el adaptador
     * @return Implementación del puerto de entrada
     */
    @Bean
    public ClienteServicePort clienteServicePort(ClientePersistencePort clientePersistencePort) {
        return new ClienteService(clientePersistencePort);
    }
}