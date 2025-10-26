# Asignación de Roles en Keycloak

## Problema Resuelto

Los roles se guardaban en la base de datos pero **NO se asignaban en Keycloak** cuando se creaba o actualizaba un usuario.

## Solución Implementada

### 1. Asignación al Crear Usuario

Cuando se registra un nuevo usuario mediante `POST /usuarios`, ahora:

1. ✅ Se crea el usuario en Keycloak
2. ✅ Se asigna el rol especificado en `rolApp` al usuario en Keycloak
3. ✅ Se guarda el usuario en la BD con el rol

**Flujo**:

```text
Usuario creado en Keycloak → Rol asignado en Keycloak → Datos guardados en BD
```

Si falla la asignación del rol, se **elimina el usuario de Keycloak** (rollback) y se lanza una excepción.

---

### 2. Actualización de Roles

Cuando se actualiza un usuario mediante `PUT /usuarios/{id}` y se cambia el `rolApp`:

1. ✅ Se **remueve** el rol anterior de Keycloak
2. ✅ Se **asigna** el nuevo rol en Keycloak
3. ✅ Se actualiza el rol en la BD

**Ejemplo**:

```json
PUT /usuarios/123e4567-e89b-12d3-a456-426614174000
{
  "rolApp": "ADMIN"
}
```

Si el usuario tenía rol `EMPLEADO`:

- Se remueve `EMPLEADO` de Keycloak
- Se asigna `ADMIN` en Keycloak
- Se actualiza en BD a `ADMIN`

---

## Métodos Implementados

### `asignarRolKeycloak(String keycloakId, String rolName)`

Asigna un rol de realm a un usuario en Keycloak.

**Parámetros**:

- `keycloakId`: ID del usuario en Keycloak
- `rolName`: Nombre del rol (`ADMIN`, `SUPERVISOR`, `EMPLEADO`)

**Excepciones**:

- `KeycloakOperationException` (404): Si el rol no existe en Keycloak
- `KeycloakOperationException` (500): Si hay un error al asignar el rol

---

### `removerRolKeycloak(String keycloakId, String rolName)`

Remueve un rol de realm de un usuario en Keycloak.

**Parámetros**:

- `keycloakId`: ID del usuario en Keycloak
- `rolName`: Nombre del rol a remover

**Comportamiento**:

- Si el rol no existe, solo registra un warning (no lanza excepción)
- Si hay otro error, lanza `KeycloakOperationException`

---

## Requisitos Previos en Keycloak

Para que esto funcione, **DEBES tener los roles creados** en el realm de Keycloak:

1. Ir a Keycloak Admin Console
2. Seleccionar realm `turn-app`
3. Ir a **Realm roles**
4. Crear los siguientes roles:
   - `ADMIN`
   - `SUPERVISOR`
   - `EMPLEADO`

### Pasos para crear roles

1. **Acceder a Keycloak**:

   ```text
   http://localhost:9090/admin/master/console/#/turn-app/roles
   ```

2. **Crear rol ADMIN**:
   - Click en "Create role"
   - Role name: `ADMIN`
   - Description: "Administrador del sistema"
   - Click "Save"

3. **Crear rol SUPERVISOR**:
   - Click en "Create role"
   - Role name: `SUPERVISOR`
   - Description: "Supervisor de empleados"
   - Click "Save"

4. **Crear rol EMPLEADO**:
   - Click en "Create role"
   - Role name: `EMPLEADO`
   - Description: "Empleado estándar"
   - Click "Save"

---

## Verificación

### Verificar rol asignado en Keycloak

1. Crear un usuario:

   ```bash
   POST http://localhost:8080/usuarios
   {
     "email": "test-rol@example.com",
     "firstName": "Test",
     "lastName": "Rol",
     "password": "Test1234!",
     "codigoEmpleado": "ROL-001",
     "cargo": "Tester",
     "fechaContratacion": "2025-01-01",
     "rolApp": "SUPERVISOR",
     "numeroIdentificacion": "ROL123",
     "telefono": "555-0001"
   }
   ```

2. Ir a Keycloak Admin Console:

   ```text
   http://localhost:9090/admin/master/console/#/turn-app/users
   ```

3. Buscar el usuario `test-rol@example.com`

4. Click en el usuario → Tab "Role mapping"

