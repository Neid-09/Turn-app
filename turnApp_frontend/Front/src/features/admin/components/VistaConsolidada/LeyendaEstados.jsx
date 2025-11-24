import { ESTADO_CONFIG } from './constants';

/**
 * Leyenda de estados del calendario
 */
export default function LeyendaEstados() {
  return (
    <div className="mt-6 bg-gray-50 rounded-lg border border-gray-200 p-4">
      <p className="text-sm font-medium text-gray-700 mb-3">Leyenda de Estados</p>
      <div className="grid grid-cols-2 md:grid-cols-5 gap-3 mb-3">
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded" style={{ backgroundColor: ESTADO_CONFIG.PLANIFICADO.colorHex }}></div>
          <span className="text-sm text-gray-600">{ESTADO_CONFIG.PLANIFICADO.label}</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded" style={{ backgroundColor: ESTADO_CONFIG.ASIGNADO.colorHex }}></div>
          <span className="text-sm text-gray-600">Asignado/Confirmado</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded" style={{ backgroundColor: ESTADO_CONFIG.COMPLETADO.colorHex }}></div>
          <span className="text-sm text-gray-600">{ESTADO_CONFIG.COMPLETADO.label}</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded" style={{ backgroundColor: ESTADO_CONFIG.CANCELADO.colorHex }}></div>
          <span className="text-sm text-gray-600">{ESTADO_CONFIG.CANCELADO.label}</span>
        </div>
      </div>
      <p className="text-xs text-gray-500 italic">
        💡 Haz clic en cualquier evento del calendario para ver los detalles de las asignaciones
      </p>
    </div>
  );
}
