# Arquitectura Hexagonal - Sistema de Gestión de Clientes

## Introducción

Este proyecto implementa **Arquitectura Hexagonal** (también conocida como Ports & Adapters), un patrón arquitectónico que promueve la separación de responsabilidades y la independencia de frameworks externos.

## Principios de la Arquitectura Hexagonal

### 1. Separación de Capas
- **Dominio**: Contiene la lógica de negocio pura, sin dependencias externas
- **Aplicación**: Orquesta los casos de uso del negocio
- **Infraestructura**: Maneja la comunicación con el mundo exterior

### 2. Inversión de Dependencias
- El dominio define interfaces (puertos) que la infraestructura implementa
- Las dependencias apuntan hacia adentro, hacia el dominio
- Facilita el testing y el cambio de tecnologías

### 3. Puertos y Adaptadores
- **Puertos**: Interfaces que definen contratos
- **Adaptadores**: Implementaciones concretas de los puertos

## Estructura del Proyecto

### Capa de Dominio (`domain/`)

#### Modelos (`domain/model/`)
- **`Cliente.java`**: Entidad de dominio principal
- **`ClienteFilter.java`**: Objeto de valor para filtros
- **`PageResponse.java`**: Objeto de valor para respuestas paginadas

#### Puertos de Entrada (`domain/port/api/`)
- **`ClienteServicePort.java`**: Define las operaciones de negocio disponibles

#### Puertos de Salida (`domain/port/spi/`)
- **`ClientePersistencePort.java`**: Define las operaciones de persistencia requeridas

### Capa de Aplicación (`application/`)

#### Servicios (`application/service/`)
- **`ClienteService.java`**: Implementa la lógica de negocio y orquesta las operaciones

### Capa de Infraestructura (`infrastructure/`)

#### Adaptador de Persistencia (`infrastructure/adapter/persistence/`)
- **`ClientePersistenceAdapter.java`**: Implementa `ClientePersistencePort`
- **`ClienteEntity.java`**: Entidad JPA para la base de datos
- **`ClienteJpaRepository.java`**: Repositorio JPA
- **`ClienteSpecification.java`**: Especificaciones para consultas dinámicas

#### Adaptador REST (`infrastructure/adapter/rest/`)
- **`ClienteRestAdapter.java`**: Controlador REST que expone la API
- **DTOs**: Objetos de transferencia de datos para la API
- **Exception Handling**: Manejo global de excepciones

#### Configuración (`infrastructure/config/`)
- **`HexagonalConfig.java`**: Configuración de Spring para conectar las capas

## Flujo de Datos

```
Cliente HTTP → RestAdapter → ServicePort → Service → PersistencePort → PersistenceAdapter → Base de Datos
```

### Ejemplo de Flujo:
1. **Cliente HTTP** envía petición a `/api/clientes`
2. **`ClienteRestAdapter`** recibe la petición y convierte DTOs a objetos de dominio
3. **`ClienteServicePort`** define la operación a realizar
4. **`ClienteService`** implementa la lógica de negocio
5. **`ClientePersistencePort`** define las operaciones de persistencia necesarias
6. **`ClientePersistenceAdapter`** implementa la persistencia usando JPA
7. Los datos fluyen de vuelta por el mismo camino

## Beneficios de esta Arquitectura

### 1. Testabilidad
- Fácil creación de mocks para puertos
- Testing independiente de frameworks
- Pruebas unitarias rápidas del dominio

### 2. Mantenibilidad
- Separación clara de responsabilidades
- Cambios en infraestructura no afectan el dominio
- Código más legible y organizado

### 3. Flexibilidad
- Fácil cambio de base de datos
- Posibilidad de múltiples adaptadores (REST, GraphQL, etc.)
- Independencia de frameworks

### 4. Escalabilidad
- Estructura preparada para microservicios
- Fácil adición de nuevas funcionalidades
- Separación de concerns

## Patrones Implementados

### 1. Dependency Inversion Principle (DIP)
- Las capas externas dependen de las internas
- El dominio no conoce la infraestructura

### 2. Repository Pattern
- Abstracción de la persistencia de datos
- Implementado a través de `ClientePersistencePort`

### 3. Specification Pattern
- Para consultas dinámicas y complejas
- Implementado en `ClienteSpecification`

### 4. DTO Pattern
- Separación entre objetos de dominio y transferencia
- Implementado en el adaptador REST

## Consideraciones de Implementación

### Mapeo entre Capas
- Cada adaptador es responsable de mapear entre sus objetos y los del dominio
- No hay dependencias directas entre DTOs y entidades de dominio

### Manejo de Errores
- Excepciones de dominio son capturadas y transformadas por los adaptadores
- Manejo global de excepciones en el adaptador REST

### Configuración
- Spring Boot se usa solo en la capa de infraestructura
- El dominio permanece libre de anotaciones de framework

## Próximos Pasos

1. **Testing**: Implementar pruebas unitarias para cada capa
2. **Documentación API**: Completar la documentación Swagger
3. **Métricas**: Añadir observabilidad y métricas
4. **Seguridad**: Implementar autenticación y autorización
5. **Cache**: Añadir estrategias de cache en el adaptador de persistencia

## Conclusión

La arquitectura hexagonal implementada proporciona una base sólida y mantenible para el sistema de gestión de clientes, facilitando el desarrollo, testing y evolución futura del sistema.