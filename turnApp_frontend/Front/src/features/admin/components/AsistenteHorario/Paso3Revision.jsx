import { FiEye, FiAlertCircle } from 'react-icons/fi';

export default function Paso3Revision({ datosHorario, asignaciones }) {
  // Agrupar asignaciones por empleado
  const asignacionesPorEmpleado = asignaciones.reduce((acc, asig) => {
    if (!acc[asig.usuarioId]) {
      acc[asig.usuarioId] = {
        nombre: asig.nombreEmpleado,
        codigo: asig.codigoEmpleado,
        asignaciones: []
      };
    }
    acc[asig.usuarioId].asignaciones.push(asig);
    return acc;
  }, {});

  const empleadosConAsignaciones = Object.values(asignacionesPorEmpleado);
  const asignacionesFueraPreferencia = asignaciones.filter(
    a => !a.cumplePreferencias && a.tienePreferencias
  ).length;

  return (
    <div className="space-y-4">
      <div className="text-center mb-6">
        <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
          <FiEye className="w-8 h-8 text-purple-600" />
        </div>
        <h3 className="text-xl font-bold text-gray-900">Revisión Final</h3>
        <p className="text-sm text-gray-500 mt-1">Verifica los datos antes de crear el horario</p>
      </div>

      <div className="space-y-3">
        {/* Información del horario */}
        <div className="bg-gray-50 rounded-lg p-4">
          <h4 className="font-semibold text-gray-900 mb-2">Información del Horario</h4>
          <dl className="space-y-1 text-sm">
            <div className="flex justify-between">
              <dt className="text-gray-600">Nombre:</dt>
              <dd className="font-medium text-gray-900">{datosHorario.nombre}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-600">Período:</dt>
              <dd className="font-medium text-gray-900">
                {datosHorario.fechaInicio} - {datosHorario.fechaFin}
              </dd>
            </div>
            {datosHorario.descripcion && (
              <div className="flex justify-between">
                <dt className="text-gray-600">Descripción:</dt>
                <dd className="font-medium text-gray-900">{datosHorario.descripcion}</dd>
              </div>
            )}
          </dl>
        </div>

        {/* Resumen de asignaciones */}
        <div className="bg-gray-50 rounded-lg p-4">
          <h4 className="font-semibold text-gray-900 mb-2">Resumen de Asignaciones</h4>
          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-2xl font-bold text-purple-600">{asignaciones.length}</p>
              <p className="text-xs text-gray-600">Total turnos</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-purple-600">{empleadosConAsignaciones.length}</p>
              <p className="text-xs text-gray-600">Empleados</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-purple-600">{asignacionesFueraPreferencia}</p>
              <p className="text-xs text-gray-600">Fuera de preferencia</p>
            </div>
          </div>
        </div>

        {/* Detalles por empleado */}
        {empleadosConAsignaciones.length > 0 && (
          <div className="bg-gray-50 rounded-lg p-4">
            <h4 className="font-semibold text-gray-900 mb-3">Detalles por Empleado</h4>
            <div className="space-y-2 max-h-64 overflow-y-auto">
              {empleadosConAsignaciones.map(emp => (
                <div key={emp.codigo} className="bg-white rounded-lg p-3 border border-gray-200">
                  <div className="flex items-center justify-between mb-2">
                    <div>
                      <p className="font-medium text-gray-900">{emp.nombre}</p>
                      <p className="text-xs text-gray-500">{emp.codigo}</p>
                    </div>
                    <span className="text-sm font-semibold text-purple-600">
                      {emp.asignaciones.length} turnos
                    </span>
                  </div>
                  <div className="flex flex-wrap gap-1">
                    {emp.asignaciones.slice(0, 5).map((asig, idx) => (
                      <span
                        key={idx}
                        className={`text-xs px-2 py-1 rounded ${
                          !asig.cumplePreferencias && asig.tienePreferencias
                            ? 'bg-yellow-100 text-yellow-700'
                            : 'bg-green-100 text-green-700'
                        }`}
                      >
                        {new Date(asig.fecha + 'T00:00:00').toLocaleDateString('es-ES', { day: '2-digit', month: 'short' })} - {asig.nombreTurno}
                      </span>
                    ))}
                    {emp.asignaciones.length > 5 && (
                      <span className="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded">
                        +{emp.asignaciones.length - 5} más
                      </span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Advertencias */}
        {asignacionesFueraPreferencia > 0 && (
          <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
            <div className="flex gap-2">
              <FiAlertCircle className="w-5 h-5 text-yellow-600 shrink-0 mt-0.5" />
              <div>
                <p className="text-sm font-semibold text-yellow-900">Advertencia de Preferencias</p>
                <p className="text-xs text-yellow-800 mt-1">
                  Hay {asignacionesFueraPreferencia} asignaciones 
                  fuera de las preferencias horarias configuradas. El sistema permite estas asignaciones 
                  pero se recomienda revisarlas.
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Nota informativa */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <p className="text-sm text-blue-800">
            <strong>Nota:</strong> El horario se creará en estado BORRADOR. Podrás editarlo
            y luego publicarlo para crear las asignaciones en el sistema.
          </p>
        </div>
      </div>
    </div>
  );
}
