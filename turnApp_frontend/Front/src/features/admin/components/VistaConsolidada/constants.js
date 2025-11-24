/**
 * Configuración de estados de asignaciones
 */
export const ESTADO_CONFIG = {
  PLANIFICADO: { 
    color: 'bg-blue-100 text-blue-700 border-blue-200',
    colorHex: '#3B82F6',
    label: 'Planificado', 
    icon: '📋' 
  },
  ASIGNADO: { 
    color: 'bg-green-100 text-green-700 border-green-200',
    colorHex: '#10B981',
    label: 'Asignado', 
    icon: '✅' 
  },
  CONFIRMADO: { 
    color: 'bg-green-100 text-green-700 border-green-200',
    colorHex: '#10B981',
    label: 'Confirmado', 
    icon: '✓' 
  },
  COMPLETADO: { 
    color: 'bg-gray-100 text-gray-700 border-gray-200',
    colorHex: '#6B7280',
    label: 'Completado', 
    icon: '✓✓' 
  },
  CANCELADO: { 
    color: 'bg-red-100 text-red-700 border-red-200',
    colorHex: '#EF4444',
    label: 'Cancelado', 
    icon: '✗' 
  }
};

/**
 * Mensajes del calendario en español
 */
export const CALENDAR_MESSAGES = {
  today: 'Hoy',
  previous: 'Anterior',
  next: 'Siguiente',
  month: 'Mes',
  week: 'Semana',
  day: 'Día',
  agenda: 'Agenda',
  date: 'Fecha',
  time: 'Hora',
  event: 'Evento',
  noEventsInRange: 'No hay asignaciones en este rango',
  showMore: (total) => `+ Ver más (${total})`
};
