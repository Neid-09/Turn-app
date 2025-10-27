# API REST - Módulo de Descansos

## Descripción General

Este módulo gestiona los **descansos o pausas** durante los turnos asignados. Permite registrar y consultar los breaks que los usuarios toman dentro de sus asignaciones.

**Base URL**: `/api/descansos`

---

## Endpoints

### 1. Registrar Nuevo Descanso

Registra un descanso durante un turno asignado.

**URL**: `POST /api/descansos`

**Request Body**:

```json
{
  "asignacionId": "number - Requerido",
  "horaInicio": "string (HH:mm:ss) - Requerido",
  "horaFin": "string (HH:mm:ss) - Requerido",
  "tipo": "string - Opcional",
  "observaciones": "string - Opcional"
}
```

**Ejemplo**:

```json
{
  "asignacionId": 1,
  "horaInicio": "10:00:00",
  "horaFin": "10:15:00",
  "tipo": "Café",
  "observaciones": "Descanso matutino"
}
```

**Response**: `201 Created`

```json
{
  "id": 1,
  "asignacionId": 1,
  "horaInicio": "10:00:00",
  "horaFin": "10:15:00",
  "duracionMinutos": 15,
  "tipo": "Café",
  "observaciones": "Descanso matutino",
  "creadoEn": "2025-10-27T10:00:00"
}
```

**Validaciones**:

- ✅ La asignación debe existir y estar activa
- ✅ El descanso debe estar dentro del horario del turno
- ✅ No puede solaparse con otros descansos de la misma asignación

---

### 2. Obtener Descanso por ID

Obtiene los detalles de un descanso específico.

**URL**: `GET /api/descansos/{id}`

**Path Parameters**:

- `id` (Long): ID del descanso

**Response**: `200 OK`

```json
{
  "id": 1,
  "asignacionId": 1,
  "horaInicio": "10:00:00",
  "horaFin": "10:15:00",
  "duracionMinutos": 15,
  "tipo": "Café",
  "observaciones": "Descanso matutino",
  "creadoEn": "2025-10-27T10:00:00"
}
```

**Errores**:

- `404 Not Found`: Descanso no encontrado

---

### 3. Listar Descansos por Asignación

Obtiene todos los descansos registrados para una asignación específica.

**URL**: `GET /api/descansos/asignacion/{asignacionId}`

**Path Parameters**:

- `asignacionId` (Long): ID de la asignación

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "asignacionId": 1,
    "horaInicio": "10:00:00",
    "horaFin": "10:15:00",
    "duracionMinutos": 15,
    "tipo": "Café",
    "observaciones": "Descanso matutino",
    "creadoEn": "2025-10-27T10:00:00"
  },
  {
    "id": 2,
    "asignacionId": 1,
    "horaInicio": "13:00:00",
    "horaFin": "14:00:00",
    "duracionMinutos": 60,
    "tipo": "Almuerzo",
    "observaciones": "Descanso para almuerzo",
    "creadoEn": "2025-10-27T13:00:00"
  }
]
```

---

### 4. Eliminar Descanso

Elimina permanentemente un descanso registrado.

**URL**: `DELETE /api/descansos/{id}`

**Path Parameters**:

- `id` (Long): ID del descanso a eliminar

**Response**: `204 No Content`

**Nota**: Esta es una **eliminación física** (no soft delete).

---

## Modelos de Datos

### DescansoRequest

```typescript
{
  asignacionId: number;    // ID de la asignación (requerido)
  horaInicio: string;      // Hora inicio HH:mm:ss (requerido)
  horaFin: string;         // Hora fin HH:mm:ss (requerido)
  tipo?: string;           // Tipo de descanso (opcional)
  observaciones?: string;  // Notas adicionales (opcional)
}
```

### DescansoResponse

```typescript
{
  id: number;
  asignacionId: number;
  horaInicio: string;       // Formato HH:mm:ss
  horaFin: string;          // Formato HH:mm:ss
  duracionMinutos: number;  // Calculado automáticamente
  tipo?: string;
  observaciones?: string;
  creadoEn: string;         // ISO 8601 DateTime
}
```

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Operación exitosa (GET) |
| `201 Created` | Descanso registrado exitosamente |
| `204 No Content` | Descanso eliminado exitosamente |
| `400 Bad Request` | Datos de entrada inválidos o violación de reglas |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Conflicto de solapamiento de descansos |
| `500 Internal Server Error` | Error del servidor |

---

## Tipos de Descanso Comunes

Aunque el campo `tipo` es libre, algunos valores comunes son:

- `Café` - Descanso corto (15 minutos)
- `Almuerzo` - Descanso de comida (30-60 minutos)
- `Refrigerio` - Snack break
- `Descanso` - Genérico
- `Otros` - Otro tipo

---

## Notas Importantes

1. **Validación de Horarios**: El descanso debe estar completamente dentro del horario del turno asignado

2. **Cálculo Automático**: La duración en minutos se calcula automáticamente basándose en horaInicio y horaFin

3. **Eliminación Física**: A diferencia de otros módulos, los descansos se eliminan permanentemente

4. **Auditoría**: Se registra la fecha de creación para fines de auditoría

---

## Ejemplos de Uso

### Registrar Descanso de Café

```bash
curl -X POST http://localhost:8080/api/descansos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "asignacionId": 1,
    "horaInicio": "10:00:00",
    "horaFin": "10:15:00",
    "tipo": "Café",
    "observaciones": "Break matutino"
  }'
```

### Registrar Descanso de Almuerzo

```bash
curl -X POST http://localhost:8080/api/descansos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "asignacionId": 1,
    "horaInicio": "13:00:00",
    "horaFin": "14:00:00",
    "tipo": "Almuerzo"
  }'
```

### Consultar Descansos de una Asignación

```bash
curl -X GET http://localhost:8080/api/descansos/asignacion/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Eliminar Descanso

```bash
curl -X DELETE http://localhost:8080/api/descansos/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Casos de Uso

### Escenario 1: Turno Mañana (8:00 - 16:00)

```json
{
  "descansos": [
    {
      "horaInicio": "10:00:00",
      "horaFin": "10:15:00",
      "tipo": "Café"
    },
    {
      "horaInicio": "13:00:00",
      "horaFin": "14:00:00",
      "tipo": "Almuerzo"
    }
  ]
}
```

### Escenario 2: Turno Tarde (16:00 - 00:00)

```json
{
  "descansos": [
    {
      "horaInicio": "18:00:00",
      "horaFin": "18:15:00",
      "tipo": "Refrigerio"
    },
    {
      "horaInicio": "20:00:00",
      "horaFin": "21:00:00",
      "tipo": "Cena"
    }
  ]
}
```
