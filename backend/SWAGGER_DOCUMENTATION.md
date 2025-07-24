# Documentación de API con Swagger/OpenAPI

## Descripción General

Este documento describe cómo acceder y utilizar la documentación interactiva de la API del sistema de gestión de clientes, implementada con Swagger/OpenAPI 3.

## Acceso a la Documentación

### URLs de Acceso

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación en las siguientes URLs:

#### Swagger UI (Interfaz Interactiva)

```
http://localhost:8080/alianza/swagger-ui.html
```

#### Documentación JSON de OpenAPI

```
http://localhost:8080/alianza/api-docs
```

#### Documentación YAML de OpenAPI

```
http://localhost:8080/alianza/api-docs.yaml
```

### Configuración de Entornos

#### Desarrollo Local

- **URL Base**: `http://localhost:8080`
- **Contexto**: `/api/v1`
- **Swagger UI**: `http://localhost:8080/alianza/swagger-ui.html`

#### Producción (Ejemplo)

- **URL Base**: `https://api.alianza.com`
- **Contexto**: `/api/v1`
- **Swagger UI**: `https://api.alianza.com/swagger-ui.html`

## Características de la Documentación

### 1. Información de la API

- **Título**: Sistema de Gestión de Clientes - API
- **Versión**: 1.0.0
- **Descripción**: API REST para la gestión completa de clientes con arquitectura hexagonal
- **Contacto**: Equipo de Desarrollo Alianza
- **Licencia**: MIT License

### 2. Endpoints Documentados

#### Gestión de Clientes (`/clientes`)

| Método | Endpoint                | Descripción                          | Autenticación |
| ------ | ----------------------- | ------------------------------------ | ------------- |
| POST   | `/clientes`             | Crear nuevo cliente                  | No requerida  |
| GET    | `/clientes`             | Listar todos los clientes (paginado) | No requerida  |
| GET    | `/clientes/{sharedKey}` | Obtener cliente por shared key       | No requerida  |
| POST   | `/clientes/filter`      | Filtrar clientes por criterios       | No requerida  |
| GET    | `/clientes/export/csv`  | Exportar clientes a CSV              | No requerida  |

### 3. Modelos de Datos

#### ClienteDTO

```json
{
  "id": 1,
  "sharedKey": "jdoe123",
  "nombre": "Juan Pérez",
  "telefono": "+57 300 123 4567",
  "email": "juan.perez@email.com",
  "fechaInicio": "2024-01-15",
  "fechaFin": "2024-12-31",
  "fechaCreacion": "2024-01-01"
}
```

#### ClienteFilterDTO

```json
{
  "sharedKey": "jdoe123",
  "nombre": "Juan",
  "telefono": "3001234567",
  "email": "juan@email.com",
  "fechaInicio": "2024-01-01",
  "fechaFin": "2024-12-31"
}
```

#### PageResponseDTO

```json
{
  "content": [
    /* array de ClienteDTO */
  ],
  "page": 0,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10,
  "first": true,
  "last": false,
  "empty": false
}
```

#### ErrorResponse

```json
{
  "timestamp": "2024-01-15 10:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "El shared key es obligatorio",
  "path": "/clientes"
}
```

#### ValidationErrorResponse

```json
{
  "timestamp": "2024-01-15 10:30:45",
  "status": 400,
  "error": "Validation Failed",
  "message": "Error de validación en los datos enviados",
  "path": "/clientes",
  "fieldErrors": {
    "nombre": "El nombre es obligatorio",
    "email": "El formato del email no es válido"
  }
}
```

## Uso de Swagger UI

### 1. Navegación

- **Tags**: Los endpoints están organizados por funcionalidad
- **Operaciones**: Cada endpoint muestra método HTTP, URL y descripción
- **Modelos**: Sección inferior con esquemas de datos

### 2. Probar Endpoints

#### Pasos para Probar un Endpoint:

1. **Seleccionar Endpoint**: Haz clic en el endpoint que deseas probar
2. **Try it out**: Haz clic en el botón "Try it out"
3. **Completar Parámetros**: Llena los campos requeridos
4. **Execute**: Haz clic en "Execute" para enviar la petición
5. **Ver Respuesta**: Revisa el código de estado y el cuerpo de la respuesta

#### Ejemplo: Crear un Cliente

1. Expandir `POST /clientes`
2. Clic en "Try it out"
3. Completar el JSON en el campo Request body:

