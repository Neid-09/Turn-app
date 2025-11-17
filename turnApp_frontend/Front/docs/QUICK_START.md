# 🎯 Guía de Inicio Rápido - Turn-App

## 🚀 Iniciar la aplicación

```bash
cd turnApp_frontend/Front
npm install
npm run dev
```

## 🔑 Credenciales de Prueba

### 👨‍💼 Administrador

```text
Email: admin@turnapp.com
Contraseña: admin123
**Acceso a**: Dashboard de administrador con opciones de gestión
```

### 👤 Empleado

```text
Email: empleado@turnapp.com
Contraseña: empleado123
**Acceso a**: Dashboard de empleado, horarios y perfil
```

## 📱 Funcionalidades por Rol

### Empleado (`/`)

- ✅ Ver dashboard personal
- ✅ Gestionar horarios (`/horario`)
- ✅ Ver y editar perfil (`/perfil`)
- ✅ Cerrar sesión

### Administrador (`/admin`)

- ✅ Ver dashboard administrativo
- ✅ Gestionar sistema
- ✅ Acceso completo a todas las funciones

## 🔐 Sistema de Seguridad

### ✨ Características Implementadas

1. **Punto único de entrada**: Solo a través de `/login`
2. **Rutas protegidas**: Todas las páginas requieren autenticación
3. **Control de roles**: Cada usuario solo accede a su área
4. **Persistencia de sesión**: La sesión se mantiene al recargar
5. **Redirección automática**: El sistema te lleva a tu dashboard correcto
6. **Logout seguro**: Limpia completamente la sesión

### 🛡️ Protecciones Activas

- ❌ No puedes acceder a rutas sin autenticarte
- ❌ No puedes acceder al área de admin siendo empleado
- ❌ No puedes acceder al área de empleado siendo admin
- ✅ Si intentas acceder al login estando autenticado, te redirige a tu dashboard
- ✅ Si intentas una ruta no autorizada, te redirige a tu área

## 🎨 Características de UI

### Pantalla de Login

- Formulario con validación
- Mensajes de error claros
- Botones de acceso rápido para usuarios demo
- Animaciones suaves
- Diseño responsive

### Pantalla de Perfil

- Avatar personalizado con inicial del nombre
- Información del usuario en tiempo real
- Botón de cerrar sesión visible
- Estado "ACTIVO" en tiempo real
- Modo de edición (preparado para futuras funciones)

## 📂 Estructura de Archivos

```text
src/
├── context/
│   └── AuthContext.jsx          # ⭐ Contexto de autenticación
├── components/
│   ├── LoginScreen.jsx          # 🔑 Pantalla de login
│   ├── ProtectedRoute.jsx       # 🛡️ Protección de rutas
│   ├── ProfileScreen.jsx        # 👤 Perfil de usuario
│   ├── EmployeeLayout.jsx       # 📱 Layout empleado
│   ├── EmployeeDashboard.jsx    # 📊 Dashboard empleado
│   ├── AdminLayout.jsx          # 📱 Layout admin
│   ├── AdminDashboard.jsx       # 📊 Dashboard admin
│   ├── BottomNav.jsx            # 🧭 Nav empleado
│   └── AdminBottomNav.jsx       # 🧭 Nav admin
├── pages/
│   └── Horario.jsx              # 📅 Gestión de horarios
├── App.jsx                      # 🎯 Router principal
└── main.jsx                     # 🚀 Punto de entrada
```

## 🧪 Probar el Sistema

### 1. Prueba de Login

1. Inicia la app
2. Deberías ver solo la pantalla de login
3. Intenta acceder a `/` o `/admin` → te redirige al login

### 2. Prueba como Empleado

1. Haz clic en "Usuarios de prueba"
2. Selecciona "Empleado"
3. Haz clic en "Iniciar sesión"
4. Deberías estar en el dashboard de empleado
5. Navega a Horario y Perfil
6. Intenta acceder a `/admin` → te redirige a `/`

### 3. Prueba como Admin

1. Cierra sesión desde el perfil
2. Inicia sesión con credenciales de admin
3. Deberías estar en `/admin`
4. Intenta acceder a `/` → te redirige a `/admin`

### 4. Prueba de Persistencia

1. Inicia sesión
2. Recarga la página (F5)
3. Deberías seguir autenticado en tu dashboard

## 🔧 Solución de Problemas Comunes

### ❌ Error: "useAuth must be used within AuthProvider"

**Solución**: Verifica que `AuthProvider` envuelva tu app en `App.jsx`

### ❌ Las rutas no redirigen correctamente

**Solución**: Asegúrate de que `BrowserRouter` esté en `main.jsx`

### ❌ La sesión no persiste

**Solución**: Verifica que localStorage esté habilitado en tu navegador

### ❌ No puedo cerrar sesión

**Solución**: El botón de logout está en la pantalla de perfil

## 📚 Documentación Adicional

- Ver `AUTHENTICATION.md` para detalles técnicos completos
- Ver `README.md` para información general del proyecto

## 🎉 ¡Listo para Usar

Tu aplicación ahora tiene:

- ✅ Sistema de autenticación completo
- ✅ Control de acceso basado en roles
- ✅ Persistencia de sesión
- ✅ UI moderna y responsive
- ✅ Dos usuarios de prueba funcionales
- ✅ Protección completa de rutas

---

**¿Necesitas ayuda?** Consulta la documentación o abre un issue.
