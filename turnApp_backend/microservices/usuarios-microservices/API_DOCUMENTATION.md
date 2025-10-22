# 📋 API de Usuarios - Documentación

## 🔐 Autenticación

Todos los endpoints requieren un token JWT válido de Keycloak en el header:

```text
Authorization: Bearer {token}
```

## 📌 Endpoints Disponibles

### 1. Health Check

**Endpoint:** `GET /usuarios`  
**Descripción:** Verifica que el microservicio está operativo  
**Autenticación:** Token JWT (cualquier rol)  
**Respuesta:**

```json
{
  "message": "Microservicio de usuarios operativo",
  "data": null
}
```

---

### 2. Obtener Usuario Actual

**Endpoint:** `GET /usuarios/me`  
**Descripción:** Retorna la información del usuario autenticado  
**Autenticación:** Token JWT (cualquier rol)  
**Respuesta:**

```json
{
  "id": "uuid-del-usuario",
  "email": "usuario@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "codigoEmpleado": null,
  "cargo": null,
  "numeroIdentificacion": null,
  "telefono": null,
  "enabled": true
}
```

---

### 3. Obtener Roles del Usuario

**Endpoint:** `GET /usuarios/me/roles`  
**Descripción:** Retorna los roles del usuario autenticado  
**Autenticación:** Token JWT (cualquier rol)  
**Respuesta:**

```json
{
  "message": "Roles del usuario obtenidos exitosamente",
  "data": [
    {
      "authority": "ROLE_ADMIN"
    },
    {
      "authority": "SCOPE_openid"
    }
  ]
}
```

---

### 4. Registrar Nuevo Usuario

**Endpoint:** `POST /usuarios/registro`  
**Descripción:** Crea un nuevo usuario en Keycloak y en la base de datos  
**Autenticación:** Token JWT con rol **ADMIN** ⚠️  
**Request Body:**

```json
{
  "email": "nuevo@example.com",
  "firstName": "María",
  "lastName": "González",
  "password": "Password123!",
  "codigoEmpleado": "EMP001",
  "cargo": "Desarrollador",
  "fechaContratacion": "2024-01-15",
  "rolApp": "EMPLEADO",
  "numeroIdentificacion": "1234567890",
  "telefono": "+573001234567"
}
```

**Respuesta Exitosa (201):**

```json
{
  "message": "Usuario creado exitosamente",
  "data": null
}
```

**Respuesta Error de Validación (400):**

```json
{
  "timestamp": "2025-10-22T12:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Error en la validación de datos",
  "errors": {
    "email": "El email debe tener un formato válido",
    "password": "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial"
  },
  "path": "/usuarios/registro"
}
```

**Respuesta Acceso Denegado (403):**

```json
{
  "timestamp": "2025-10-22T12:30:00",
  "status": 403,
  "error": "Access Denied",
  "message": "No tienes permisos suficientes para realizar esta acción",
  "path": "/usuarios/registro"
}
```

---

## 🔑 Roles de Keycloak

| Rol | Constante en Código | Descripción |
|-----|---------------------|-------------|
| `ADMIN` | `RolKeycloak.ADMIN` | Administrador del sistema. Puede registrar usuarios |
| `SUPERVISOR` | `RolKeycloak.SUPERVISOR` | Supervisor. Permisos intermedios |
| `EMPLEADO` | `RolKeycloak.EMPLEADO` | Empleado estándar. Permisos básicos |

---

## ✅ Validaciones del Request de Registro

| Campo | Validación |
|-------|-----------|
| `email` | Obligatorio, formato de email válido |
| `firstName` | Obligatorio, entre 2 y 50 caracteres |
| `lastName` | Obligatorio, entre 2 y 50 caracteres |
| `password` | Obligatorio, mínimo 8 caracteres, debe contener mayúscula, minúscula, número y carácter especial |
| `codigoEmpleado` | Obligatorio |
| `cargo` | Obligatorio |
| `fechaContratacion` | Obligatorio, debe ser fecha pasada |
| `rolApp` | Obligatorio, valores: ADMIN, SUPERVISOR, EMPLEADO |
| `numeroIdentificacion` | Obligatorio |
| `telefono` | Obligatorio, entre 10 y 15 dígitos |

---

## 🔧 Ejemplo de uso con cURL

### Obtener información del usuario

```bash
curl -X GET http://localhost:8080/usuarios-microservices/usuarios/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Registrar un nuevo usuario (requiere rol ADMIN)

```bash
curl -X POST http://localhost:8080/usuarios-microservices/usuarios/registro \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nuevo@example.com",
    "firstName": "María",
    "lastName": "González",
    "password": "Password123!",
    "codigoEmpleado": "EMP001",
    "cargo": "Desarrollador",
    "fechaContratacion": "2024-01-15",
    "rolApp": "EMPLEADO",
    "numeroIdentificacion": "1234567890",
    "telefono": "+573001234567"
  }'
```

---

## 🐛 Manejo de Errores

La API maneja los siguientes tipos de errores:

| Código HTTP | Descripción |
|-------------|-------------|
| 200 | Solicitud exitosa |
| 201 | Recurso creado exitosamente |
| 400 | Error de validación de datos |
| 403 | Acceso denegado (sin permisos) |
| 500 | Error interno del servidor |

Todos los errores retornan un formato consistente con `timestamp`, `status`, `error`, `message` y `path`.
