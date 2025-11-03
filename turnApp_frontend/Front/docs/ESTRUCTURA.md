# 📁 Estructura del Proyecto - Turn App Frontend

## 🎯 Organización por Funcionalidad

Este proyecto está organizado siguiendo el patrón **Feature-Based Architecture**, separando el código por funcionalidades (Admin y Employee) para mejor mantenibilidad y escalabilidad.

## 📂 Estructura de Carpetas

```text
src/
├── features/                    # Funcionalidades principales
│   ├── admin/                   # Módulo de Administrador
│   │   ├── components/          # Componentes exclusivos de Admin
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── AdminBottomNav.jsx
│   │   │   ├── AdminLayout.jsx
│   │   │   └── index.js        # Exportaciones centralizadas
│   │   └── pages/              # Páginas específicas de Admin
│   │
│   └── employee/               # Módulo de Empleado
│       ├── components/         # Componentes exclusivos de Employee
│       │   ├── EmployeeDashboard.jsx
│       │   ├── BottomNav.jsx
│       │   ├── EmployeeLayout.jsx
│       │   └── index.js       # Exportaciones centralizadas
│       └── pages/             # Páginas específicas de Employee
│           ├── Horario.jsx
│           └── index.js       # Exportaciones centralizadas
│
├── shared/                    # Código compartido entre módulos
│   ├── components/           # Componentes reutilizables
│   │   ├── LoginScreen.jsx
│   │   ├── ProfileScreen.jsx
│   │   ├── ProtectedRoute.jsx
│   │   └── index.js         # Exportaciones centralizadas
│   └── context/             # Contextos globales
│       └── AuthContext.jsx
│
├── App.jsx                  # Configuración de rutas
├── main.jsx                 # Punto de entrada
└── index.css               # Estilos globales
```

## 🔑 Convenciones de Importación

### Importaciones dentro del mismo módulo

```javascript
// En admin/components/AdminLayout.jsx
import AdminBottomNav from './AdminBottomNav';
```

### Importaciones desde shared

```javascript
// En admin/components/AdminLayout.jsx
import { useAuth } from '../../shared/context/AuthContext';
```

### Importaciones en App.jsx

```javascript
import { AuthProvider } from './shared/context/AuthContext';
import { LoginScreen, ProtectedRoute, ProfileScreen } from './shared/components';
import { EmployeeLayout, EmployeeDashboard } from './features/employee/components';
import { AdminLayout, AdminDashboard } from './features/admin/components';
import { Horario } from './features/employee/pages';
```

## 📋 Ventajas de esta Estructura

### ✅ Separación Clara de Responsabilidades

- Cada módulo (Admin/Employee) tiene sus propios componentes
- El código compartido está en `shared/`
- Fácil identificar qué componente pertenece a qué funcionalidad

### ✅ Escalabilidad

- Agregar nuevas funcionalidades es simple: crear nueva carpeta en `features/`
- Los componentes relacionados están agrupados
- Fácil de navegar y mantener

### ✅ Reutilización

- Componentes compartidos en `shared/components`
- Contextos globales en `shared/context`
- Sin duplicación de código

### ✅ Testing Aislado

- Cada feature puede ser testeado independientemente
- Los tests compartidos están separados

## 🚀 Próximos Pasos para Expandir

### Para agregar una nueva página de Admin

1. Crear el componente en `features/admin/pages/NuevaPagina.jsx`
2. Exportarlo en `features/admin/pages/index.js`
3. Agregar la ruta en `App.jsx`

### Para agregar una nueva página de Employee

1. Crear el componente en `features/employee/pages/NuevaPagina.jsx`
2. Exportarlo en `features/employee/pages/index.js`
3. Agregar la ruta en `App.jsx`

### Para agregar un componente compartido

1. Crear el componente en `shared/components/NuevoComponente.jsx`
2. Exportarlo en `shared/components/index.js`
3. Importarlo donde se necesite

## 📌 Rutas del Sistema

### Rutas Públicas

- `/login` - Pantalla de inicio de sesión

### Rutas de Empleado (protegidas)

- `/` - Dashboard del empleado
- `/horario` - Vista de horarios
- `/asistencia` - Registro de asistencia
- `/avisos` - Avisos y notificaciones
- `/perfil` - Perfil del usuario

### Rutas de Admin (protegidas)

- `/admin` - Dashboard administrativo
- `/admin/horarios` - Gestión de horarios
- `/admin/empleados` - Gestión de empleados
- `/admin/solicitudes` - Gestión de solicitudes
- `/admin/avisos` - Gestión de avisos
- `/admin/perfil` - Perfil del administrador

## 🔐 Autenticación (Provisional)

La autenticación está gestionada por `AuthContext` que provee:

- `user` - Usuario actual
- `login()` - Función de inicio de sesión
- `logout()` - Función de cierre de sesión
- `isAuthenticated` - Estado de autenticación
- `isAdmin` - Verifica si el usuario es admin
- `isEmployee` - Verifica si el usuario es empleado

## 📝 Notas Adicionales

- Los componentes de navegación (`BottomNav`, `AdminBottomNav`) son específicos de cada rol
- `ProfileScreen` es compartido pero se adapta al rol del usuario
- `ProtectedRoute` valida el rol del usuario antes de permitir el acceso
- Todos los layouts incluyen padding inferior para evitar que el contenido quede oculto por la barra de navegación fija
