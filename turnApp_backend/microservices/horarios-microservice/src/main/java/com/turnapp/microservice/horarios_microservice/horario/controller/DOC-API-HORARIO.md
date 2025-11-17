# 📅 API de Horarios - Microservicio de Gestión de Horarios

## Descripción General

El microservicio de **Horarios** gestiona la planificación semanal y mensual de turnos, permitiendo:

✅ Crear horarios como plantillas (borradores)  
✅ Agregar asignaciones planificadas a los horarios  
✅ Publicar horarios masivamente a `turnos-microservice`  
✅ Consultar vistas consolidadas con datos reales de turnos  
✅ Generar reportes de calendario y estadísticas  

---

## Características Principales

### 🔹 Gestión de Horarios

- CRUD completo de horarios
- Estados del ciclo de vida: `BORRADOR` → `PUBLICADO` → `ACTIVO` → `FINALIZADO`
- Validaciones de períodos y fechas

### 🔹 Gestión de Detalles

- Asignaciones planificadas (detalles de horario)
- Carga masiva de detalles (batch)
- Sincronización con turnos-microservice

### 🔹 Publicación Síncrona

- Publicación de horarios a `turnos-microservice` vía Feign
- Creación automática de asignaciones
- Reporte detallado de éxitos y fallos

### 🔹 Vistas Consolidadas

- Consulta de asignaciones reales desde `turnos-microservice`
- Estadísticas de sincronización y completitud
- Organización por fecha para calendarios

---

## Endpoints de la API

### **Base URL**

```text
http://localhost:{port}/api/horarios
```

> **Nota:** El puerto es asignado dinámicamente por Eureka. Use el discovery client o API Gateway.

---

## 📋 CRUD de Horarios

### **1. Crear Horario**

**Endpoint:** `POST /api/horarios`

**Descripción:** Crea un nuevo horario en estado `BORRADOR`.

**Headers:**

```text
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Request Body:**

```json
{
  "nombre": "Horario Diciembre 2025",
  "fechaInicio": "2025-12-01",
  "fechaFin": "2025-12-31",
  "descripcion": "Horario mensual para diciembre con cobertura completa"
}
```

**Response:** `201 Created`

```json
{
  "id": 1,
  "nombre": "Horario Diciembre 2025",
  "fechaInicio": "2025-12-01",
  "fechaFin": "2025-12-31",
  "estado": "BORRADOR",
  "descripcion": "Horario mensual para diciembre con cobertura completa",
  "creadoPor": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "creadoEn": "2025-11-13T10:30:00",
  "actualizadoEn": "2025-11-13T10:30:00",
  "publicadoEn": null,
  "cantidadDetalles": 0,
  "detalles": null
}
```

---

### **2. Listar Horarios**

**Endpoint:** `GET /api/horarios`

**Descripción:** Obtiene todos los horarios ordenados por fecha de inicio descendente.

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "nombre": "Horario Diciembre 2025",
    "fechaInicio": "2025-12-01",
    "fechaFin": "2025-12-31",
    "estado": "BORRADOR",
    "creadoPor": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "creadoEn": "2025-11-13T10:30:00",
    "actualizadoEn": "2025-11-13T10:30:00",
    "cantidadDetalles": 0
  }
]
```

---

### **3. Obtener Horario por ID**

**Endpoint:** `GET /api/horarios/{id}?incluirDetalles={true|false}`

**Parámetros de Query:**

- `incluirDetalles` (opcional): `true` para incluir lista de detalles, `false` por defecto

**Ejemplo:** `GET /api/horarios/1?incluirDetalles=true`

**Response:** `200 OK`

