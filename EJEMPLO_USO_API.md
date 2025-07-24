# Ejemplos de Uso de la API - Sistema de Gestión de Clientes

## Introducción

Este documento proporciona ejemplos prácticos de cómo utilizar la API REST del sistema de gestión de clientes implementado con arquitectura hexagonal.

## Base URL

```
http://localhost:8080/api
```

## Endpoints Disponibles

### 1. Crear Cliente

**POST** `/clientes`

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "sharedKey": "CLI001",
    "nombre": "Juan Pérez",
    "telefono": "+57 300 123 4567",
    "email": "juan.perez@email.com",
    "fechaInicio": "2024-01-15",
    "fechaFin": "2024-12-31"
  }'
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "sharedKey": "CLI001",
  "nombre": "Juan Pérez",
  "telefono": "+57 300 123 4567",
  "email": "juan.perez@email.com",
  "fechaInicio": "2024-01-15",
  "fechaFin": "2024-12-31",
  "fechaCreacion": "2024-01-10"
}
```

### 2. Obtener Cliente por Shared Key

**GET** `/clientes/{sharedKey}`

```bash
curl -X GET http://localhost:8080/api/clientes/CLI001
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "sharedKey": "CLI001",
  "nombre": "Juan Pérez",
  "telefono": "+57 300 123 4567",
  "email": "juan.perez@email.com",
  "fechaInicio": "2024-01-15",
  "fechaFin": "2024-12-31",
  "fechaCreacion": "2024-01-10"
}
```

### 3. Listar Todos los Clientes (Paginado)

**GET** `/clientes?page=0&size=10`

```bash
curl -X GET "http://localhost:8080/api/clientes?page=0&size=10"
```

**Respuesta exitosa (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "sharedKey": "CLI001",
      "nombre": "Juan Pérez",
      "telefono": "+57 300 123 4567",
      "email": "juan.perez@email.com",
      "fechaInicio": "2024-01-15",
      "fechaFin": "2024-12-31",
      "fechaCreacion": "2024-01-10"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### 4. Filtrar Clientes

**GET** `/clientes/filter`

#### Filtrar por nombre:
```bash
curl -X GET "http://localhost:8080/api/clientes/filter?nombre=Juan&page=0&size=10"
```

#### Filtrar por email:
```bash
curl -X GET "http://localhost:8080/api/clientes/filter?email=juan.perez@email.com&page=0&size=10"
```

#### Filtrar por rango de fechas:
```bash
curl -X GET "http://localhost:8080/api/clientes/filter?fechaInicio=2024-01-01&fechaFin=2024-12-31&page=0&size=10"
```

#### Filtros combinados:
```bash
curl -X GET "http://localhost:8080/api/clientes/filter?nombre=Juan&telefono=300&page=0&size=10"
```

**Respuesta exitosa (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "sharedKey": "CLI001",
      "nombre": "Juan Pérez",
      "telefono": "+57 300 123 4567",
      "email": "juan.perez@email.com",
      "fechaInicio": "2024-01-15",
      "fechaFin": "2024-12-31",
      "fechaCreacion": "2024-01-10"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### 5. Exportar Clientes a CSV

**GET** `/clientes/export/csv`

```bash
curl -X GET http://localhost:8080/api/clientes/export/csv \
  -H "Accept: text/csv" \
  -o clientes.csv
```

**Respuesta exitosa (200 OK):**
```csv
Shared Key,Nombre,Teléfono,Email,Fecha Inicio,Fecha Fin,Fecha Creación
CLI001,Juan Pérez,+57 300 123 4567,juan.perez@email.com,2024-01-15,2024-12-31,2024-01-10
```

## Manejo de Errores

### Error de Validación (400 Bad Request)

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "sharedKey": "",
    "nombre": "",
    "email": "email-invalido"
  }'
```

**Respuesta de error:**
```json
{
  "timestamp": "2024-01-10T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Error de validación en los datos enviados",
  "path": "/api/clientes",
  "fieldErrors": {
    "sharedKey": "El shared key es obligatorio",
    "nombre": "El nombre es obligatorio",
    "email": "El formato del email no es válido"
  }
}
```

### Cliente No Encontrado (404 Not Found)

```bash
curl -X GET http://localhost:8080/api/clientes/CLI999
```

**Respuesta de error:**
```json
{
  "timestamp": "2024-01-10T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente no encontrado con shared key: CLI999",
  "path": "/api/clientes/CLI999"
}
```

### Shared Key Duplicado (400 Bad Request)

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "sharedKey": "CLI001",
    "nombre": "María García",
    "telefono": "+57 300 987 6543",
    "email": "maria.garcia@email.com",
    "fechaInicio": "2024-02-01",
    "fechaFin": "2024-12-31"
  }'
```

**Respuesta de error:**
```json
{
  "timestamp": "2024-01-10T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Ya existe un cliente con el shared key: CLI001",
  "path": "/api/clientes"
}
```

## Ejemplos con JavaScript (Frontend)

### Crear Cliente

```javascript
const crearCliente = async (clienteData) => {
  try {
    const response = await fetch('http://localhost:8080/api/clientes', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(clienteData)
    });
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status}`);
    }
    
    const cliente = await response.json();
    console.log('Cliente creado:', cliente);
    return cliente;
  } catch (error) {
    console.error('Error al crear cliente:', error);
    throw error;
  }
};

// Uso
crearCliente({
  sharedKey: 'CLI002',
  nombre: 'María García',
  telefono: '+57 300 987 6543',
  email: 'maria.garcia@email.com',
  fechaInicio: '2024-02-01',
  fechaFin: '2024-12-31'
});
```

### Obtener Clientes con Filtros

```javascript
const obtenerClientesFiltrados = async (filtros, page = 0, size = 10) => {
  try {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...filtros
    });
    
    const response = await fetch(`http://localhost:8080/api/clientes/filter?${params}`);
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status}`);
    }
    
    const resultado = await response.json();
    console.log('Clientes encontrados:', resultado);
    return resultado;
  } catch (error) {
    console.error('Error al obtener clientes:', error);
    throw error;
  }
};

// Uso
obtenerClientesFiltrados({
  nombre: 'Juan',
  email: 'juan'
}, 0, 5);
```

## Documentación Swagger

La documentación interactiva de la API está disponible en:

```
http://localhost:8080/api/swagger-ui/index.html
```

Esta interfaz permite:
- Explorar todos los endpoints disponibles
- Probar las APIs directamente desde el navegador
- Ver los esquemas de datos de entrada y salida
- Entender los códigos de respuesta posibles

## Consideraciones de Rendimiento

### Paginación
- Siempre usar paginación para listas grandes
- Tamaño de página recomendado: 10-50 elementos
- Máximo permitido: 100 elementos por página

### Filtros
- Los filtros son case-insensitive
- Se pueden combinar múltiples filtros
- Los filtros de fecha son inclusivos

### Cache
- Las consultas frecuentes pueden ser cacheadas
- El cache se invalida automáticamente al crear/actualizar clientes

## Próximos Endpoints

En futuras versiones se planea agregar:
- `PUT /clientes/{sharedKey}` - Actualizar cliente
- `DELETE /clientes/{sharedKey}` - Eliminar cliente
- `GET /clientes/stats` - Estadísticas de clientes
- `POST /clientes/bulk` - Creación masiva de clientes