```json
{
  "sharedKey": "test123",
  "nombre": "Cliente de Prueba",
  "telefono": "3001234567",
  "email": "test@email.com",
  "fechaInicio": "2024-01-15",
  "fechaFin": "2024-12-31"
}
```

4. Clic en "Execute"
5. Verificar respuesta 201 Created

### 3. Características Interactivas

- **Autocompletado**: Campos con valores de ejemplo
- **Validación**: Validación en tiempo real de esquemas
- **Ejemplos**: Múltiples ejemplos para cada modelo
- **Filtros**: Búsqueda de endpoints por nombre o tag
- **Exportación**: Descarga de especificación OpenAPI

## Configuración Avanzada

### 1. Personalización de Swagger UI

```properties
# application.properties

# Configuración básica
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html

# Configuración de ordenamiento
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha

# Configuración de funcionalidades
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true

# Configuración de grupos
springdoc.group-configs[0].group=clientes-api
springdoc.group-configs[0].paths-to-match=/clientes/**
```

### 2. Configuración de Seguridad (Futuro)

```java
@Configuration
public class SwaggerSecurityConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

## Integración con Herramientas

### 1. Postman

1. Abrir Postman
2. Import > Link
3. Pegar URL: `http://localhost:8080/alianza/api-docs`
4. Importar colección automáticamente

### 2. Insomnia

1. Abrir Insomnia
2. Create > Import from URL
3. Pegar URL: `http://localhost:8080/alianza/api-docs`
4. Importar especificación

### 3. Generación de Clientes

#### OpenAPI Generator

```bash
# Generar cliente JavaScript
openapi-generator-cli generate \
  -i http://localhost:8080/alianza/api-docs \
  -g javascript \
  -o ./client-js

# Generar cliente Java
openapi-generator-cli generate \
  -i http://localhost:8080/alianza/api-docs \
  -g java \
  -o ./client-java
```

## Mejores Prácticas

### 1. Documentación de Endpoints

- **Descripciones Claras**: Cada endpoint tiene descripción detallada
- **Ejemplos Realistas**: Valores de ejemplo representativos
- **Códigos de Estado**: Documentación completa de respuestas
- **Parámetros**: Descripción de todos los parámetros

### 2. Modelos de Datos

- **Esquemas Completos**: Todos los campos documentados
- **Validaciones**: Restricciones claramente especificadas
- **Ejemplos**: Valores de ejemplo para cada campo
- **Formatos**: Especificación de formatos de fecha, email, etc.

### 3. Manejo de Errores

- **Respuestas de Error**: Documentación de todos los errores posibles
- **Códigos HTTP**: Uso correcto de códigos de estado
- **Mensajes**: Mensajes de error descriptivos

## Troubleshooting

### Problemas Comunes

#### 1. Swagger UI No Carga

```bash
# Verificar que la aplicación esté ejecutándose
curl http://localhost:8080/actuator/health

# Verificar configuración
grep -r "springdoc" src/main/resources/
```

#### 2. Endpoints No Aparecen

- Verificar anotaciones `@RestController`
- Confirmar que los controladores estén en el package escaneado
- Revisar configuración de `@ComponentScan`

#### 3. Modelos No Se Muestran

- Verificar anotaciones `@Schema`
- Confirmar que las clases DTO estén en el classpath
- Revisar imports de OpenAPI

### Logs de Depuración

```properties
# Habilitar logs de SpringDoc
logging.level.org.springdoc=DEBUG
logging.level.io.swagger=DEBUG
```

## Versionado de API

### Estrategia de Versionado

```java
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteRestAdapter {
    // Implementación
}
```

### Documentación de Versiones

- **v1.0.0**: Versión inicial con CRUD básico
- **v1.1.0**: Agregado filtrado avanzado
- **v1.2.0**: Agregada exportación CSV

## Recursos Adicionales

### Enlaces Útiles

- [OpenAPI Specification](https://swagger.io/specification/)
- [SpringDoc Documentation](https://springdoc.org/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Generator](https://openapi-generator.tech/)

### Herramientas Recomendadas

- **Swagger Editor**: Editor online para especificaciones OpenAPI
- **Swagger Codegen**: Generación de código cliente/servidor
- **Postman**: Testing de APIs
- **Insomnia**: Cliente REST alternativo

---

**Nota**: Esta documentación se actualiza automáticamente con cada cambio en la API. Para reportar problemas o sugerir mejoras, contacta al equipo de desarrollo.
