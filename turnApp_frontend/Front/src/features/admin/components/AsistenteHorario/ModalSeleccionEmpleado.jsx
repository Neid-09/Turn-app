import { FiX, FiAlertCircle } from 'react-icons/fi';
import ModoAsignacionSelector from './ModoAsignacionSelector';

export default function ModalSeleccionEmpleado({
  mostrar,
  onCerrar,
  fechaSeleccionada,
  turnoSeleccionado,
  usuariosDisponibles,
  loadingUsuarios,
  onAsignarUsuario,
  modoAsignacion,
  setModoAsignacion,
  fechas,
  diasSeleccionados,
  setDiasSeleccionados
}) {
  if (!mostrar) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[80vh] overflow-y-auto">
        <div className="sticky top-0 bg-white border-b px-6 py-4 flex items-center justify-between">
          <div>
            <h3 className="text-lg font-bold text-gray-900">Asignar Empleado</h3>
            <p className="text-sm text-gray-500">
              {new Date(fechaSeleccionada + 'T00:00:00').toLocaleDateString('es-ES')} - {turnoSeleccionado?.nombre} ({turnoSeleccionado?.horaInicio} - {turnoSeleccionado?.horaFin})
            </p>
          </div>
          <button
            onClick={onCerrar}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <FiX className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        <div className="p-6">
          <ModoAsignacionSelector
            modoAsignacion={modoAsignacion}
            setModoAsignacion={setModoAsignacion}
            fechas={fechas}
            fechaSeleccionada={fechaSeleccionada}
            diasSeleccionados={diasSeleccionados}
            setDiasSeleccionados={setDiasSeleccionados}
          />

          {loadingUsuarios ? (
            <div className="py-12 text-center">
              <div className="inline-block w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
              <p className="text-gray-500 mt-4">Cargando empleados disponibles...</p>
            </div>
          ) : usuariosDisponibles.length === 0 ? (
            <div className="py-12 text-center text-gray-500">
              <FiAlertCircle className="w-12 h-12 mx-auto mb-4 text-gray-400" />
              <p>No hay empleados disponibles para este turno</p>
            </div>
          ) : (
            <>
              <h4 className="text-sm font-semibold text-gray-700 mb-3">Seleccionar Empleado</h4>
              <div className="space-y-2">
                {usuariosDisponibles.map(usuario => (
                  <button
                    key={usuario.keycloakId}
                    type="button"
                    onClick={() => onAsignarUsuario(usuario)}
                    className="w-full flex items-center gap-3 p-4 border border-gray-200 rounded-lg hover:bg-purple-50 hover:border-purple-300 transition-colors text-left"
                  >
                    <div className="flex-1">
                      <p className="font-medium text-gray-900">{usuario.nombreCompleto}</p>
                      <p className="text-sm text-gray-500">{usuario.codigoEmpleado}</p>
                    </div>
                    {usuario.tienePreferencias && (
                      <div className="text-right">
                        {usuario.cumplePreferencias ? (
                          <span className="text-xs px-2 py-1 bg-green-100 text-green-700 rounded">
                            ✓ En preferencia
                          </span>
                        ) : (
                          <span className="text-xs px-2 py-1 bg-yellow-100 text-yellow-700 rounded">
                            ⚠️ Fuera de preferencia
                          </span>
                        )}
                      </div>
                    )}
                    {usuario.rolApp && (
                      <span className="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded">
                        {usuario.rolApp}
                      </span>
                    )}
                  </button>
                ))}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
