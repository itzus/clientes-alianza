# Sistema de Gestión de Clientes - Alianza

Este proyecto implementa un sistema completo de gestión de clientes utilizando **Arquitectura Hexagonal** con backend en Spring Boot y frontend en Angular.

## Arquitectura

El proyecto sigue los principios de **Arquitectura Hexagonal (Ports & Adapters)** que proporciona:

- **Separación clara de responsabilidades**
- **Independencia de frameworks y tecnologías**
- **Facilidad para testing**
- **Mantenibilidad y escalabilidad**

### Estructura Hexagonal del Backend

```
backend/src/main/java/com/alianza/clientes/
├── domain/                           # Núcleo del negocio
│   ├── model/                       # Entidades de dominio
│   │   ├── Cliente.java
│   │   ├── ClienteFilter.java
│   │   └── PageResponse.java
│   └── port/                        # Contratos/Interfaces
│       ├── api/                     # Puertos de entrada
│       │   └── ClienteServicePort.java
│       └── spi/                     # Puertos de salida
│           └── ClientePersistencePort.java
├── application/                      # Casos de uso
│   └── service/
│       └── ClienteService.java      # Implementación de lógica de negocio
└── infrastructure/                   # Adaptadores
    └── adapter/
        ├── persistence/             # Adaptador de persistencia
        │   ├── entity/
        │   │   └── ClienteEntity.java
        │   ├── repository/
        │   │   ├── ClienteJpaRepository.java
        │   │   └── ClienteSpecification.java
        │   └── ClientePersistenceAdapter.java
        └── rest/                    # Adaptador REST
            ├── dto/
            │   ├── ClienteDTO.java
            │   ├── ClienteFilterDTO.java
            │   └── PageResponseDTO.java
            ├── exception/
            │   ├── ErrorResponse.java
            │   ├── ValidationErrorResponse.java
            │   └── GlobalExceptionHandler.java
            └── ClienteRestAdapter.java
```

## Estructura del Proyecto

```
clientes-alianza/
├── backend/          # API REST con Spring Boot (Arquitectura Hexagonal)
└── frontend/         # Aplicación web con Angular
```

## Requisitos Previos

- Java 17 o superior
- Node.js 18 o superior
- npm 9 o superior
- PostgreSQL 12 o superior

## Configuración del Backend

1. Asegúrate de tener PostgreSQL instalado y en ejecución.
2. Crea una base de datos llamada `clientes_alianza`.
3. Configura las credenciales de la base de datos en el archivo `backend/src/main/resources/application.properties`.

## Ejecución del Backend

```bash
cd backend
./gradlew bootRun
```

El backend estará disponible en `http://localhost:8080/api`.

## Configuración del Frontend

```bash
cd frontend
npm install
```

## Ejecución del Frontend

```bash
cd frontend
npm start
```

El frontend estará disponible en `http://localhost:4200`.

## Documentación de la API

La documentación de la API está disponible en `http://localhost:8080/api/swagger-ui.html` una vez que el backend esté en ejecución.

## Funcionalidades

- Creación de clientes
- Búsqueda de clientes por diferentes criterios
- Exportación de clientes a CSV
- Validación de datos
- Paginación de resultados

## Tecnologías Utilizadas

### Backend
- **Spring Boot 3.2.0**: Framework principal
- **Spring Data JPA**: Persistencia de datos
- **PostgreSQL**: Base de datos
- **Swagger/OpenAPI**: Documentación de API
- **Lombok**: Reducción de código boilerplate
- **Gradle**: Gestión de dependencias
- **Arquitectura Hexagonal**: Patrón arquitectónico
- **JUnit y Mockito**: Para pruebas

### Frontend
- **Angular 17**: Framework de frontend
- **Angular Material**: Componentes UI
- **TypeScript**: Lenguaje de programación
- **RxJS**: Programación reactiva
- **npm**: Gestión de dependencias