5. Verificar que el rol `SUPERVISOR` aparece en "Assigned roles"

---

### Verificar actualización de rol

1. Actualizar el rol:

   ```bash
   PUT http://localhost:8080/usuarios/{id}
   {
     "rolApp": "ADMIN"
   }
   ```

2. Volver a Keycloak → Usuario → Role mapping

3. Verificar que:
   - ✅ `SUPERVISOR` ya NO aparece
   - ✅ `ADMIN` SÍ aparece

---

## Logs de Seguimiento

### Al crear usuario

```log
INFO  c.t.m.u.s.UsuarioServiceImpl : Usuario creado en Keycloak con ID: abc-123-...
DEBUG c.t.m.u.s.UsuarioServiceImpl : Rol SUPERVISOR asignado exitosamente al usuario abc-123-...
INFO  c.t.m.u.s.UsuarioServiceImpl : Rol SUPERVISOR asignado al usuario en Keycloak
INFO  c.t.m.u.s.UsuarioServiceImpl : Usuario guardado en BD local con ID: 456-def-...
```

### Al actualizar rol

```log
INFO  c.t.m.u.s.UsuarioServiceImpl : Actualizando usuario con ID: 456-def-...
DEBUG c.t.m.u.s.UsuarioServiceImpl : Rol EMPLEADO removido exitosamente del usuario abc-123-...
DEBUG c.t.m.u.s.UsuarioServiceImpl : Rol ADMIN asignado exitosamente al usuario abc-123-...
INFO  c.t.m.u.s.UsuarioServiceImpl : Rol actualizado en Keycloak de EMPLEADO a ADMIN
INFO  c.t.m.u.s.UsuarioServiceImpl : Usuario actualizado en BD local: 456-def-...
```

### Error si el rol no existe en Keycloak

```log
ERROR c.t.m.u.s.UsuarioServiceImpl : Error al asignar rol en Keycloak, eliminando usuario...
KeycloakOperationException: Rol no encontrado en Keycloak: MANAGER. Asegúrate de que el rol existe en el realm.
```

---

## Manejo de Errores

### Rol no existe en Keycloak

**Error**:

```json
{
  "success": false,
  "message": "Rol no encontrado en Keycloak: MANAGER. Asegúrate de que el rol existe en el realm.",
  "timestamp": "2025-10-22T16:00:00"
}
```

**Solución**: Crear el rol en Keycloak Admin Console

---

### Error al asignar rol

**Error**:

```json
{
  "success": false,
  "message": "Error al asignar rol ADMIN al usuario en Keycloak",
  "timestamp": "2025-10-22T16:00:00"
}
```

**Solución**: Verificar:

1. Keycloak está funcionando
2. El cliente admin tiene los permisos correctos
3. El rol existe en el realm

---

## Rollback en Caso de Error

Si falla la asignación del rol al crear un usuario:

1. ✅ El usuario se **elimina de Keycloak**
2. ✅ No se guarda en la BD
3. ✅ Se lanza `KeycloakOperationException`

Esto garantiza que no queden usuarios sin rol en Keycloak.

---

## Resumen de Cambios

### Archivos Modificados

1. **UsuarioServiceImpl.java**:
   - ✅ Import `RoleRepresentation`
   - ✅ Método `asignarRolKeycloak()`
   - ✅ Método `removerRolKeycloak()`
   - ✅ Actualizado `registrarUsuario()` para asignar rol
   - ✅ Actualizado `actualizarUsuario()` para cambiar rol

### Nuevos Comportamientos

1. **Crear usuario**: Asigna rol en Keycloak
2. **Actualizar usuario**: Actualiza rol en Keycloak si cambió
3. **Rollback**: Elimina usuario de Keycloak si falla asignar rol

---

## Checklist de Validación

- [ ] Roles creados en Keycloak (ADMIN, SUPERVISOR, EMPLEADO)
- [ ] Usuario creado tiene rol asignado en Keycloak
- [ ] Actualizar rol cambia el rol en Keycloak
- [ ] Si el rol no existe, se muestra error claro
- [ ] Los logs muestran la asignación de roles
- [ ] Rollback funciona si falla la asignación

---

## Próximos Pasos

✅ **Implementado**: Asignación y actualización de roles  
🔄 **Próximo**: Pruebas de integración  
📋 **Futuro**: Endpoint para listar roles asignados a un usuario
