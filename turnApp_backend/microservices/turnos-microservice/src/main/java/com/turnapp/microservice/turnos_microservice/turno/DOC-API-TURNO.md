# API REST - Módulo de Turnos (Plantillas)

## Descripción General

Este módulo gestiona las **plantillas de turnos** del sistema. Define los tipos de turnos disponibles con sus horarios y duraciones, que luego serán asignados a usuarios.

**Base URL**: `/api/turnos`

---

## Endpoints

### 1. Crear Nuevo Turno

Crea una nueva plantilla de turno.

**URL**: `POST /api/turnos`

**Request Body**:

```json
{
  "nombre": "string - Requerido",
  "horaInicio": "string (HH:mm:ss) - Requerido",
  "horaFin": "string (HH:mm:ss) - Requerido",
  "duracionTotal": "number (minutos) - Opcional",
  "estado": "string (ACTIVO/INACTIVO) - Requerido"
}
```

**Ejemplo**:

```json
{
  "nombre": "Turno Mañana",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "duracionTotal": 480,
  "estado": "ACTIVO"
}
```

**Response**: `201 Created`

```json
{
  "id": 1,
  "nombre": "Turno Mañana",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "duracionTotal": 480,
  "estado": "ACTIVO",
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

---

### 2. Listar Todos los Turnos

Obtiene todas las plantillas de turnos (activos e inactivos).

**URL**: `GET /api/turnos`

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "nombre": "Turno Mañana",
    "horaInicio": "08:00:00",
    "horaFin": "16:00:00",
    "duracionTotal": 480,
    "estado": "ACTIVO",
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  },
  {
    "id": 2,
    "nombre": "Turno Tarde",
    "horaInicio": "16:00:00",
    "horaFin": "00:00:00",
    "duracionTotal": 480,
    "estado": "ACTIVO",
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  }
]
```

---

### 3. Obtener Turno por ID

Obtiene los detalles de una plantilla de turno específica.

**URL**: `GET /api/turnos/{id}`

**Path Parameters**:

- `id` (Long): ID del turno

**Response**: `200 OK`

```json
{
  "id": 1,
  "nombre": "Turno Mañana",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "duracionTotal": 480,
  "estado": "ACTIVO",
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

**Errores**:

- `404 Not Found`: Turno no encontrado

---

### 4. Listar Turnos Activos

Obtiene únicamente las plantillas de turnos activas.

**URL**: `GET /api/turnos/activos`

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "nombre": "Turno Mañana",
    "horaInicio": "08:00:00",
    "horaFin": "16:00:00",
    "duracionTotal": 480,
    "estado": "ACTIVO",
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  }
]
```

---

### 5. Actualizar Turno

Actualiza una plantilla de turno existente.

**URL**: `PUT /api/turnos/{id}`

**Path Parameters**:

- `id` (Long): ID del turno a actualizar

**Request Body**:

```json
{
  "nombre": "Turno Mañana Modificado",
  "horaInicio": "08:30:00",
  "horaFin": "16:30:00",
  "duracionTotal": 480,
  "estado": "ACTIVO"
}
```

**Response**: `200 OK`

```json
{
  "id": 1,
  "nombre": "Turno Mañana Modificado",
  "horaInicio": "08:30:00",
  "horaFin": "16:30:00",
  "duracionTotal": 480,
  "estado": "ACTIVO",
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T15:30:00"
}
```

---

### 6. Eliminar Turno (Soft Delete)

Desactiva una plantilla de turno (no la elimina físicamente, cambia el estado a INACTIVO).

**URL**: `DELETE /api/turnos/{id}`

**Path Parameters**:

- `id` (Long): ID del turno a eliminar

**Response**: `204 No Content`

**Nota**: Este es un **soft delete**. El turno cambia su estado a `INACTIVO` pero permanece en la base de datos.

---

## Modelos de Datos

### TurnoRequest

```typescript
{
  nombre: string;          // Nombre descriptivo del turno (requerido)
  horaInicio: string;      // Hora inicio HH:mm:ss (requerido)
  horaFin: string;         // Hora fin HH:mm:ss (requerido)
  duracionTotal?: number;  // Duración en minutos (opcional)
  estado: EstadoTurno;     // ACTIVO o INACTIVO (requerido)
}
```

### TurnoResponse

```typescript
{
  id: number;
  nombre: string;
  horaInicio: string;      // Formato HH:mm:ss
  horaFin: string;         // Formato HH:mm:ss
  duracionTotal?: number;  // Duración en minutos
  estado: EstadoTurno;
  creadoEn: string;        // ISO 8601 DateTime
  actualizadoEn: string;   // ISO 8601 DateTime
}
```

### EstadoTurno (Enum)

- `ACTIVO`: Turno disponible para asignación
- `INACTIVO`: Turno deshabilitado

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Operación exitosa (GET, PUT) |
| `201 Created` | Turno creado exitosamente |
| `204 No Content` | Turno eliminado exitosamente |
| `400 Bad Request` | Datos de entrada inválidos |
| `404 Not Found` | Turno no encontrado |
| `500 Internal Server Error` | Error del servidor |

---

## Validaciones

- ✅ Nombre del turno no puede estar vacío
- ✅ Hora de inicio debe ser anterior a hora de fin
- ✅ Estado debe ser ACTIVO o INACTIVO
- ✅ Formato de hora debe ser HH:mm:ss

---

## Notas Importantes

1. **Plantillas Reutilizables**: Los turnos son plantillas que se asignan múltiples veces a diferentes usuarios y fechas

2. **Soft Delete**: La eliminación es lógica (cambia estado a INACTIVO), preservando el historial

3. **Turnos Nocturnos**: Si `horaFin` es menor que `horaInicio`, se asume que el turno cruza medianoche

4. **Duración**: Si no se especifica `duracionTotal`, se calcula automáticamente

---

## Ejemplos de Uso

### Crear Turno

```bash
curl -X POST http://localhost:8080/api/turnos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "nombre": "Turno Noche",
    "horaInicio": "00:00:00",
    "horaFin": "08:00:00",
    "duracionTotal": 480,
    "estado": "ACTIVO"
  }'
```

### Listar Turnos Activos

```bash
curl -X GET http://localhost:8080/api/turnos/activos \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Actualizar Turno

```bash
curl -X PUT http://localhost:8080/api/turnos/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "nombre": "Turno Mañana Plus",
    "horaInicio": "07:00:00",
    "horaFin": "15:00:00",
    "duracionTotal": 480,
    "estado": "ACTIVO"
  }'
```

### Desactivar Turno

```bash
curl -X DELETE http://localhost:8080/api/turnos/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```
