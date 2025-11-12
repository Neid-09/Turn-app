# API REST - Módulo de Disponibilidad

## Descripción General

Este módulo gestiona la **disponibilidad preferencial** de los usuarios. Permite configurar los horarios en los que los usuarios prefieren o están disponibles para trabajar, facilitando la asignación inteligente de turnos.

**Base URL**: `/api/disponibilidades`

---

## Endpoints

### 1. Crear Nueva Disponibilidad

Crea una nueva regla de disponibilidad preferencial para un usuario.

**URL**: `POST /api/disponibilidades`

**Request Body**:

```json
{
  "usuarioId": "string (Keycloak ID) - Requerido",
  "diaSemana": "string - Requerido",
  "horaInicio": "string (HH:mm:ss) - Requerido",
  "horaFin": "string (HH:mm:ss) - Requerido",
  "activo": "boolean - Opcional (default: true)"
}
```

**Ejemplo**:

```json
{
  "usuarioId": "abc-123-def-456",
  "diaSemana": "LUNES",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "activo": true
}
```

**Response**: `201 Created`

```json
{
  "id": 1,
  "usuarioId": "abc-123-def-456",
  "diaSemana": "LUNES",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "activo": true,
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

---

### 2. Obtener Disponibilidad por ID

Obtiene los detalles de una disponibilidad específica.

**URL**: `GET /api/disponibilidades/{id}`

**Path Parameters**:

- `id` (Long): ID de la disponibilidad

**Response**: `200 OK`

```json
{
  "id": 1,
  "usuarioId": "abc-123-def-456",
  "diaSemana": "LUNES",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "activo": true,
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

**Errores**:

- `404 Not Found`: Disponibilidad no encontrada

---

### 3. Listar Disponibilidades por Usuario

Obtiene todas las disponibilidades configuradas para un usuario específico.

**URL**: `GET /api/disponibilidades/usuario/{keycloakId}`

**Path Parameters**:

- `keycloakId` (String): Keycloak ID del usuario

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "usuarioId": "abc-123-def-456",
    "diaSemana": "LUNES",
    "horaInicio": "08:00:00",
    "horaFin": "16:00:00",
    "activo": true,
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  },
  {
    "id": 2,
    "usuarioId": "abc-123-def-456",
    "diaSemana": "MARTES",
    "horaInicio": "08:00:00",
    "horaFin": "16:00:00",
    "activo": true,
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  }
]
```

---

### 4. Actualizar Disponibilidad

Actualiza una regla de disponibilidad existente.

**URL**: `PUT /api/disponibilidades/{id}`

**Path Parameters**:

- `id` (Long): ID de la disponibilidad a actualizar

**Request Body**:

```json
{
  "usuarioId": "abc-123-def-456",
  "diaSemana": "LUNES",
  "horaInicio": "09:00:00",
  "horaFin": "17:00:00",
  "activo": true
}
```

**Response**: `200 OK`

```json
{
  "id": 1,
  "usuarioId": "abc-123-def-456",
  "diaSemana": "LUNES",
  "horaInicio": "09:00:00",
  "horaFin": "17:00:00",
  "activo": true,
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T15:30:00"
}
```

---

### 5. Eliminar Disponibilidad

Elimina permanentemente una regla de disponibilidad.

**URL**: `DELETE /api/disponibilidades/{id}`

**Path Parameters**:

- `id` (Long): ID de la disponibilidad a eliminar

**Response**: `204 No Content`

**Nota**: Esta es una **eliminación física** (no soft delete).

---

## Modelos de Datos

### DisponibilidadRequest

```typescript
{
  usuarioId: string;       // Keycloak ID del usuario (requerido)
  diaSemana: DiaSemana;    // Día de la semana (requerido)
  horaInicio: string;      // Hora inicio HH:mm:ss (requerido)
  horaFin: string;         // Hora fin HH:mm:ss (requerido)
  activo?: boolean;        // Estado activo/inactivo (opcional)
}
```

### DisponibilidadResponse

```typescript
{
  id: number;
  usuarioId: string;
  diaSemana: DiaSemana;
  horaInicio: string;      // Formato HH:mm:ss
  horaFin: string;         // Formato HH:mm:ss
  activo: boolean;
  creadoEn: string;        // ISO 8601 DateTime
  actualizadoEn: string;   // ISO 8601 DateTime
}
```

### DiaSemana (Enum)

- `LUNES`
- `MARTES`
- `MIERCOLES`
- `JUEVES`
- `VIERNES`
- `SABADO`
- `DOMINGO`

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Operación exitosa (GET, PUT) |
| `201 Created` | Disponibilidad creada exitosamente |
| `204 No Content` | Disponibilidad eliminada exitosamente |
| `400 Bad Request` | Datos de entrada inválidos |
| `404 Not Found` | Disponibilidad no encontrada |
| `409 Conflict` | Conflicto con disponibilidades existentes |
| `500 Internal Server Error` | Error del servidor |

---

## Validaciones

- ✅ Usuario debe existir en el sistema
- ✅ Día de la semana debe ser válido
- ✅ Hora de inicio debe ser anterior a hora de fin
- ✅ No se permiten solapamientos de disponibilidad para el mismo usuario en el mismo día
- ✅ Formato de hora debe ser HH:mm:ss

---

## Notas Importantes

1. **Disponibilidad vs Asignación**: La disponibilidad es una preferencia, no una restricción absoluta. Los administradores pueden asignar turnos fuera de la disponibilidad declarada

2. **Múltiples Bloques**: Un usuario puede tener varios bloques de disponibilidad en el mismo día si no se solapan

3. **Estado Activo**: Las disponibilidades inactivas no se consideran en las sugerencias de asignación

4. **Actualización**: Al actualizar, todos los campos son requeridos (no es PATCH parcial)

---

## Ejemplos de Uso

### Configurar Disponibilidad Completa de Semana Laboral

```bash
# Lunes a Viernes, 8:00 - 16:00
for day in LUNES MARTES MIERCOLES JUEVES VIERNES; do
  curl -X POST http://localhost:8080/api/disponibilidades \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer YOUR_TOKEN" \
    -d "{
      \"usuarioId\": \"abc-123-def-456\",
      \"diaSemana\": \"$day\",
      \"horaInicio\": \"08:00:00\",
      \"horaFin\": \"16:00:00\",
      \"activo\": true
    }"
