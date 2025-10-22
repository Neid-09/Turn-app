# Testing de Sincronización - Resumen Ejecutivo

## ✅ Implementación Completada

### Nuevos Componentes

1. **DTO**: `ReporteSincronizacionResponse.java`
   - Reporte completo de usuarios en ambos sistemas
   - Identifica huérfanos en Keycloak y BD

2. **Servicio**:
   - `generarReporteSincronizacion()` - Compara ambos sistemas
   - `limpiarUsuarioHuerfanoBD(String keycloakId)` - Limpia huérfanos de BD

3. **Controller** - 2 nuevos endpoints:
   - `GET /usuarios/sync/reporte` (ADMIN)
   - `DELETE /usuarios/sync/limpiar-bd/{keycloakId}` (ADMIN)

---

## 🎯 Solución al Problema

### Problema Original

- Usuario eliminado de Keycloak → Queda en BD → No se lista
- Usuario eliminado de BD → Queda en Keycloak → No se lista

### Solución

1. **Reporte de Sincronización**: Detecta usuarios huérfanos en ambos sistemas
2. **Limpieza Automática**: Permite eliminar usuarios huérfanos de BD de forma segura
3. **Validación en CRUD**: Métodos individuales validan sincronización antes de operar

---

## 🧪 Cómo Probarlo (Paso a Paso)

### Paso 1: Obtener Token de ADMIN

```bash
POST http://localhost:8080/realms/turn-app/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
&client_id=spring-cloud-gateway-client
&client_secret=<secret>
&username=admin@turnapp.com
&password=<password>
```

### Paso 2: Crear Usuario de Prueba

```bash
POST http://localhost:8080/usuarios
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "email": "test-sync@example.com",
  "firstName": "Test",
  "lastName": "Sync",
  "password": "Test1234!",
  "codigoEmpleado": "SYNC-001",
  "cargo": "Tester",
  "fechaContratacion": "2025-01-01",
  "rolApp": "CAJERO",
  "numeroIdentificacion": "SYNC123",
  "telefono": "555-9999"
}
```

Guardar el `keycloakId` de la respuesta.

### Paso 3: Simular Usuario Huérfano

  *Opción A: Eliminar de Keycloak (crear huérfano en BD)**

1. Ir a <http://localhost:9090/admin/master/console/#/turn-app/users>
2. Buscar `test-sync@example.com`
3. Eliminar el usuario
4. El registro queda en BD pero no en Keycloak

*Opción B: Eliminar de BD (crear huérfano en Keycloak)**

1. Conectarse a MySQL: `mysql -u root -p`
2. `USE turn_app_usuarios;`
3. `DELETE FROM usuarios WHERE codigo_empleado = 'SYNC-001';`
4. El usuario queda en Keycloak pero no en BD

### Paso 4: Generar Reporte de Sincronización

```bash
GET http://localhost:8080/usuarios/sync/reporte
Authorization: Bearer <token-admin>
```

**Resultado Esperado** (si eliminaste de Keycloak):

```json
{
  "success": true,
  "message": "Reporte de sincronización generado exitosamente",
  "data": {
    "totalUsuariosKeycloak": 2,
    "totalUsuariosBD": 3,
    "usuariosSincronizados": 2,
    "usuariosHuerfanosKeycloak": [],
    "usuariosHuerfanosBD": [
      {
        "keycloakId": "abc-123-...",
        "email": "N/A",
        "nombre": "SYNC-001",
        "apellido": "Tester",
        "ubicacion": "BD"
      }
    ]
  }
}
```

### Paso 5: Limpiar Usuario Huérfano (si aplica)

```bash
DELETE http://localhost:8080/usuarios/sync/limpiar-bd/abc-123-...
Authorization: Bearer <token-admin>
```

**Resultado Esperado**:

```json
{
  "success": true,
  "message": "Usuario huérfano eliminado de BD exitosamente"
}
```

### Paso 6: Verificar Limpieza

```bash
GET http://localhost:8080/usuarios/sync/reporte
Authorization: Bearer <token-admin>
```

Ahora `usuariosHuerfanosBD` debería estar vacío.

---

## 📊 Casos de Prueba

| # | Escenario | Estado Esperado | Endpoint a Usar |
|---|-----------|----------------|-----------------|
| 1 | Sistemas sincronizados | `usuariosSincronizados = total` | `GET /sync/reporte` |
| 2 | Usuario solo en BD | `usuariosHuerfanosBD` contiene 1 | `GET /sync/reporte` |
| 3 | Usuario solo en Keycloak | `usuariosHuerfanosKeycloak` contiene 1 | `GET /sync/reporte` |
| 4 | Limpiar huérfano BD | Usuario eliminado de BD | `DELETE /sync/limpiar-bd/{id}` |
| 5 | Limpiar NO huérfano | Error 400 | `DELETE /sync/limpiar-bd/{id}` |

---

## 🔍 Verificación en Logs

Busca estos mensajes en la consola:

```log
# Al generar reporte
INFO  c.t.m.u.s.UsuarioServiceImpl : Reporte generado - KC: 2, BD: 3, Sincronizados: 2, Huérfanos KC: 0, Huérfanos BD: 1

# Al limpiar huérfano
INFO  c.t.m.u.s.UsuarioServiceImpl : Usuario huérfano eliminado de BD: Keycloak ID abc-123-..., Código Empleado: SYNC-001

# Al intentar limpiar no-huérfano
ERROR c.t.m.u.s.UsuarioServiceImpl : No se puede limpiar: el usuario existe en Keycloak. Email: test@example.com
```

---

## ✅ Checklist de Validación

- [ ] El reporte muestra totales correctos
- [ ] Detecta usuarios huérfanos en BD
- [ ] Detecta usuarios huérfanos en Keycloak
- [ ] Permite limpiar huérfanos de BD
- [ ] Previene limpiar usuarios que existen en Keycloak
- [ ] Los logs muestran información correcta
- [ ] Las excepciones se manejan correctamente

---

## 🎉 Resultado

Con esta implementación, ahora puedes:

1. ✅ **Detectar** inconsistencias entre Keycloak y BD
2. ✅ **Limpiar** usuarios huérfanos de BD de forma segura
3. ✅ **Prevenir** operaciones sobre usuarios inconsistentes
4. ✅ **Visualizar** el estado de sincronización en cualquier momento
