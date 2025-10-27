# API REST - Módulo de Reemplazo de Turnos

## Descripción General

Este módulo gestiona el **flujo de reemplazo de turnos** entre usuarios. Maneja solicitudes, aprobaciones y rechazos de reemplazos, permitiendo que un usuario tome el turno de otro.

**Base URL**: `/api/reemplazos`

---

## Endpoints

### 1. Solicitar Nuevo Reemplazo

Crea una solicitud de reemplazo de turno.

**URL**: `POST /api/reemplazos`

**Request Body**:

```json
{
  "asignacionOriginalId": "number - Requerido",
  "reemplazanteId": "string (Keycloak ID) - Requerido",
  "fechaReemplazo": "string (YYYY-MM-DD) - Requerido",
  "motivo": "string - Opcional",
  "observaciones": "string - Opcional"
}
```

**Ejemplo**:

```json
{
  "asignacionOriginalId": 1,
  "reemplazanteId": "xyz-789-abc-012",
  "fechaReemplazo": "2025-10-28",
  "motivo": "Emergencia médica",
  "observaciones": "Necesito que alguien cubra mi turno"
}
```

**Response**: `201 Created`

```json
{
  "id": 1,
  "asignacionOriginalId": 1,
  "usuarioOriginalId": "abc-123-def-456",
  "reemplazanteId": "xyz-789-abc-012",
  "fechaReemplazo": "2025-10-28",
  "estado": "PENDIENTE",
  "motivo": "Emergencia médica",
  "observaciones": "Necesito que alguien cubra mi turno",
  "aprobadorId": null,
  "fechaAprobacion": null,
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

**Validaciones**:

- ✅ La asignación original debe existir y estar activa
- ✅ El reemplazante no puede ser el mismo usuario original
- ✅ El reemplazante debe estar disponible en la fecha/hora del turno
- ✅ No debe existir otro reemplazo pendiente para la misma asignación

---

### 2. Obtener Reemplazo por ID

Obtiene los detalles de un reemplazo específico.

**URL**: `GET /api/reemplazos/{id}`

**Path Parameters**:

- `id` (Long): ID del reemplazo

**Response**: `200 OK`

```json
{
  "id": 1,
  "asignacionOriginalId": 1,
  "usuarioOriginalId": "abc-123-def-456",
  "reemplazanteId": "xyz-789-abc-012",
  "fechaReemplazo": "2025-10-28",
  "estado": "PENDIENTE",
  "motivo": "Emergencia médica",
  "observaciones": "Necesito que alguien cubra mi turno",
  "aprobadorId": null,
  "fechaAprobacion": null,
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T10:00:00"
}
```

**Errores**:

- `404 Not Found`: Reemplazo no encontrado

---

### 3. Listar Reemplazos Pendientes

Obtiene todas las solicitudes de reemplazo pendientes de aprobación.

**URL**: `GET /api/reemplazos/pendientes`

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "asignacionOriginalId": 1,
    "usuarioOriginalId": "abc-123-def-456",
    "reemplazanteId": "xyz-789-abc-012",
    "fechaReemplazo": "2025-10-28",
    "estado": "PENDIENTE",
    "motivo": "Emergencia médica",
    "observaciones": "Necesito que alguien cubra mi turno",
    "aprobadorId": null,
    "fechaAprobacion": null,
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T10:00:00"
  }
]
```

**Uso**: Útil para administradores que necesitan revisar y aprobar/rechazar solicitudes.

---

### 4. Listar Reemplazos por Reemplazante

Obtiene todos los reemplazos donde un usuario es el reemplazante.

**URL**: `GET /api/reemplazos/reemplazante/{keycloakId}`

**Path Parameters**:

- `keycloakId` (String): Keycloak ID del reemplazante

**Response**: `200 OK`

```json
[
  {
    "id": 1,
    "asignacionOriginalId": 1,
    "usuarioOriginalId": "abc-123-def-456",
    "reemplazanteId": "xyz-789-abc-012",
    "fechaReemplazo": "2025-10-28",
    "estado": "APROBADO",
    "motivo": "Emergencia médica",
    "observaciones": "Necesito que alguien cubra mi turno",
    "aprobadorId": 100,
    "fechaAprobacion": "2025-10-27T15:00:00",
    "creadoEn": "2025-10-27T10:00:00",
    "actualizadoEn": "2025-10-27T15:00:00"
  }
]
```

---

### 5. Aprobar Reemplazo

Aprueba una solicitud de reemplazo y actualiza la asignación.

**URL**: `PATCH /api/reemplazos/{id}/aprobar`

**Path Parameters**:

- `id` (Long): ID del reemplazo

**Query Parameters** (opcionales):

- `aprobadorId` (Long): ID del administrador que aprueba

**Ejemplo**: `PATCH /api/reemplazos/1/aprobar?aprobadorId=100`

**Response**: `200 OK`

```json
{
  "id": 1,
  "asignacionOriginalId": 1,
  "usuarioOriginalId": "abc-123-def-456",
  "reemplazanteId": "xyz-789-abc-012",
  "fechaReemplazo": "2025-10-28",
  "estado": "APROBADO",
  "motivo": "Emergencia médica",
  "observaciones": "Necesito que alguien cubra mi turno",
  "aprobadorId": 100,
  "fechaAprobacion": "2025-10-27T15:00:00",
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T15:00:00"
}
```

**Efectos**:

- ✅ Cambia el estado del reemplazo a `APROBADO`
- ✅ Actualiza el `usuarioId` en la asignación original al reemplazante
- ✅ Registra el aprobador y fecha de aprobación

---

