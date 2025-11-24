import { FiX, FiClock, FiUser } from 'react-icons/fi';
import { parseLocalDate } from '../CalendarioHorario/utils/calendarHelpers';
import { ESTADO_CONFIG } from './constants';

/**
 * Modal de detalles de asignaciones
 */
export default function DetallesAsignacionesModal({ asignaciones, fecha, turno, onClose }) {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" style={{ zIndex: 60 }}>
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[80vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-bold text-gray-900">{turno}</h3>
              <p className="text-sm text-gray-500 flex items-center gap-2 mt-1">
                <FiClock className="w-4 h-4" />
                {parseLocalDate(fecha).toLocaleDateString('es-ES', { 
                  weekday: 'long', 
                  day: 'numeric', 
                  month: 'long', 
                  year: 'numeric' 
                })}
                {asignaciones[0]?.horaInicio && asignaciones[0]?.horaFin && (
                  <span>• {asignaciones[0].horaInicio.slice(0, 5)} - {asignaciones[0].horaFin.slice(0, 5)}</span>
                )}
              </p>
            </div>
            <button
              onClick={onClose}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <FiX className="w-5 h-5 text-gray-500" />
            </button>
          </div>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto p-6">
          <div className="mb-4">
            <div className="flex items-center justify-between mb-3">
              <h4 className="font-semibold text-gray-900">Asignaciones</h4>
              <span className="text-sm font-medium text-blue-600">
                {asignaciones.length} {asignaciones.length === 1 ? 'asignación' : 'asignaciones'}
              </span>
            </div>
          </div>

          <div className="space-y-3">
            {asignaciones.map((asignacion) => {
              const config = ESTADO_CONFIG[asignacion.estado] || ESTADO_CONFIG.PLANIFICADO;
              
              return (
                <div 
                  key={asignacion.id} 
                  className="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <FiUser className="w-4 h-4 text-gray-400" />
                        <span className="font-medium text-gray-900">
                          {asignacion.nombreUsuario || `Usuario ID: ${asignacion.usuarioId.slice(0, 8)}...`}
                        </span>
                      </div>
                      
                      <div className="flex items-center gap-2 text-sm text-gray-600 mb-1">
                        <FiClock className="w-3.5 h-3.5" />
                        <span>{asignacion.horaInicio.slice(0, 5)} - {asignacion.horaFin.slice(0, 5)}</span>
                      </div>

                      {asignacion.observaciones && (
                        <p className="text-sm text-gray-500 mt-2 italic">
                          {asignacion.observaciones}
                        </p>
                      )}
                    </div>

                    <div className="flex flex-col items-end gap-2">
                      <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium ${config.color}`}>
                        <span>{config.icon}</span>
                        {config.label}
                      </span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50">
          <button
            onClick={onClose}
            className="w-full px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors"
          >
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
}
