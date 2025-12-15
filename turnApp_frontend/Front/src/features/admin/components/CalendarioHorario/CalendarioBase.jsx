import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'moment/locale/es';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import CustomToolbar from './CustomToolbar';

moment.locale('es');
const localizer = momentLocalizer(moment);

/**
 * Mensajes predeterminados del calendario en español
 */
const DEFAULT_MESSAGES = {
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
  noEventsInRange: 'No hay eventos en este rango',
  showMore: (total) => `+ Ver más (${total})`
};

/**
 * Componente base reutilizable de calendario
 * Encapsula la lógica común de react-big-calendar
 * 
 * @param {Object} props
 * @param {Array} props.eventos - Array de eventos del calendario
 * @param {string} props.view - Vista actual ('day' | 'week' | 'month' | 'agenda')
 * @param {Date} props.date - Fecha actual del calendario
 * @param {Function} props.onView - Callback al cambiar de vista
 * @param {Function} props.onNavigate - Callback al navegar entre fechas
 * @param {Function} props.onSelectEvent - Callback al seleccionar un evento
 * @param {boolean} props.selectable - Si permite seleccionar slots (para edición)
 * @param {Function} props.onSelectSlot - Callback al seleccionar un slot vacío
 * @param {string} props.accentColor - Color de acento ('purple' | 'blue')
 * @param {string|number} props.height - Altura del calendario
 * @param {Object} props.messages - Mensajes personalizados (se mezclan con los default)
 * @param {boolean} props.loading - Estado de carga
 * @param {string} props.loadingMessage - Mensaje durante la carga
 * @param {string} props.loadingColor - Color del spinner ('purple' | 'blue')
 */
export default function CalendarioBase({
  eventos = [],
  view = 'month',
  date,
  onView,
  onNavigate,
  onSelectEvent,
  selectable = false,
  onSelectSlot,
  accentColor = 'purple',
  height = '600px',
  messages = {},
  loading = false,
  loadingMessage = 'Cargando calendario...',
  loadingColor = 'purple'
}) {
  // Combinar mensajes personalizados con los predeterminados
  const calendarMessages = { ...DEFAULT_MESSAGES, ...messages };

  // Determinar clases de color para el spinner
  const spinnerColorClass = loadingColor === 'blue' 
    ? 'border-blue-600' 
    : 'border-purple-600';

  if (loading) {
    return (
      <div className="h-full flex items-center justify-center">
        <div className="text-center">
          <div className={`inline-block w-12 h-12 border-4 ${spinnerColorClass} border-t-transparent rounded-full animate-spin`}></div>
          <p className="text-gray-500 mt-4">{loadingMessage}</p>
        </div>
      </div>
    );
  }

  return (
    <div style={{ height }}>
      <Calendar
        localizer={localizer}
        events={eventos}
        startAccessor="start"
        endAccessor="end"
        view={view}
        onView={onView}
        date={date}
        onNavigate={onNavigate}
        onSelectEvent={onSelectEvent}
        selectable={selectable}
        onSelectSlot={selectable ? onSelectSlot : undefined}
        components={{
          toolbar: (props) => (
            <CustomToolbar 
              {...props} 
              view={view} 
              onView={onView}
              accentColor={accentColor}
            />
          )
        }}
        eventPropGetter={(event) => ({
          style: {
            backgroundColor: event.resource?.color || '#6366F1',
            borderColor: event.resource?.color || '#6366F1',
            color: 'white',
            borderRadius: '6px',
            padding: '2px 6px',
            fontSize: '0.875rem',
            fontWeight: '500',
            cursor: onSelectEvent ? 'pointer' : 'default'
          }
        })}
        messages={calendarMessages}
      />
    </div>
  );
}
