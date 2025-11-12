# API REST - Módulo de Asignación de Turnos

## Descripción General

Este módulo es el **controlador principal del sistema**, encargado de gestionar la asignación de turnos a usuarios. Maneja la validación de solapamientos y disponibilidad automáticamente.

**Base URL**: `/api/asignaciones`

---

## Endpoints

### 1. Crear Nueva Asignación

Crea una nueva asignación de turno con validación automática de solapamientos y disponibilidad.

**URL**: `POST /api/asignaciones`

**Request Body**:

```json
{
  "usuarioId": "string (Keycloak ID) - Requerido",
  "turnoId": "number - Requerido",
  "fecha": "string (YYYY-MM-DD) - Requerido",
  "observaciones": "string - Opcional"
}
```

**Response**: `201 Created`

```json
{
  "id": 1,
  "usuarioId": "abc-123-def-456",
  "turnoId": 5,
  "nombreTurno": "Turno Mañana",
  "fecha": "2025-10-28",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "estado": "ACTIVO",
  "observaciones": "Asignación regular",
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

**Validaciones**:

- ✅ No permite asignar turnos que se solapen con asignaciones existentes
- ✅ Valida disponibilidad del usuario
- ✅ Verifica que el turno exista y esté activo

---

### 2. Obtener Asignación por ID

Obtiene los detalles completos de una asignación específica.

**URL**: `GET /api/asignaciones/{id}`

**Path Parameters**:

- `id` (Long): ID de la asignación

**Response**: `200 OK`

```json
{
  "id": 1,
  "usuarioId": "abc-123-def-456",
  "turnoId": 5,
  "nombreTurno": "Turno Mañana",
  "fecha": "2025-10-28",
  "horaInicio": "08:00:00",
  "horaFin": "16:00:00",
  "estado": "ACTIVO",
  "observaciones": "Asignación regular",
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

**Errores**:

- `404 Not Found`: Asignación no encontrada

---

### 3. Listar Asignaciones por Usuario

Obtiene todas las asignaciones de un usuario específico.

**URL**: `GET /api/asignaciones/usuario/{keycloakId}`

**Path Parameters**:

- `keycloakId` (String): Keycloak ID del usuario

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "usuarioId": "abc-123-def-456",
    "turnoId": 5,
    "nombreTurno": "Turno Mañana",
    "fecha": "2025-10-28",
    "horaInicio": "08:00:00",
    "horaFin": "16:00:00",
    "estado": "ACTIVO",
    "observaciones": "Asignación regular",
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  }
]
```

---

### 4. Listar Asignaciones por Fecha

Obtiene todas las asignaciones programadas para una fecha específica.

**URL**: `GET /api/asignaciones/fecha`

**Query Parameters**:

- `fecha` (LocalDate, formato: `YYYY-MM-DD`): Fecha a consultar

**Ejemplo**: `GET /api/asignaciones/fecha?fecha=2025-10-28`

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "usuarioId": "abc-123-def-456",
    "turnoId": 5,
    "nombreTurno": "Turno Mañana",
    "fecha": "2025-10-28",
    "horaInicio": "08:00:00",
    "horaFin": "16:00:00",
    "estado": "ACTIVO",
    "observaciones": null,
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  }
]
```

---

### 5. Listar Asignaciones por Período

Obtiene todas las asignaciones dentro de un rango de fechas específico. Útil para generar reportes y consolidar horarios.

**URL**: `GET /api/asignaciones/periodo`

**Query Parameters**:

- `fechaInicio` (LocalDate, formato: `YYYY-MM-DD`): Fecha de inicio del período (requerido)
- `fechaFin` (LocalDate, formato: `YYYY-MM-DD`): Fecha de fin del período (requerido)