```json
{
  "id": 1,
  "nombre": "Horario Diciembre 2025",
  "fechaInicio": "2025-12-01",
  "fechaFin": "2025-12-31",
  "estado": "BORRADOR",
  "cantidadDetalles": 2,
  "detalles": [
    {
      "id": 1,
      "horarioId": 1,
      "usuarioId": "user-uuid-123",
      "fecha": "2025-12-01",
      "turnoId": 1,
      "nombreTurno": "Turno Mañana",
      "asignacionId": null,
      "estado": "PLANIFICADO",
      "creadoEn": "2025-11-13T10:35:00"
    }
  ]
}
```

---

### **4. Actualizar Horario**

**Endpoint:** `PUT /api/horarios/{id}`

**Descripción:** Actualiza un horario. Solo horarios en `BORRADOR` pueden ser actualizados.

**Request Body:**

```json
{
  "nombre": "Horario Diciembre 2025 - Actualizado",
  "fechaInicio": "2025-12-01",
  "fechaFin": "2025-12-31",
  "descripcion": "Descripción actualizada"
}
```

**Response:** `200 OK` (mismo formato que crear)

**Errores:**

- `404 NOT FOUND` - Horario no existe
- `409 CONFLICT` - Horario no está en estado BORRADOR

---

### **5. Eliminar Horario**

**Endpoint:** `DELETE /api/horarios/{id}`

**Descripción:** Elimina un horario. Solo horarios en `BORRADOR` pueden ser eliminados.

**Response:** `204 No Content`

**Errores:**

- `404 NOT FOUND` - Horario no existe
- `409 CONFLICT` - Horario no está en estado BORRADOR

---

## 📝 Gestión de Detalles

### **6. Agregar Detalle**

**Endpoint:** `POST /api/horarios/{id}/detalles`

**Descripción:** Agrega una asignación planificada al horario.

**Request Body:**

```json
{
  "usuarioId": "user-uuid-123",
  "fecha": "2025-12-01",
  "turnoId": 1,
  "observaciones": "Primera asignación del mes"
}
```

**Response:** `201 Created`

```json
{
  "id": 1,
  "horarioId": 1,
  "usuarioId": "user-uuid-123",
  "fecha": "2025-12-01",
  "turnoId": 1,
  "nombreTurno": "Turno Mañana",
  "asignacionId": null,
  "estado": "PLANIFICADO",
  "observaciones": "Primera asignación del mes",
  "creadoEn": "2025-11-13T10:35:00",
  "actualizadoEn": "2025-11-13T10:35:00",
  "confirmadoEn": null
}
```

**Validaciones:**

- Horario debe estar en `BORRADOR`
- Fecha debe estar dentro del período del horario
- Turno debe existir en turnos-microservice

---

### **7. Agregar Detalles en Lote**

**Endpoint:** `POST /api/horarios/{id}/detalles/lote`

**Descripción:** Agrega múltiples asignaciones de forma masiva.

**Request Body:**

```json
[
  {
    "usuarioId": "user-uuid-123",
    "fecha": "2025-12-01",
    "turnoId": 1
  },
  {
    "usuarioId": "user-uuid-456",
    "fecha": "2025-12-01",
    "turnoId": 2
  },
  {
    "usuarioId": "user-uuid-789",
    "fecha": "2025-12-02",
    "turnoId": 1
  }
]
```

**Response:** `201 Created`

```json
[
  {
    "id": 1,
    "horarioId": 1,
    "usuarioId": "user-uuid-123",
    "fecha": "2025-12-01",
    "turnoId": 1,
    "nombreTurno": "Turno Mañana",
    "estado": "PLANIFICADO"
  },
  {
    "id": 2,
    "horarioId": 1,
    "usuarioId": "user-uuid-456",
    "fecha": "2025-12-01",
    "turnoId": 2,
    "nombreTurno": "Turno Tarde",
    "estado": "PLANIFICADO"
  }
]
```

---

### **8. Eliminar Detalle**

**Endpoint:** `DELETE /api/horarios/{horarioId}/detalles/{detalleId}`

**Descripción:** Elimina un detalle del horario.

**Response:** `204 No Content`

---

## 📢 Publicación de Horarios

### **9. Publicar Horario**

