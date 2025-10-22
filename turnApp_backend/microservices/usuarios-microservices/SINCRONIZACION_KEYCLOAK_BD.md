# Documentación de Sincronización Keycloak-BD

## Problema Detectado

Cuando un usuario se elimina manualmente de Keycloak o de la BD, se crea una **inconsistencia** entre ambos sistemas:

1. **Usuario eliminado de Keycloak → queda en BD**: El usuario no aparece en listados porque la BD lo busca pero Keycloak no lo tiene.
2. **Usuario eliminado de BD → queda en Keycloak**: El usuario existe en Keycloak pero no se lista porque la búsqueda empieza en BD.

## Solución Implementada

### 1. Reporte de Sincronización

**Endpoint**: `GET /usuarios/sync/reporte`

**Descripción**: Genera un reporte completo que identifica usuarios huérfanos en ambos sistemas.

**Requiere**: ROL ADMIN

**Respuesta**:

```json
{
  "success": true,
  "message": "Reporte de sincronización generado exitosamente",
  "data": {
    "totalUsuariosKeycloak": 10,
    "totalUsuariosBD": 9,
    "usuariosSincronizados": 8,
    "usuariosHuerfanosKeycloak": [
      {
        "keycloakId": "123e4567-e89b-12d3-a456-426614174000",
        "email": "usuario@example.com",
        "nombre": "Juan",
        "apellido": "Pérez",
        "ubicacion": "KEYCLOAK"
      }
    ],
    "usuariosHuerfanosBD": [
      {
        "keycloakId": "987fcdeb-51a2-43d7-b123-987654321000",
        "email": "N/A",
        "nombre": "EMP-001",
        "apellido": "Cajero",
        "ubicacion": "BD"
      }
    ]
  }
}
```

**Interpretación del Reporte**:

- **usuariosHuerfanosKeycloak**: Usuarios que existen en Keycloak pero NO en BD
  - ⚠️ Estos usuarios no aparecerán en listados
  - 🔧 Solución: Crear el registro en BD manualmente o eliminar de Keycloak
  
- **usuariosHuerfanosBD**: Usuarios que existen en BD pero NO en Keycloak
  - ⚠️ Estos usuarios quedaron huérfanos por eliminación manual de Keycloak
  - 🔧 Solución: Usar endpoint de limpieza

---

### 2. Limpieza de Usuarios Huérfanos en BD

**Endpoint**: `DELETE /usuarios/sync/limpiar-bd/{keycloakId}`

**Descripción**: Elimina un usuario de la BD que ya NO existe en Keycloak (usuario huérfano).

**Requiere**: ROL ADMIN

**Validaciones**:

- ✅ Verifica que el usuario NO existe en Keycloak
- ✅ Verifica que el usuario SÍ existe en BD
- ❌ Si el usuario existe en Keycloak, lanza error (no es huérfano)

**Ejemplo de Uso**:

```bash
DELETE http://localhost:8080/usuarios/sync/limpiar-bd/987fcdeb-51a2-43d7-b123-987654321000
Authorization: Bearer <admin-token>
```

**Respuesta Exitosa**:

```json
{
  "success": true,
  "message": "Usuario huérfano eliminado de BD exitosamente"
}
```

**Error si el usuario existe en Keycloak**:

```json
{
  "success": false,
  "message": "No se puede limpiar: el usuario existe en Keycloak. Email: usuario@example.com",
  "timestamp": "2025-10-22T15:30:00"
}
```

---

## Flujo de Trabajo Recomendado

### Escenario 1: Usuario eliminado de Keycloak (queda en BD)

1. **Detectar**:

   ```bash
   GET /usuarios/sync/reporte
   ```

2. **Revisar** la lista de `usuariosHuerfanosBD`

3. **Limpiar cada usuario huérfano**:

   ```bash
   DELETE /usuarios/sync/limpiar-bd/{keycloakId}
   ```

---

### Escenario 2: Usuario eliminado de BD (queda en Keycloak)

1. **Detectar**:

   ```bash
   GET /usuarios/sync/reporte
   ```

2. **Revisar** la lista de `usuariosHuerfanosKeycloak`

3. **Decidir acción**:
   - **Opción A**: Recrear el usuario en BD manualmente con `POST /usuarios`
   - **Opción B**: Eliminar de Keycloak manualmente desde la consola de administración

