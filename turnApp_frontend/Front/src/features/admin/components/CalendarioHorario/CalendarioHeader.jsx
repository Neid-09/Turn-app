import moment from 'moment';
import { FiX, FiSend } from 'react-icons/fi';

/**
 * Header del modal de calendario
 * @param {Object} props
 * @param {Object} props.horario - Datos del horario
 * @param {boolean} props.puedePublicar - Indica si se puede publicar
 * @param {Function} props.onPublicar - Callback al publicar
 * @param {Function} props.onClose - Callback al cerrar
 */
export default function CalendarioHeader({ horario, puedePublicar, onPublicar, onClose }) {
  return (
    <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
      <div>
        <h2 className="text-xl font-bold text-gray-900">{horario.nombre}</h2>
        <p className="text-sm text-gray-500 mt-1">
          {moment(horario.fechaInicio).format('DD/MM/YYYY')} - {moment(horario.fechaFin).format('DD/MM/YYYY')}
        </p>
      </div>
      <div className="flex items-center gap-2">
        {puedePublicar && (
          <button
            onClick={onPublicar}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium transition-colors"
          >
            <FiSend className="w-4 h-4" />
            Publicar
          </button>
        )}
        <button
          onClick={onClose}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          aria-label="Cerrar"
        >
          <FiX className="w-6 h-6 text-gray-500" />
        </button>
      </div>
    </div>
  );
}