**Endpoint:** `POST /api/horarios/{id}/publicar`

**Descripción:** Publica el horario, creando asignaciones en `turnos-microservice`.

**Proceso:**

1. Valida que el horario esté en `BORRADOR` y tenga detalles
2. Para cada detalle:
   - Crea `AsignacionRequest`
   - Llama a `POST /api/asignaciones` en turnos-microservice (síncrono)
   - Guarda `asignacionId` retornado
   - Marca detalle como `CONFIRMADO`
3. Cambia estado del horario a `PUBLICADO`
4. Retorna reporte detallado

**Response:** `200 OK` (publicación completa) o `206 Partial Content` (publicación parcial)

```json
{
  "horarioId": 1,
  "nombreHorario": "Horario Diciembre 2025",
  "totalProcesados": 10,
  "totalExitosos": 9,
  "totalFallidos": 1,
  "asignacionesExitosas": [
    {
      "detalleId": 1,
      "asignacionId": 101,
      "usuarioId": "user-uuid-123",
      "fecha": "2025-12-01",
      "nombreTurno": "Turno Mañana"
    }
  ],
  "asignacionesFallidas": [
    {
      "detalleId": 5,
      "usuarioId": "user-uuid-999",
      "fecha": "2025-12-05",
      "turnoId": 1,
      "motivoError": "Usuario no encontrado en el sistema"
    }
  ]
}
```

**Códigos de Estado:**

- `200 OK` - Publicación completamente exitosa (todos confirmados)
- `206 Partial Content` - Publicación parcialmente exitosa (algunos confirmados, otros fallidos)
- `500 Internal Server Error` - Publicación completamente fallida (todos fallidos)

**Errores Posibles:**

- `404 NOT FOUND` - Horario no existe
- `409 CONFLICT` - Horario no está en estado BORRADOR o no tiene detalles
- `503 SERVICE UNAVAILABLE` - turnos-microservice no disponible

**Nota de Escalabilidad:**
> ⚠️ **IMPORTANTE:** La publicación actual es **síncrona**. Para volúmenes altos (>100 asignaciones),
> se recomienda migrar a un patrón **asíncrono con eventos** (Apache Kafka, RabbitMQ) para mejorar
> el rendimiento y la tolerancia a fallos.

---

## 📊 Vistas Consolidadas y Reportes

### **10. Obtener Vista Consolidada**

**Endpoint:** `GET /api/horarios/{id}/consolidado`

**Descripción:** Obtiene vista consolidada del horario con asignaciones reales desde turnos-microservice.

**Response:** `200 OK`

```json
{
  "horario": {
    "id": 1,
    "nombre": "Horario Diciembre 2025",
    "estado": "PUBLICADO",
    "fechaInicio": "2025-12-01",
    "fechaFin": "2025-12-31"
  },
  "asignacionesPorFecha": {
    "2025-12-01": [
      {
        "id": 101,
        "usuarioId": "user-uuid-123",
        "turnoId": 1,
        "nombreTurno": "Turno Mañana",
        "fecha": "2025-12-01",
        "horaInicio": "08:00:00",
        "horaFin": "16:00:00",
        "estado": "ASIGNADO"
      },
      {
        "id": 102,
        "usuarioId": "user-uuid-456",
        "turnoId": 2,
        "nombreTurno": "Turno Tarde",
        "fecha": "2025-12-01",
        "horaInicio": "14:00:00",
        "horaFin": "22:00:00",
        "estado": "ASIGNADO"
      }
    ],
    "2025-12-02": [
      {
        "id": 103,
        "usuarioId": "user-uuid-789",
        "turnoId": 1,
        "nombreTurno": "Turno Mañana",
        "fecha": "2025-12-02",
        "horaInicio": "08:00:00",
        "horaFin": "16:00:00",
        "estado": "COMPLETADO"
      }
    ]
  },
  "estadisticas": {
    "totalPlanificadas": 10,
    "totalConfirmadas": 9,
    "totalCompletadas": 1,
    "totalCanceladas": 0,
    "porcentajeSincronizacion": 90.0
  }
}
```

