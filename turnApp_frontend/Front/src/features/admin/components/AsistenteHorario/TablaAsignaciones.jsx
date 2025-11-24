import { FiPlus, FiTrash2, FiAlertCircle } from 'react-icons/fi';

export default function TablaAsignaciones({ 
  fechas, 
  turnos, 
  asignaciones, 
  onAbrirModal,
  onEliminarAsignacion 
}) {
  const obtenerAsignacionesPorFechaTurno = (fecha, turnoId) => {
    return asignaciones.filter(a => a.fecha === fecha && a.turnoId === turnoId);
  };

  return (
    <div className="border border-gray-200 rounded-lg overflow-hidden">
      <div className="max-h-[500px] overflow-y-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 sticky top-0">
            <tr>
              <th className="px-4 py-3 text-left font-medium text-gray-700">Fecha</th>
              {turnos.map(turno => (
                <th key={turno.id} className="px-4 py-3 text-center font-medium text-gray-700">
                  <div>{turno.nombre}</div>
                  <div className="text-xs text-gray-500 font-normal">
                    {turno.horaInicio} - {turno.horaFin}
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {fechas.map(fecha => (
              <tr key={fecha} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium text-gray-900 whitespace-nowrap">
                  {new Date(fecha + 'T00:00:00').toLocaleDateString('es-ES', {
                    weekday: 'short',
                    day: '2-digit',
                    month: 'short'
                  })}
                </td>
                {turnos.map(turno => {
                  const asignacionesCelda = obtenerAsignacionesPorFechaTurno(fecha, turno.id);
                  return (
                    <td key={turno.id} className="px-2 py-2 text-center">
                      {asignacionesCelda.length > 0 ? (
                        <div className="space-y-1">
                          {asignacionesCelda.map(asig => (
                            <div
                              key={asig.id}
                              className="flex items-center gap-1 bg-green-50 border border-green-200 rounded px-2 py-1"
                            >
                              <span className="text-xs text-green-900 flex-1 truncate">
                                {asig.nombreEmpleado}
                              </span>
                              {!asig.cumplePreferencias && asig.tienePreferencias && (
                                <FiAlertCircle className="w-3 h-3 text-yellow-600 shrink-0" />
                              )}
                              <button
                                type="button"
                                onClick={() => onEliminarAsignacion(asig.id)}
                                className="text-red-600 hover:text-red-800 shrink-0"
                              >
                                <FiTrash2 className="w-3 h-3" />
                              </button>
                            </div>
                          ))}
                          <button
                            type="button"
                            onClick={() => onAbrirModal(fecha, turno)}
                            className="w-full px-2 py-1 text-xs text-purple-600 hover:bg-purple-50 rounded transition-colors"
                          >
                            <FiPlus className="w-3 h-3 inline" /> Agregar
                          </button>
                        </div>
                      ) : (
                        <button
                          type="button"
                          onClick={() => onAbrirModal(fecha, turno)}
                          className="w-full px-3 py-2 text-xs text-gray-600 hover:bg-gray-100 border border-dashed border-gray-300 rounded transition-colors"
                        >
                          <FiPlus className="w-4 h-4 inline" />
                        </button>
                      )}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