---

## Comportamiento de Métodos CRUD

### Listados (`GET /usuarios`, `GET /usuarios/buscar`)

- ✅ Solo muestran usuarios que existen en **BD**
- ⚠️ Usuarios huérfanos de Keycloak NO aparecen (no están en BD)
- 🔍 Usar reporte de sincronización para detectarlos

### Obtención Individual (`GET /usuarios/{id}`, `GET /usuarios/email/{email}`)

- ✅ Valida sincronización antes de retornar
- ❌ Lanza `SincronizacionException` si hay inconsistencia
- 📋 Tipo de inconsistencia: `EXISTE_EN_BD_NO_EN_KEYCLOAK` o `EXISTE_EN_KEYCLOAK_NO_EN_BD`

### Actualización (`PUT /usuarios/{id}`)

- ✅ Valida sincronización antes de actualizar
- ❌ No permite actualizar si hay inconsistencia

### Eliminación (`DELETE /usuarios/{id}`)

- ✅ Intenta eliminar de Keycloak primero
- ✅ Si el usuario no existe en Keycloak, continúa y elimina de BD (limpieza automática)
- ⚠️ Registra warning en logs cuando limpia usuario huérfano

---

## Excepciones

### `SincronizacionException` (HTTP 409 Conflict)

Se lanza cuando hay inconsistencia entre Keycloak y BD.

**Tipos**:

```java
public enum TipoInconsistencia {
    EXISTE_EN_BD_NO_EN_KEYCLOAK,
    EXISTE_EN_KEYCLOAK_NO_EN_BD,
    DATOS_INCONSISTENTES
}
```

**Ejemplo de respuesta**:

```json
{
  "success": false,
  "message": "Usuario existe en Keycloak pero no en la base de datos local. Email: user@example.com, Keycloak ID: 123-456",
  "tipoInconsistencia": "EXISTE_EN_KEYCLOAK_NO_EN_BD",
  "timestamp": "2025-10-22T15:30:00"
}
```

---

## Testing Manual

### 1. Crear usuario de prueba

```bash
POST /usuarios
{
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User",
  "password": "Test1234!",
  "codigoEmpleado": "TEST-001",
  "cargo": "Tester",
  "fechaContratacion": "2025-01-01",
  "rolApp": "CAJERO",
  "numeroIdentificacion": "12345678",
  "telefono": "555-1234"
}
```

### 2. Simular inconsistencia (eliminar solo de Keycloak)

- Ir a Keycloak Admin Console
- Eliminar el usuario `test@example.com`

### 3. Generar reporte

```bash
GET /usuarios/sync/reporte
```

### 4. Limpiar usuario huérfano

```bash
DELETE /usuarios/sync/limpiar-bd/{keycloakId}
```

---

## Logs de Seguimiento

Los siguientes logs ayudan a detectar problemas de sincronización:

```log
# Limpieza de usuario huérfano en eliminación normal
WARN  - Usuario no encontrado en Keycloak, continuando con eliminación en BD: 123-456

# Generación de reporte
INFO  - Reporte generado - KC: 10, BD: 9, Sincronizados: 8, Huérfanos KC: 1, Huérfanos BD: 1

# Limpieza manual de huérfano
INFO  - Usuario huérfano eliminado de BD: Keycloak ID 123-456, Código Empleado: EMP-001
```

---

## Endpoint Summary

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/usuarios/sync/reporte` | Genera reporte de sincronización | ADMIN |
| DELETE | `/usuarios/sync/limpiar-bd/{keycloakId}` | Limpia usuario huérfano de BD | ADMIN |

---

## Mejoras Futuras (Opcional)

1. **Job Programado**: Ejecutar reporte cada noche y enviar notificación si hay inconsistencias
2. **Endpoint de Reparación Automática**: `POST /usuarios/sync/reparar` que sincroniza automáticamente
3. **Dashboard de Sincronización**: Interfaz gráfica para visualizar el estado
4. **Webhook**: Notificar cuando se detecta una inconsistencia

---

## Conclusión

Con esta implementación, ahora puedes:

- ✅ Detectar usuarios huérfanos en ambos sistemas
- ✅ Limpiar usuarios huérfanos de BD de forma segura
- ✅ Prevenir operaciones sobre usuarios inconsistentes
- ✅ Mantener la integridad entre Keycloak y BD