done
```

### Configurar Disponibilidad con Doble Turno

```bash
# Turno Mañana
curl -X POST http://localhost:8080/api/disponibilidades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "usuarioId": "abc-123-def-456",
    "diaSemana": "LUNES",
    "horaInicio": "08:00:00",
    "horaFin": "12:00:00",
    "activo": true
  }'

# Turno Tarde
curl -X POST http://localhost:8080/api/disponibilidades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "usuarioId": "abc-123-def-456",
    "diaSemana": "LUNES",
    "horaInicio": "14:00:00",
    "horaFin": "18:00:00",
    "activo": true
  }'
```

### Consultar Disponibilidades

```bash
curl -X GET http://localhost:8080/api/disponibilidades/usuario/abc-123-def-456 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Actualizar Horario

```bash
curl -X PUT http://localhost:8080/api/disponibilidades/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "usuarioId": "abc-123-def-456",
    "diaSemana": "LUNES",
    "horaInicio": "09:00:00",
    "horaFin": "17:00:00",
    "activo": true
  }'
```

### Eliminar Disponibilidad

```bash
curl -X DELETE http://localhost:8080/api/disponibilidades/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Casos de Uso

### Caso 1: Usuario con Disponibilidad Completa (40 horas/semana)

```json
[
  {"diaSemana": "LUNES", "horaInicio": "08:00:00", "horaFin": "16:00:00"},
  {"diaSemana": "MARTES", "horaInicio": "08:00:00", "horaFin": "16:00:00"},
  {"diaSemana": "MIERCOLES", "horaInicio": "08:00:00", "horaFin": "16:00:00"},
  {"diaSemana": "JUEVES", "horaInicio": "08:00:00", "horaFin": "16:00:00"},
  {"diaSemana": "VIERNES", "horaInicio": "08:00:00", "horaFin": "16:00:00"}
]
```

### Caso 2: Usuario Part-Time (Fines de Semana)

```json
[
  {"diaSemana": "SABADO", "horaInicio": "10:00:00", "horaFin": "18:00:00"},
  {"diaSemana": "DOMINGO", "horaInicio": "10:00:00", "horaFin": "18:00:00"}
]
```

### Caso 3: Usuario con Horario Flexible

```json
[
  {"diaSemana": "LUNES", "horaInicio": "06:00:00", "horaFin": "22:00:00"},
  {"diaSemana": "MARTES", "horaInicio": "06:00:00", "horaFin": "22:00:00"},
  {"diaSemana": "MIERCOLES", "horaInicio": "06:00:00", "horaFin": "22:00:00"}
]
```

---

## 8. Manejo de Errores Cuando el Microservicio de Usuarios No Está Disponible

### 8.1. Problema Identificado

Cuando el microservicio de usuarios no está disponible (caído, timeout, error de red), el endpoint de usuarios disponibles fallaba mostrando un error genérico de tipo 500.

### 8.2. Solución Implementada

Se implementó un manejo robusto de errores que distingue entre:

- **404 Not Found**: Usuario específico no existe (error de negocio)
- **503 Service Unavailable**: Microservicio caído o no responde (error de infraestructura)
- **500 Internal Server Error**: Error interno del microservicio
- **401/403**: Errores de autenticación
- **-1**: Errores de conexión (timeout, connection refused)

### 8.3. Componentes Creados

#### 8.3.1. Excepción Personalizada

**Archivo**: `MicroserviceUnavailableException.java`

```java
public class MicroserviceUnavailableException extends RuntimeException {
    private final String microserviceName;
    private final String operation;
    
