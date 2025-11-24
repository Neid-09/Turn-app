import { FiEdit2, FiTrash2, FiToggleLeft, FiToggleRight, FiUser, FiMail, FiHash } from 'react-icons/fi';

export default function EmpleadoCard({ empleado, onEditar, onEliminar, onCambiarEstado }) {
  const nombre = empleado.firstName || empleado.nombre;
  const apellido = empleado.lastName || empleado.apellido;
  const nombreCompleto = `${nombre} ${apellido}`;
  const iniciales = `${nombre?.charAt(0)}${apellido?.charAt(0)}`;
  const estaActivo = empleado.enabled || empleado.habilitado;
  const rol = empleado.rolApp || empleado.rol;
  const esAdmin = rol === 'ADMIN';

  return (
    <div className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100 group">
      <div className="p-6">
        <div className="flex justify-between items-center">
          <div className="flex-1 flex items-start gap-4">
            {/* Avatar */}
            <div className="w-14 h-14 bg-linear-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center shrink-0 shadow-md">
              <span className="text-white font-bold text-lg">
                {iniciales}
              </span>
            </div>
            
            {/* Info */}
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <h3 className="font-bold text-lg text-gray-900">
                  {nombreCompleto}
                </h3>
                <span
                  className={`px-3 py-1 text-xs font-semibold rounded-full ${
                    estaActivo
                      ? 'bg-green-100 text-green-700 border border-green-200'
                      : 'bg-red-100 text-red-700 border border-red-200'
                  }`}
                >
                  {estaActivo ? '● Activo' : '○ Inactivo'}
                </span>
              </div>

              {/* Email */}
              <div className="flex items-center gap-2 text-gray-600 mb-2">
                <FiMail className="w-4 h-4 text-gray-400" />
                <span className="text-sm">{empleado.email}</span>
              </div>

              {/* Username */}
              {empleado.username && (
                <div className="flex items-center gap-2 text-gray-600 mb-3">
                  <FiUser className="w-4 h-4 text-gray-400" />
                  <span className="text-sm font-mono">@{empleado.username}</span>
                </div>
              )}
              
              {/* Badges */}
              <div className="flex flex-wrap gap-2 text-sm">
                <div className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-100 rounded-md">
                  <FiHash className="w-3.5 h-3.5 text-gray-500" />
                  <span className="text-gray-600 font-mono text-xs">
                    {empleado.codigoEmpleado}
                  </span>
                </div>
                <span className={`px-3 py-1 rounded-md text-xs font-semibold ${
                  esAdmin 
                    ? 'bg-purple-100 text-purple-700' 
                    : 'bg-blue-100 text-blue-700'
                }`}>
                  {esAdmin ? '👑 Administrador' : '👤 Empleado'}
                </span>
                {empleado.cargo && (
                  <span className="px-3 py-1 bg-gray-100 text-gray-700 rounded-md text-xs">
                    {empleado.cargo}
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Botones de acción */}
          <div className="flex gap-2 ml-4 opacity-0 group-hover:opacity-100 transition-opacity">
            <button
              onClick={() => onEditar(empleado)}
              className="p-3 text-blue-600 hover:bg-blue-50 rounded-xl transition-all transform hover:scale-110 shadow-sm hover:shadow"
              title="Editar empleado"
            >
              <FiEdit2 className="w-5 h-5" />
            </button>
            <button
              onClick={() => onCambiarEstado(empleado.id, estaActivo, nombreCompleto)}
              className={`p-3 rounded-xl transition-all transform hover:scale-110 shadow-sm hover:shadow ${
                estaActivo
                  ? 'text-orange-600 hover:bg-orange-50'
                  : 'text-green-600 hover:bg-green-50'
              }`}
              title={estaActivo ? 'Deshabilitar' : 'Habilitar'}
            >
              {estaActivo ? (
                <FiToggleRight className="w-5 h-5" />
              ) : (
                <FiToggleLeft className="w-5 h-5" />
              )}
            </button>
            <button
              onClick={() => onEliminar(empleado.id, nombreCompleto)}
              className="p-3 text-red-600 hover:bg-red-50 rounded-xl transition-all transform hover:scale-110 shadow-sm hover:shadow"
              title="Eliminar empleado"
            >
              <FiTrash2 className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
