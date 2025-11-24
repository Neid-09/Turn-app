# CalendarioHorario - Componentes

Esta carpeta contiene los componentes descompuestos del `CalendarioHorarioModal`, organizado de manera modular y reutilizable.

## 📁 Estructura

```text
CalendarioHorario/
├── index.js                      # Exportaciones centralizadas
├── CalendarioHorarioModal.jsx    # Componente principal del calendario
├── CustomToolbar.jsx             # Toolbar personalizado del calendario
├── CalendarioHeader.jsx          # Header del modal con acciones
├── CalendarioFooter.jsx          # Footer con estadísticas
├── AgregarDetalleModal.jsx       # Modal para agregar turnos
├── DetallesTurnoModal.jsx        # Modal para ver detalles de turno
├── EmpleadoAsignadoCard.jsx      # Tarjeta de empleado asignado
└── utils/
    └── calendarHelpers.js        # Funciones helper del calendario
```

## 🔧 Componentes

### CalendarioHorarioModal

Componente principal que orquesta todo el calendario de horarios.

**Props:**

- `horario` (Object): Datos del horario a mostrar
- `modoVista` (string): Modo de vista ('ver' | 'editar'). Default: 'ver'
- `onClose` (Function): Callback al cerrar el modal
- `onPublicar` (Function): Callback al publicar el horario

**Ejemplo:**

```jsx
<CalendarioHorarioModal
  horario={horarioData}
  modoVista="editar"
  onClose={handleClose}
  onPublicar={handlePublicar}
/>
```

### CustomToolbar

Toolbar personalizado para navegación y cambio de vistas del calendario.

**Props:**

- `label` (string): Etiqueta del período actual
- `onNavigate` (Function): Función de navegación (PREV, TODAY, NEXT)
- `onView` (Function): Función para cambiar vista
- `view` (string): Vista actual ('day', 'week', 'month', 'agenda')

### CalendarioHeader

Header del modal con título, fechas y botones de acción.

**Props:**

- `horario` (Object): Datos del horario
- `puedePublicar` (boolean): Indica si se puede publicar
- `onPublicar` (Function): Callback al publicar
- `onClose` (Function): Callback al cerrar

### CalendarioFooter

Footer con estadísticas y ayudas contextuales.

**Props:**

- `totalDetalles` (number): Total de turnos asignados
- `soloLectura` (boolean): Indica si está en modo solo lectura

### AgregarDetalleModal

Modal para agregar un nuevo detalle de turno en una fecha específica.

**Props:**

- `fecha` (string): Fecha seleccionada (formato ISO)
- `horarioId` (number): ID del horario
- `onClose` (Function): Callback al cerrar
- `onSuccess` (Function): Callback al guardar exitosamente

**Funcionalidad:**

- Carga empleados y turnos disponibles
- Validación de campos requeridos
- Manejo de estado de carga y envío
- Permite agregar observaciones opcionales

### DetallesTurnoModal

Modal para visualizar los detalles de un turno específico con empleados asignados.

**Props:**

- `turnoData` (Object): Datos del turno con detalles
- `horarioId` (number): ID del horario
- `soloLectura` (boolean): Modo solo lectura. Default: false
- `onClose` (Function): Callback al cerrar
- `onEliminar` (Function): Callback al eliminar un detalle
- `confirm` (Function): Función de confirmación de alertas
- `success` (Function): Función de éxito de alertas
- `showError` (Function): Función de error de alertas

**Funcionalidad:**

- Muestra lista de empleados asignados
- Permite eliminar asignaciones (si no es solo lectura)
- Confirmación antes de eliminar
- Muestra observaciones de cada asignación

### EmpleadoAsignadoCard

Tarjeta individual de empleado asignado con opción de eliminar.

**Props:**

- `detalle` (Object): Datos del detalle con información del empleado
- `soloLectura` (boolean): Indica si está en modo solo lectura
- `onEliminar` (Function): Callback al eliminar (recibe detalleId y nombreEmpleado)

**Características:**

- Muestra nombre del empleado
- Muestra observaciones si existen
- Botón de eliminar (solo si no es lectura)
- Estados hover interactivos

## 🛠️ Utilidades

### calendarHelpers.js

Funciones helper para el manejo del calendario.

**Funciones exportadas:**

#### `parseLocalDate(dateString)`

Parsea una fecha ISO sin conversión de zona horaria.

```javascript
const fecha = parseLocalDate('2024-01-15T00:00:00');
```

#### `generarColorPorTurno(turnoId)`

Genera un color consistente basado en el ID del turno.

```javascript
const color = generarColorPorTurno(turnoId); // '#8B5CF6'
```

#### `agruparDetallesPorFechaTurno(detalles)`

Agrupa detalles por combinación de fecha y turno.

```javascript
const grupos = agruparDetallesPorFechaTurno(detalles);
// { '2024-01-15-1': { fecha, turnoId, nombreTurno, detalles: [...] } }
```

#### `convertirGruposAEventos(gruposPorFechaTurno, turnos)`

Convierte grupos de detalles a eventos del calendario.

```javascript
const eventos = convertirGruposAEventos(grupos, turnos);
```

## 📦 Dependencias

- `react-big-calendar`: Componente de calendario
- `moment`: Manejo de fechas
- `react-icons/fi`: Iconos de Feather
- Servicios: `horarioService`, `turnoService`, `usuarioService`
- Hooks: `useAlert`
- Componentes compartidos: `AlertDialog`, `ReportePublicacionModal`

## 🎨 Estilos

Los componentes utilizan Tailwind CSS para estilos consistentes:

- Purple (`purple-600`) como color primario
- Estados hover y transiciones suaves
- Diseño responsive con flexbox y grid
- Elevaciones con sombras (`shadow-xl`)

## 🚀 Uso

### Importación básica

```javascript
import { CalendarioHorarioModal } from '@/features/admin/components/CalendarioHorario';
```

### Importación de componentes individuales

```javascript
import { 
  CustomToolbar, 
  AgregarDetalleModal,
  DetallesTurnoModal 
} from '@/features/admin/components/CalendarioHorario';
```

### Importación de utilidades

```javascript
import { parseLocalDate, generarColorPorTurno } from '@/features/admin/components/CalendarioHorario';
```

## 🔄 Flujo de Datos

1. **Carga inicial**: `CalendarioHorarioModal` carga horario y turnos
2. **Eventos del calendario**: Se generan a partir de detalles agrupados
3. **Selección de fecha**: Abre `AgregarDetalleModal`
4. **Selección de evento**: Abre `DetallesTurnoModal`
5. **Publicación**: Genera reporte y muestra `ReportePublicacionModal`

## 🧪 Testing

Cada componente puede ser testeado de forma independiente:

```javascript
import { render, screen } from '@testing-library/react';
import EmpleadoAsignadoCard from './EmpleadoAsignadoCard';

test('muestra nombre del empleado', () => {
  const detalle = { 
    id: 1, 
    nombreEmpleado: 'Juan Pérez',
    usuarioId: 'abc123' 
  };
  render(<EmpleadoAsignadoCard detalle={detalle} soloLectura={false} />);
  expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
});
```

## 📝 Notas

- Los componentes usan `z-index: 50` y `60` para modales anidados
- Las fechas se manejan sin conversión de zona horaria para consistencia
- Los colores de turnos son generados de forma determinística
- El modo solo lectura desactiva todas las acciones de modificación