    public MicroserviceUnavailableException(
        String microserviceName, 
        String operation, 
        String message, 
        Throwable cause
    ) {
        super(String.format("Error al comunicarse con %s durante %s: %s", 
                          microserviceName, operation, message), cause);
        this.microserviceName = microserviceName;
        this.operation = operation;
    }
}
```

#### 8.3.2. Handler Global

**Archivo**: `GlobalExceptionHandler.java`

Se agregó un nuevo handler que captura `MicroserviceUnavailableException` y retorna:

```json
{
  "timestamp": "2025-11-11T15:30:45",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Error al comunicarse con usuarios-microservice durante obtener usuarios...",
  "path": "/api/disponibilidades/usuarios-disponibles"
}
```

#### 8.3.3. Servicio de Validación Mejorado

**Archivo**: `UsuarioValidationServiceImpl.java`

Se agregó el método `determinarMensajeError()` que analiza el código de estado HTTP y retorna mensajes descriptivos:

| Código HTTP | Mensaje al Usuario |
|-------------|-------------------|
| -1 | El servicio de usuarios no está disponible. Por favor, intente nuevamente en unos momentos. |
| 503 | El servicio de usuarios está temporalmente fuera de servicio. Por favor, intente más tarde. |
| 500-599 | El servicio de usuarios está experimentando problemas técnicos. Por favor, contacte al administrador. |
| 401/403 | Error de autenticación con el servicio de usuarios. Por favor, contacte al administrador. |
| Otros | Error al comunicarse con el servicio de usuarios. Por favor, intente nuevamente. |

### 8.4. Ejemplo de Respuesta de Error

#### Cuando el microservicio de usuarios está caído

**Request:**

```http
GET /api/disponibilidades/usuarios-disponibles?fecha=2025-11-27&horaInicio=08:00&horaFin=16:00
Authorization: Bearer {token}
```

**Response (503):**

```json
{
  "timestamp": "2025-11-11T15:30:45.123",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Error al comunicarse con usuarios-microservice durante obtener usuarios: El servicio de usuarios no está disponible. Por favor, intente nuevamente en unos momentos.",
  "path": "/api/disponibilidades/usuarios-disponibles"
}
```

### 8.5. Logs Generados

Cuando ocurre un error de microservicio:

```log
2025-11-11T15:30:45.068-05:00 ERROR [turnos-microservice] 
c.t.m.t.i.u.s.i.UsuarioValidationServiceImpl : 
Error al obtener usuarios. Status: -1 - Mensaje: connect timed out

2025-11-11T15:30:45.125-05:00 ERROR [turnos-microservice]
c.t.m.t.s.e.GlobalExceptionHandler : 
Microservicio no disponible - usuarios-microservice: Error al comunicarse...
```

### 8.6. Ventajas de esta Implementación

✅ **Mensajes claros**: El usuario final recibe mensajes descriptivos según el tipo de error  
✅ **Código HTTP correcto**: Se usa 503 (Service Unavailable) en lugar de 500  
✅ **Trazabilidad**: Logs detallados para depuración  
✅ **Separación de responsabilidades**: Errores de negocio vs errores de infraestructura  
✅ **Mantenibilidad**: Lógica centralizada en el GlobalExceptionHandler  
✅ **Información contextual**: Se registra qué microservicio falló y qué operación se intentaba realizar

### 8.7. Mejoras Futuras Recomendadas

🔄 **Circuit Breaker**: Implementar Resilience4j para evitar llamadas repetidas a servicios caídos  
🔄 **Retry Policy**: Reintentar automáticamente en caso de fallos temporales  
🔄 **Fallback**: Retornar datos en caché cuando el microservicio no esté disponible  
🔄 **Health Checks**: Monitorear el estado de los microservicios proactivamente
