package com.alianza.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Clase principal de la aplicación Spring Boot para la gestión de clientes.
 * Implementa arquitectura hexagonal con separación clara de responsabilidades:
 * - Dominio: Contiene la lógica de negocio pura
 * - Aplicación: Orquesta las operaciones de negocio
 * - Infraestructura: Maneja persistencia, API REST y configuración
 */
@SpringBootApplication
@EnableTransactionManagement
public class ClientesAlianzaApplication {

	/**
	 * Método principal que inicia la aplicación Spring Boot
	 * 
	 * @param args Argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(ClientesAlianzaApplication.class, args);
	}
}