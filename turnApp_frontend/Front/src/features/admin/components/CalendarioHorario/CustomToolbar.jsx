import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';

/**
 * Toolbar personalizado para el calendario
 * @param {Object} props
 * @param {string} props.label - Etiqueta del período actual
 * @param {Function} props.onNavigate - Función de navegación
 * @param {Function} props.onView - Función para cambiar vista
 * @param {string} props.view - Vista actual
 */
export default function CustomToolbar({ label, onNavigate, onView, view }) {
  return (
    <div className="mb-4 flex flex-col sm:flex-row gap-3 items-center justify-between">
      <div className="flex items-center gap-2">
        <button
          onClick={() => onNavigate('PREV')}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          aria-label="Mes anterior"
        >
          <FiChevronLeft className="w-5 h-5 text-gray-600" />
        </button>
        <button
          onClick={() => onNavigate('TODAY')}
          className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
        >
          Hoy
        </button>
        <button
          onClick={() => onNavigate('NEXT')}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          aria-label="Mes siguiente"
        >
          <FiChevronRight className="w-5 h-5 text-gray-600" />
        </button>
      </div>

      <h3 className="text-lg font-bold text-gray-900">{label}</h3>

      <div className="flex gap-2">
        {['day', 'week', 'month', 'agenda'].map((v) => (
          <button
            key={v}
            onClick={() => onView(v)}
            className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
              view === v
                ? 'bg-purple-600 text-white'
                : 'text-gray-700 hover:bg-gray-100'
            }`}
          >
            {v === 'day' && 'Día'}
            {v === 'week' && 'Semana'}
            {v === 'month' && 'Mes'}
            {v === 'agenda' && 'Agenda'}
          </button>
        ))}
      </div>
    </div>
  );
}
