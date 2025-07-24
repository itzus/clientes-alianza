package com.alianza.clientes.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Indica a Spring que esta es una clase de configuración
public class CorsConfig {

    @Bean // Define un bean que Spring gestionará
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("*/alianza/**") // Aplica CORS a todas las rutas bajo /api/
                        .allowedOrigins("http://localhost:4200", "*") // Orígenes permitidos (URL
                                                                      // de tu frontend)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*") // Todas las cabeceras permitidas en la petición
                        .allowCredentials(true) // Permite el envío de cookies, cabeceras de autorización, etc.
                        .maxAge(3600); // Tiempo en segundos que la pre-flight request (OPTIONS) puede ser cacheada
            }
        };
    }
}