**Ejemplo**: `GET /api/asignaciones/periodo?fechaInicio=2025-10-01&fechaFin=2025-10-31`

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "usuarioId": "abc-123-def-456",
    "turnoId": 5,
    "nombreTurno": "Turno Mañana",
    "fecha": "2025-10-15",
    "horaInicio": "08:00:00",
    "horaFin": "16:00:00",
    "estado": "COMPLETADO",
    "observaciones": null,
    "creadoEn": "2025-10-14T10:00:00",
    "actualizadoEn": "2025-10-15T16:05:00"
  },
  {
    "id": 2,
    "usuarioId": "abc-123-def-456",
    "turnoId": 6,
    "nombreTurno": "Turno Tarde",
    "fecha": "2025-10-20",
    "horaInicio": "14:00:00",
    "horaFin": "22:00:00",
    "estado": "ACTIVO",
    "observaciones": "Turno adicional",
    "creadoEn": "2025-10-19T09:00:00",
    "actualizadoEn": "2025-10-19T09:00:00"
  }
]
```

**Notas**:

- Incluye todas las asignaciones entre `fechaInicio` y `fechaFin` (ambos inclusive)
- Útil para la consolidación de horarios en el microservicio de Horarios
- Devuelve array vacío si no hay asignaciones en el período

---

### 6. Cancelar Asignación

Cancela una asignación existente (no la elimina, cambia el estado a CANCELADO).

**URL**: `PATCH /api/asignaciones/{id}/cancelar`

**Path Parameters**:

- `id` (Long): ID de la asignación a cancelar

**Query Parameters** (opcionales):

- `motivo` (String): Motivo de la cancelación

**Ejemplo**: `PATCH /api/asignaciones/1/cancelar?motivo=Enfermedad`

**Response**: `204 No Content`

---

### 7. Completar Asignación

Marca una asignación como completada.

**URL**: `PATCH /api/asignaciones/{id}/completar`

**Path Parameters**:

- `id` (Long): ID de la asignación

**Response**: `204 No Content`

---

## Modelos de Datos

### AsignacionRequest

```typescript
{
  usuarioId: string;      // Keycloak ID del usuario (requerido)
  turnoId: number;        // ID del turno a asignar (requerido)
  fecha: string;          // Fecha de la asignación YYYY-MM-DD (requerido)
  observaciones?: string; // Notas adicionales (opcional)
}
```

### AsignacionResponse

```typescript
{
  id: number;
  usuarioId: string;
  turnoId: number;
  nombreTurno: string;
  fecha: string;
  horaInicio: string;     // Formato HH:mm:ss
  horaFin: string;        // Formato HH:mm:ss
  estado: EstadoAsignacion;
  observaciones?: string;
  creadoEn: string;       // ISO 8601 DateTime
  actualizadoEn: string;  // ISO 8601 DateTime
}
```

### EstadoAsignacion (Enum)

- `ACTIVO`: Asignación vigente
- `COMPLETADO`: Turno completado
- `CANCELADO`: Asignación cancelada

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Operación exitosa (GET) |
| `201 Created` | Asignación creada exitosamente |
| `204 No Content` | Operación de actualización exitosa (PATCH) |
| `400 Bad Request` | Datos de entrada inválidos o violación de reglas de negocio |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Conflicto de solapamiento de turnos |
| `500 Internal Server Error` | Error del servidor |

---

## Notas Importantes

1. **Validación Automática**: El sistema valida automáticamente:
   - Solapamiento de turnos para el mismo usuario
   - Disponibilidad del usuario en la fecha/hora especificada
   - Existencia y estado activo del turno

2. **Soft Delete**: Las cancelaciones no eliminan registros, solo cambian el estado

3. **Zona Horaria**: Todas las fechas y horas se manejan en la zona horaria del servidor

4. **Autenticación**: Requiere token JWT válido (configurado en API Gateway)

---

## Ejemplos de Uso

### Crear Asignación

```bash
curl -X POST http://localhost:8080/api/asignaciones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "usuarioId": "abc-123-def-456",
    "turnoId": 5,
    "fecha": "2025-10-28",
    "observaciones": "Turno regular"
  }'
```

### Consultar Asignaciones por Fecha

```bash
curl -X GET "http://localhost:8080/api/asignaciones/fecha?fecha=2025-10-28" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Consultar Asignaciones por Período

```bash
curl -X GET "http://localhost:8080/api/asignaciones/periodo?fechaInicio=2025-10-01&fechaFin=2025-10-31" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Cancelar Asignación

```bash
curl -X PATCH "http://localhost:8080/api/asignaciones/1/cancelar?motivo=Enfermedad" \
  -H "Authorization: Bearer YOUR_TOKEN"
```
