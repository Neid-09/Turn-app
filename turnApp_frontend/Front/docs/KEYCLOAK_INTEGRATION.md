# Integración con Keycloak - Frontend

## 🎯 Cambios Realizados

### 1. Configuración de Keycloak

**Archivo:** `src/config/keycloak.config.js`

- Cliente: `turnapp-frontend-client`
- Realm: `myturnapp-microservices-realm`
- URL: `http://localhost:9091`

### 2. Servicios de API

**Archivos creados:**

- `src/config/api.config.js` - Configuración de endpoints
- `src/services/api.service.js` - Cliente Axios con interceptores JWT
- `src/services/usuario.service.js` - Servicio de usuarios

**Características:**

- ✅ Token JWT automático en cada petición
- ✅ Refresh automático de tokens
- ✅ Manejo de errores 401
- ✅ Timeout configurado (10s)

### 3. AuthContext Actualizado

**Archivo:** `src/shared/context/AuthContext.jsx`

- ✅ Inicialización automática de Keycloak
- ✅ Carga de perfil desde backend
- ✅ Extracción de roles de Keycloak
- ✅ Refresh automático de tokens cada 60s
- ✅ Métodos: `login()`, `logout()`, `updateProfile()`

### 4. LoginScreen Simplificado

**Archivo:** `src/shared/components/LoginScreen.jsx`

- ✅ Botón de login que redirige a Keycloak
- ✅ Sin formularios de credenciales
- ✅ Loading mientras se inicializa
- ✅ Redirección automática después del login

## 🚀 Cómo funciona

1. **Usuario hace clic en "Iniciar sesión"**
2. **Redirige a Keycloak** (página de login)
3. **Usuario ingresa credenciales en Keycloak**
4. **Keycloak redirige de vuelta** con token JWT
5. **App carga perfil del usuario** desde backend
6. **Extrae roles** (ADMIN/EMPLOYEE)
7. **Redirige** a dashboard correspondiente

## 🔐 Flujo de Autenticación

```text
Usuario → LoginScreen → Keycloak Login Page
                              ↓
                         Autentica
                              ↓
                    Redirige con token JWT
                              ↓
                    AuthContext carga perfil
                              ↓
                    Redirige a /admin o /
```
