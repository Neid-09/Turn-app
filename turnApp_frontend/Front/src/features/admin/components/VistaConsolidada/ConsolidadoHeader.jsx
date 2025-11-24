import moment from 'moment';
import { FiX, FiRefreshCw } from 'react-icons/fi';

/**
 * Header del modal de vista consolidada
 */
export default function ConsolidadoHeader({ horario, loading, onRefresh, onClose }) {
  return (
    <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
      <div>
        <h2 className="text-xl font-bold text-gray-900">Vista Consolidada</h2>
        <p className="text-sm text-gray-500 mt-1">
          {horario.nombre} • {moment(horario.fechaInicio).format('DD/MM/YYYY')} -{' '}
          {moment(horario.fechaFin).format('DD/MM/YYYY')}
        </p>
      </div>
      <div className="flex items-center gap-2">
        <button
          onClick={onRefresh}
          disabled={loading}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-blue-700 bg-blue-50 hover:bg-blue-100 disabled:bg-blue-50 disabled:opacity-50 rounded-lg transition-colors"
        >
          <FiRefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          Actualizar
        </button>
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
