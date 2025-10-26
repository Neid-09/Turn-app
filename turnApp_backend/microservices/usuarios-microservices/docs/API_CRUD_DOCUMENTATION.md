# 📚 API de Usuarios - CRUD Completo

Ver archivo `API_CRUD_DOCUMENTATION.md` para la documentación completa de todos los endpoints.

## 🎯 Resumen de Endpoints

### Públicos (Autenticados)

- `GET /usuarios` - Health check
- `GET /usuarios/me` - Información del usuario actual
- `GET /usuarios/me/roles` - Roles del usuario actual

### CRUD (Solo ADMIN)

- `POST /usuarios` - Crear usuario
- `GET /usuarios/{id}` - Obtener por ID
- `GET /usuarios/keycloak/{keycloakId}` - Obtener por Keycloak ID
- `GET /usuarios/email/{email}` - Obtener por email
- `GET /usuarios/listado` - Listar (paginado)
- `GET /usuarios/todos` - Listar todos
- `GET /usuarios/buscar` - Buscar usuarios
- `PUT /usuarios/{id}` - Actualizar usuario
- `PATCH /usuarios/{id}/password` - Cambiar contraseña
- `PATCH /usuarios/{id}/estado` - Habilitar/Deshabilitar
- `DELETE /usuarios/{id}` - Eliminar usuario

## 🔧 Mejoras Implementadas

✅ CRUD completo con todas las operaciones
✅ Paginación y ordenamiento
✅ Búsqueda por múltiples criterios
✅ Manejo de excepciones personalizado
✅ Validaciones exhaustivas
✅ Transaccionalidad con rollback
✅ Sincronización Keycloak-BD
✅ Logging detallado
✅ Documentación completa