### 6. Rechazar Reemplazo

Rechaza una solicitud de reemplazo.

**URL**: `PATCH /api/reemplazos/{id}/rechazar`

**Path Parameters**:

- `id` (Long): ID del reemplazo

**Query Parameters** (opcionales):

- `aprobadorId` (Long): ID del administrador que rechaza
- `motivo` (String): Motivo del rechazo

**Ejemplo**: `PATCH /api/reemplazos/1/rechazar?aprobadorId=100&motivo=No disponible`

**Response**: `200 OK`

```json
{
  "id": 1,
  "asignacionOriginalId": 1,
  "usuarioOriginalId": "abc-123-def-456",
  "reemplazanteId": "xyz-789-abc-012",
  "fechaReemplazo": "2025-10-28",
  "estado": "RECHAZADO",
  "motivo": "Emergencia médica",
  "observaciones": "No disponible",
  "aprobadorId": 100,
  "fechaAprobacion": "2025-10-27T15:00:00",
  "creadoEn": "2025-10-27T10:00:00",
  "actualizadoEn": "2025-10-27T15:00:00"
}
```

---

## Modelos de Datos

### ReemplazoRequest

```typescript
{
  asignacionOriginalId: number;  // ID de la asignación a reemplazar (requerido)
  reemplazanteId: string;        // Keycloak ID del reemplazante (requerido)
  fechaReemplazo: string;        // Fecha del reemplazo YYYY-MM-DD (requerido)
  motivo?: string;               // Motivo del reemplazo (opcional)
  observaciones?: string;        // Notas adicionales (opcional)
}
```

### ReemplazoResponse

```typescript
{
  id: number;
  asignacionOriginalId: number;
  usuarioOriginalId: string;
  reemplazanteId: string;
  fechaReemplazo: string;        // YYYY-MM-DD
  estado: EstadoReemplazo;
  motivo?: string;
  observaciones?: string;
  aprobadorId?: number;
  fechaAprobacion?: string;      // ISO 8601 DateTime
  creadoEn: string;              // ISO 8601 DateTime
  actualizadoEn: string;         // ISO 8601 DateTime
}
```

### EstadoReemplazo (Enum)

- `PENDIENTE`: Solicitud creada, esperando aprobación
- `APROBADO`: Reemplazo aprobado y aplicado
- `RECHAZADO`: Solicitud rechazada

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Operación exitosa (GET, PATCH) |
| `201 Created` | Reemplazo solicitado exitosamente |
| `400 Bad Request` | Datos de entrada inválidos o violación de reglas |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Conflicto (reemplazo ya existe, reemplazante no disponible) |
| `500 Internal Server Error` | Error del servidor |

---

## Flujo de Trabajo

```text
1. Usuario A solicita reemplazo
   POST /api/reemplazos
   Estado: PENDIENTE

2. Administrador revisa solicitudes
   GET /api/reemplazos/pendientes

3a. Si aprueba:
    PATCH /api/reemplazos/{id}/aprobar
    Estado: APROBADO
    → La asignación se actualiza al Usuario B

3b. Si rechaza:
    PATCH /api/reemplazos/{id}/rechazar
    Estado: RECHAZADO
    → La asignación permanece con Usuario A
```

---

## Notas Importantes

1. **Validación de Disponibilidad**: El sistema valida automáticamente que el reemplazante esté disponible

2. **Una Solicitud a la Vez**: No se permite más de una solicitud de reemplazo pendiente para la misma asignación

3. **Actualización Automática**: Al aprobar un reemplazo, la asignación original se actualiza automáticamente

4. **Auditoría Completa**: Se registra quién aprobó/rechazó y cuándo

5. **Estados Finales**: Los estados APROBADO y RECHAZADO son finales (no se pueden revertir)

---

## Ejemplos de Uso

### Solicitar Reemplazo

```bash
curl -X POST http://localhost:8080/api/reemplazos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "asignacionOriginalId": 1,
    "reemplazanteId": "xyz-789-abc-012",
    "fechaReemplazo": "2025-10-28",
    "motivo": "Emergencia familiar"
  }'
```

### Consultar Reemplazos Pendientes

```bash
curl -X GET http://localhost:8080/api/reemplazos/pendientes \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Aprobar Reemplazo

```bash
curl -X PATCH "http://localhost:8080/api/reemplazos/1/aprobar?aprobadorId=100" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Rechazar Reemplazo

```bash
curl -X PATCH "http://localhost:8080/api/reemplazos/1/rechazar?aprobadorId=100&motivo=Reemplazante no disponible" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Consultar Reemplazos como Reemplazante

```bash
curl -X GET http://localhost:8080/api/reemplazos/reemplazante/xyz-789-abc-012 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Casos de Uso

### Escenario 1: Emergencia Médica

```json
{
  "asignacionOriginalId": 5,
  "reemplazanteId": "user-456",
  "fechaReemplazo": "2025-10-30",
  "motivo": "Emergencia médica - cita urgente",
  "observaciones": "Turno de 8:00 a 16:00"
}
```

### Escenario 2: Intercambio de Turnos

```json
{
  "asignacionOriginalId": 10,
  "reemplazanteId": "user-789",
  "fechaReemplazo": "2025-11-01",
  "motivo": "Intercambio acordado con compañero",
  "observaciones": "Cambio por turno del viernes"
}
```

### Escenario 3: Vacaciones Imprevistas

```json
{
  "asignacionOriginalId": 15,
  "reemplazanteId": "user-321",
  "fechaReemplazo": "2025-11-05",
  "motivo": "Viaje de última hora",
  "observaciones": "Posible cobertura de varios días"
}
```
