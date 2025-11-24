import { FiTrash2 } from 'react-icons/fi';

/**
 * Tarjeta de empleado asignado con opción de eliminar
 * @param {Object} props
 * @param {Object} props.detalle - Datos del detalle
 * @param {boolean} props.soloLectura - Indica si está en modo solo lectura
 * @param {Function} props.onEliminar - Callback al eliminar
 */
export default function EmpleadoAsignadoCard({ detalle, soloLectura, onEliminar }) {
  const nombreEmpleado = detalle.nombreEmpleado || detalle.usuarioId.substring(0, 8);
  
  return (
    <div className="flex items-center justify-between p-4 bg-gray-50 border border-gray-200 rounded-lg hover:bg-gray-100 transition-colors">
      <div className="flex-1">
        <p className="font-medium text-gray-900">{nombreEmpleado}</p>
        {detalle.observaciones && (
          <p className="text-xs text-gray-600 mt-1 italic">{detalle.observaciones}</p>
        )}
      </div>
      {!soloLectura && (
        <button
          onClick={() => onEliminar(detalle.id, nombreEmpleado)}
          className="ml-4 p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
          title="Eliminar asignación"
        >
          <FiTrash2 className="w-5 h-5" />
        </button>
      )}
    </div>
  );
}
