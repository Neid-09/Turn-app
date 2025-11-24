import { FiX, FiCheckCircle, FiXCircle, FiAlertCircle } from 'react-icons/fi';

export default function ReportePublicacionModal({ reporte, onClose }) {
  const porcentajeExito = reporte.totalProcesados > 0
    ? Math.round((reporte.totalExitosos / reporte.totalProcesados) * 100)
    : 0;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-60 p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between bg-linear-to-r from-purple-600 to-blue-600 text-white">
          <div>
            <h2 className="text-xl font-bold">Reporte de Publicación</h2>
            <p className="text-sm text-purple-100 mt-1">
              Resultados del proceso de asignación de turnos
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-white/20 rounded-lg transition-colors"
          >
            <FiX className="w-6 h-6" />
          </button>
        </div>

        {/* Resumen */}
        <div className="p-6 border-b border-gray-200 bg-gray-50">
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-white rounded-lg p-4 border border-gray-200">
              <div className="flex items-center gap-2 text-gray-600 mb-1">
                <FiAlertCircle className="w-4 h-4" />
                <p className="text-sm font-medium">Total Procesados</p>
              </div>
              <p className="text-2xl font-bold text-gray-900">{reporte.totalProcesados}</p>
            </div>

            <div className="bg-green-50 rounded-lg p-4 border border-green-200">
              <div className="flex items-center gap-2 text-green-700 mb-1">
                <FiCheckCircle className="w-4 h-4" />
                <p className="text-sm font-medium">Exitosos</p>
              </div>
              <p className="text-2xl font-bold text-green-700">{reporte.totalExitosos}</p>
            </div>

            <div className="bg-red-50 rounded-lg p-4 border border-red-200">
              <div className="flex items-center gap-2 text-red-700 mb-1">
                <FiXCircle className="w-4 h-4" />
                <p className="text-sm font-medium">Errores</p>
              </div>
              <p className="text-2xl font-bold text-red-700">{reporte.totalErrores}</p>
            </div>
          </div>

          {/* Barra de progreso */}
          <div className="mt-4">
            <div className="flex items-center justify-between text-sm mb-2">
              <span className="font-medium text-gray-700">Tasa de éxito</span>
              <span className="font-bold text-gray-900">{porcentajeExito}%</span>
            </div>
            <div className="h-3 bg-gray-200 rounded-full overflow-hidden">
              <div
                className={`h-full transition-all ${
                  porcentajeExito === 100
                    ? 'bg-green-500'
                    : porcentajeExito >= 70
                    ? 'bg-yellow-500'
                    : 'bg-red-500'
                }`}
                style={{ width: `${porcentajeExito}%` }}
              ></div>
            </div>
          </div>
        </div>

        {/* Detalles */}
        <div className="flex-1 overflow-auto p-6">
          <h3 className="text-lg font-bold text-gray-900 mb-4">Detalles de Asignaciones</h3>

          {reporte.detalles && reporte.detalles.length > 0 ? (
            <div className="space-y-2">
              {reporte.detalles.map((detalle, index) => (
                <div
                  key={index}
                  className={`p-4 rounded-lg border ${
                    detalle.exitoso
                      ? 'bg-green-50 border-green-200'
                      : 'bg-red-50 border-red-200'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <div className="shrink-0 mt-0.5">
                      {detalle.exitoso ? (
                        <FiCheckCircle className="w-5 h-5 text-green-600" />
                      ) : (
                        <FiXCircle className="w-5 h-5 text-red-600" />
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex-1">
                          <p className="font-medium text-gray-900">
                            Asignación #{index + 1}
                          </p>
                          {detalle.asignacionId && (
                            <p className="text-sm text-gray-600 mt-1">
                              ID: {detalle.asignacionId}
                            </p>
                          )}
                        </div>
                        <span
                          className={`text-xs px-2 py-1 rounded-full font-medium ${
                            detalle.exitoso
                              ? 'bg-green-100 text-green-700'
                              : 'bg-red-100 text-red-700'
                          }`}
                        >
                          {detalle.exitoso ? 'EXITOSO' : 'ERROR'}
                        </span>
                      </div>
                      {detalle.mensaje && (
                        <p
                          className={`text-sm mt-2 ${
                            detalle.exitoso ? 'text-green-700' : 'text-red-700'
                          }`}
                        >
                          {detalle.mensaje}
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <FiAlertCircle className="w-12 h-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500">No hay detalles disponibles</p>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50">
          <button
            onClick={onClose}
            className="w-full px-4 py-3 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition-colors"
          >
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
}