**Uso:** Ideal para generar vistas de calendario en frontend.

---

### **11. Buscar Horarios por Fecha**

**Endpoint:** `GET /api/horarios/fecha?fecha={fecha}`

**Descripción:** Busca horarios que cubren una fecha específica.

**Parámetros de Query:**

- `fecha` (requerido): Fecha en formato `YYYY-MM-DD`

**Ejemplo:** `GET /api/horarios/fecha?fecha=2025-12-15`

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "nombre": "Horario Diciembre 2025",
    "fechaInicio": "2025-12-01",
    "fechaFin": "2025-12-31",
    "estado": "ACTIVO"
  }
]
```

---

## 🔒 Seguridad y Autenticación

### Roles y Permisos

| Endpoint | EMPLEADO (GET) | ADMIN (ALL) |
|----------|---------------|------------|
| `GET /api/horarios` | ✅ | ✅ |
| `GET /api/horarios/{id}` | ✅ | ✅ |
| `GET /api/horarios/{id}/consolidado` | ✅ | ✅ |
| `GET /api/horarios/fecha` | ✅ | ✅ |
| `POST /api/horarios` | ❌ | ✅ |
| `PUT /api/horarios/{id}` | ❌ | ✅ |
| `DELETE /api/horarios/{id}` | ❌ | ✅ |
| `POST /api/horarios/{id}/detalles` | ❌ | ✅ |
| `POST /api/horarios/{id}/publicar` | ❌ | ✅ |

### Headers Requeridos

Todos los endpoints requieren autenticación JWT:

```http
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

---

## 🔄 Estados del Horario

```text
BORRADOR ──publicar──> PUBLICADO ──activar──> ACTIVO ──finalizar──> FINALIZADO
   │                                             │
   └────────────────cancelar───────────────────>│
                                                 v
                                              CANCELADO
```

- **BORRADOR**: Horario en edición, no visible para empleados
- **PUBLICADO**: Asignaciones creadas en turnos, visible pero no vigente
- **ACTIVO**: Horario vigente actualmente
- **FINALIZADO**: Período completado
- **CANCELADO**: Cancelado administrativamente

---

## 📌 Ejemplos de Uso

### Flujo Completo: Crear y Publicar Horario

```bash
# 1. Crear horario
curl -X POST http://localhost:8080/api/horarios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Horario Semana 1",
    "fechaInicio": "2025-12-01",
    "fechaFin": "2025-12-07"
  }'
# Retorna: {"id": 1, ...}

# 2. Agregar detalles en lote
curl -X POST http://localhost:8080/api/horarios/1/detalles/lote \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '[
    {"usuarioId": "user-1", "fecha": "2025-12-01", "turnoId": 1},
    {"usuarioId": "user-2", "fecha": "2025-12-01", "turnoId": 2}
  ]'

# 3. Publicar horario
curl -X POST http://localhost:8080/api/horarios/1/publicar \
  -H "Authorization: Bearer $TOKEN"

# 4. Obtener vista consolidada
curl -X GET http://localhost:8080/api/horarios/1/consolidado \
  -H "Authorization: Bearer $TOKEN"
```

---

## ⚠️ Manejo de Errores

### Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Operación exitosa |
| `201 Created` | Recurso creado |
| `204 No Content` | Eliminación exitosa |
| `206 Partial Content` | Publicación parcialmente exitosa |
| `400 Bad Request` | Validación fallida |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Violación de regla de negocio |
| `503 Service Unavailable` | Microservicio externo no disponible |

### Formato de Error

```json
{
  "timestamp": "2025-11-13T10:45:00",
  "status": 409,
  "error": "Conflict",
  "message": "No se puede actualizar un horario en estado PUBLICADO",
  "path": "/api/horarios/1",
  "validationErrors": null
}
```

---
