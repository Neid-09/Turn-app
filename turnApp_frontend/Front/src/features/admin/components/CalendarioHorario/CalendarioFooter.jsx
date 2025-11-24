/**
 * Footer del modal de calendario con estadísticas
 * @param {Object} props
 * @param {number} props.totalDetalles - Total de turnos asignados
 * @param {boolean} props.soloLectura - Indica si está en modo solo lectura
 */
export default function CalendarioFooter({ totalDetalles, soloLectura }) {
  return (
    <div className="px-6 py-4 border-t border-gray-200 bg-gray-50">
      <div className="flex items-center justify-between text-sm">
        <p className="text-gray-600">
          <strong>{totalDetalles}</strong> turnos asignados
        </p>
        <p className="text-gray-500">
          {soloLectura 
            ? 'Click en una tarjeta para ver detalles' 
            : 'Click en una fecha para agregar turno • Click en una tarjeta para ver detalles'
          }
        </p>
      </div>
    </div>
  );
}
