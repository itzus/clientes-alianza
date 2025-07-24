# Guía de Pruebas Unitarias - Sistema de Gestión de Clientes

## Descripción General

Este documento describe la estrategia de pruebas unitarias implementada para el sistema de gestión de clientes, siguiendo la arquitectura hexagonal (Ports & Adapters).

## Estructura de Pruebas

### 1. Pruebas de Dominio

#### `ClienteTest.java`
- **Ubicación**: `src/test/java/com/alianza/clientes/domain/model/`
- **Propósito**: Validar la lógica del modelo de dominio `Cliente`
- **Cobertura**:
  - Creación de objetos Cliente
  - Validación de métodos equals() y hashCode()
  - Verificación del método toString()
  - Comportamiento de Builder pattern

### 2. Pruebas de Aplicación

#### `ClienteServiceTest.java`
- **Ubicación**: `src/test/java/com/alianza/clientes/application/service/`
- **Propósito**: Validar la lógica de negocio en el servicio de aplicación
- **Cobertura**:
  - Guardar cliente (éxito y shared key duplicado)
  - Buscar cliente por shared key (encontrado y no encontrado)
  - Listar todos los clientes con paginación
  - Filtrar clientes por criterios
  - Exportar clientes a CSV
  - Asignación automática de fecha de creación

### 3. Pruebas de Infraestructura

#### `ClientePersistenceAdapterTest.java`
- **Ubicación**: `src/test/java/com/alianza/clientes/infrastructure/adapter/persistence/`
- **Propósito**: Validar la persistencia de datos
- **Cobertura**:
  - Operaciones CRUD básicas
  - Búsquedas por shared key
  - Verificación de existencia
  - Paginación
  - Filtrado de datos
  - Conversiones entre entidades y objetos de dominio

#### `ClienteRestAdapterTest.java`
- **Ubicación**: `src/test/java/com/alianza/clientes/infrastructure/adapter/rest/`
- **Propósito**: Validar los endpoints REST
- **Cobertura**:
  - Creación de clientes (éxito y errores de validación)
  - Búsqueda por shared key
  - Listado con paginación
  - Filtrado de clientes
  - Exportación a CSV
  - Manejo de errores HTTP

### 4. Pruebas de Integración

#### `ClienteIntegrationTest.java`
- **Ubicación**: `src/test/java/com/alianza/clientes/integration/`
- **Propósito**: Validar el flujo completo de la aplicación
- **Cobertura**:
  - Flujo completo de gestión de clientes
  - Integración entre capas
  - Validación de datos end-to-end
  - Escenarios de error complejos

## Tecnologías Utilizadas

### Frameworks de Testing
- **JUnit 5**: Framework principal de pruebas
- **Mockito**: Mocking y stubbing
- **Spring Boot Test**: Integración con Spring
- **TestContainers**: Pruebas con base de datos real
- **H2 Database**: Base de datos en memoria para pruebas

### Anotaciones Principales
- `@ExtendWith(MockitoExtension.class)`: Para pruebas unitarias con Mockito
- `@SpringBootTest`: Para pruebas de integración
- `@WebMvcTest`: Para pruebas de controladores
- `@DataJpaTest`: Para pruebas de repositorios
- `@Mock`: Para crear mocks
- `@InjectMocks`: Para inyectar mocks

## Ejecución de Pruebas

### Ejecutar Todas las Pruebas
```bash
# Con Gradle
./gradlew test

# Con Maven (si se usa)
mvn test
```

### Ejecutar Pruebas por Categoría
```bash
# Solo pruebas unitarias
./gradlew test --tests "*Test"

# Solo pruebas de integración
./gradlew test --tests "*IntegrationTest"

# Pruebas de una clase específica
./gradlew test --tests "ClienteServiceTest"
```

### Generar Reporte de Cobertura
```bash
./gradlew jacocoTestReport
```

## Patrones de Testing Implementados

### 1. Arrange-Act-Assert (AAA)
Todas las pruebas siguen el patrón AAA:
```java
@Test
void shouldCreateClienteSuccessfully() {
    // Arrange
    ClienteDTO clienteDTO = createValidClienteDTO();
    Cliente expectedCliente = createValidCliente();
    
    // Act
    Cliente result = clienteService.save(expectedCliente);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
}
```

### 2. Test Data Builders
Uso de builders para crear datos de prueba:
```java
private ClienteDTO createValidClienteDTO() {
    return ClienteDTO.builder()
        .sharedKey("test123")
        .nombre("Test Cliente")
        .email("test@email.com")
        .build();
}
```

### 3. Mocking Estratégico
- Mock de dependencias externas
- Stub de comportamientos específicos
- Verificación de interacciones

## Cobertura de Pruebas

### Objetivos de Cobertura
- **Líneas de código**: > 80%
- **Ramas**: > 75%
- **Métodos**: > 90%

### Áreas Críticas
- Lógica de negocio (100%)
- Validaciones (100%)
- Conversiones de datos (95%)
- Manejo de errores (90%)

## Mejores Prácticas Implementadas

### 1. Nomenclatura de Pruebas
```java
// Patrón: should[ExpectedBehavior]When[StateUnderTest]
@Test
void shouldReturnClienteWhenValidSharedKeyProvided() { }

@Test
void shouldThrowExceptionWhenSharedKeyAlreadyExists() { }
```

### 2. Aislamiento de Pruebas
- Cada prueba es independiente
- No hay dependencias entre pruebas
- Setup y teardown apropiados

### 3. Datos de Prueba
- Uso de datos realistas pero no sensibles
- Builders para crear objetos complejos
- Constantes para valores reutilizables

### 4. Aserciones Expresivas
```java
// Uso de AssertJ para aserciones más legibles
assertThat(clientes)
    .hasSize(2)
    .extracting(Cliente::getNombre)
    .containsExactly("Cliente 1", "Cliente 2");
```

## Configuración de CI/CD

### Pipeline de Pruebas
1. **Pruebas Unitarias**: Ejecución rápida en cada commit
2. **Pruebas de Integración**: Ejecución en pull requests
3. **Reporte de Cobertura**: Generación automática
4. **Quality Gates**: Bloqueo si cobertura < 80%

### Configuración de Base de Datos para Pruebas
```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.flyway.enabled=false
```

## Troubleshooting

### Problemas Comunes

1. **Pruebas Lentas**
   - Verificar uso excesivo de `@SpringBootTest`
   - Preferir `@WebMvcTest` o `@DataJpaTest`

2. **Fallos Intermitentes**
   - Revisar dependencias entre pruebas
   - Verificar limpieza de datos

3. **Mocks No Funcionan**
   - Verificar anotaciones `@Mock` y `@InjectMocks`
   - Confirmar configuración de MockitoExtension

## Próximos Pasos

### Mejoras Planificadas
1. **Pruebas de Performance**: JMeter o Gatling
2. **Pruebas de Contrato**: Spring Cloud Contract
3. **Pruebas de Mutación**: PIT testing
4. **Pruebas de Arquitectura**: ArchUnit

### Métricas a Implementar
- Tiempo de ejecución de pruebas
- Cobertura por módulo
- Tendencias de calidad
- Detección de código muerto

---

**Nota**: Este documento debe actualizarse conforme evolucione la estrategia de pruebas del proyecto.