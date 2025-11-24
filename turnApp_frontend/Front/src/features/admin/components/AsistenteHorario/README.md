# AsistenteHorarioModal - Componentes

Esta carpeta contiene los componentes descompuestos del `AsistenteHorarioModal`, organizado de manera modular y reutilizable.

## 📁 Estructura

```text
AsistenteHorario/
├── index.js                      # Exportaciones centralizadas
├── StepIndicator.jsx             # Indicador de pasos (1-2-3)
├── Paso1InfoBasica.jsx           # Formulario de información básica
├── Paso2AsignarTurnos.jsx        # Gestión de asignaciones de turnos
├── Paso3Revision.jsx             # Vista de revisión final
├── TablaAsignaciones.jsx         # Tabla de fechas y turnos
├── ModalSeleccionEmpleado.jsx    # Modal para seleccionar empleados
└── ModoAsignacionSelector.jsx    # Selector de modo de asignación
```

## 🔧 Componentes

### StepIndicator

Muestra el progreso del asistente en 3 pasos con indicadores visuales.

**Props:**

- `currentStep` (number): Paso actual (1-3)
- `totalSteps` (number): Total de pasos

### Paso1InfoBasica

Formulario para ingresar datos básicos del horario.

**Props:**

- `register`: Función de react-hook-form
- `errors`: Objeto de errores del formulario

### Paso2AsignarTurnos

Interfaz para asignar empleados a turnos en fechas específicas.

**Props:**

- `fechaInicio` (string): Fecha de inicio del período
- `fechaFin` (string): Fecha de fin del período
- `turnos` (array): Lista de turnos disponibles
- `asignaciones` (array): Lista de asignaciones actuales
- `onAgregarAsignaciones` (function): Callback para agregar asignaciones
- `onEliminarAsignacion` (function): Callback para eliminar asignación
- `loadingUsuarios` (boolean): Estado de carga
- `setLoadingUsuarios` (function): Setter del estado de carga

### Paso3Revision

Vista final con resumen de toda la información antes de crear el horario.

**Props:**

- `datosHorario` (object): Datos del formulario
- `asignaciones` (array): Lista de asignaciones creadas

### TablaAsignaciones

Tabla interactiva mostrando todas las fechas, turnos y asignaciones.

**Props:**

- `fechas` (array): Lista de fechas del período
- `turnos` (array): Lista de turnos
- `asignaciones` (array): Lista de asignaciones
- `onAbrirModal` (function): Callback para abrir modal de selección
- `onEliminarAsignacion` (function): Callback para eliminar asignación

### ModalSeleccionEmpleado

Modal para seleccionar empleados con diferentes modos de asignación.

**Props:**

- `mostrar` (boolean): Controla la visibilidad
- `onCerrar` (function): Callback al cerrar
- `fechaSeleccionada` (string): Fecha actual
- `turnoSeleccionado` (object): Turno actual
- `usuariosDisponibles` (array): Lista de usuarios disponibles
- `loadingUsuarios` (boolean): Estado de carga
- `onAsignarUsuario` (function): Callback al asignar usuario
- `modoAsignacion` (string): Modo actual de asignación
- `setModoAsignacion` (function): Setter del modo
- `fechas` (array): Lista de fechas del período
- `diasSeleccionados` (array): Días seleccionados en modo personalizado
- `setDiasSeleccionados` (function): Setter de días seleccionados

### ModoAsignacionSelector

Selector de modo de asignación (individual, período, semana, etc.).

**Props:**

- `modoAsignacion` (string): Modo actual
- `setModoAsignacion` (function): Setter del modo
- `fechas` (array): Lista de fechas
- `fechaSeleccionada` (string): Fecha actual
- `diasSeleccionados` (array): Días seleccionados
- `setDiasSeleccionados` (function): Setter de días

## 🎯 Hooks Personalizados

### useAsignaciones

Hook para manejar el estado y operaciones de asignaciones.

**Retorna:**

- `asignaciones`: Array de asignaciones
- `setAsignaciones`: Setter directo
- `agregarAsignaciones`: Agregar múltiples asignaciones
- `eliminarAsignacion`: Eliminar por ID
- `obtenerAsignacionesPorFechaTurno`: Filtrar por fecha y turno
- `obtenerAsignacionesPorEmpleado`: Agrupar por empleado
- `contarAsignacionesFueraPreferencia`: Contar alertas

### useFechas

Hook para generar lista de fechas del período.

**Parámetros:**

- `fechaInicio` (string)
- `fechaFin` (string)

**Retorna:** Array de fechas (strings ISO)

## 💡 Ventajas de la Componentización

1. **Separación de responsabilidades**: Cada componente tiene un propósito específico
2. **Reutilización**: Componentes como `StepIndicator` pueden usarse en otros asistentes
3. **Mantenibilidad**: Más fácil encontrar y modificar código específico
4. **Testeo**: Componentes más pequeños son más fáciles de testear
5. **Legibilidad**: Código más claro y organizado
6. **Colaboración**: Múltiples desarrolladores pueden trabajar en diferentes componentes

## 🚀 Uso

```jsx
import AsistenteHorarioModal from './AsistenteHorarioModal';

function MiComponente() {
  return (
    <AsistenteHorarioModal 
      onClose={() => console.log('Cerrado')}
      onSuccess={() => console.log('Horario creado')}
    />
  );
}
```

## 📝 Notas

- Todos los componentes usan Tailwind CSS para estilos
- Los iconos provienen de `react-icons/fi`
- La validación del formulario usa `react-hook-form` + `zod`
- Los servicios se importan desde `../../../services/`
