import { useState } from 'react';
import { FiX } from 'react-icons/fi';
import { horarioService } from '../../../../services/horario.service';
import AlertDialog from '../../../../shared/components/AlertDialog';
import EmpleadoAsignadoCard from './EmpleadoAsignadoCard';
import { parseLocalDate } from './utils/calendarHelpers';

/**
 * Modal para ver detalles del turno
 * @param {Object} props
 * @param {Object} props.turnoData - Datos del turno
 * @param {number} props.horarioId - ID del horario
 * @param {boolean} props.soloLectura - Modo solo lectura
 * @param {Function} props.onClose - Callback al cerrar
 * @param {Function} props.onEliminar - Callback al eliminar
 * @param {Function} props.confirm - Función de confirmación
 * @param {Function} props.success - Función de éxito
 * @param {Function} props.showError - Función de error
 */
export default function DetallesTurnoModal({ 
  turnoData, 
  horarioId, 
  soloLectura = false, 
  onClose, 
  onEliminar, 
  confirm, 
  success, 
  showError 
}) {
  const [confirmDialog, setConfirmDialog] = useState({
    isOpen: false,
    detalleId: null,
    nombreEmpleado: ''
  });

  const handleEliminarDetalle = (detalleId, nombreEmpleado) => {
    setConfirmDialog({
      isOpen: true,
      detalleId,
      nombreEmpleado
    });
  };

  const confirmarEliminacion = async () => {
    try {
      await horarioService.eliminarDetalle(horarioId, confirmDialog.detalleId);
      success('Detalle eliminado');
      setConfirmDialog({ isOpen: false, detalleId: null, nombreEmpleado: '' });
      onEliminar();
    } catch (err) {
      console.error('Error:', err);
      showError('Error al eliminar detalle');
      setConfirmDialog({ isOpen: false, detalleId: null, nombreEmpleado: '' });
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" style={{ zIndex: 60 }}>
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[80vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-bold text-gray-900">{turnoData.turnoNombre}</h3>
              <p className="text-sm text-gray-500">
                {parseLocalDate(turnoData.fecha).toLocaleDateString('es-ES', { 
                  weekday: 'long', 
                  day: 'numeric', 
                  month: 'long', 
                  year: 'numeric' 
                })}
                {turnoData.turno && ` • ${turnoData.turno.horaInicio} - ${turnoData.turno.horaFin}`}
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
              <h4 className="font-semibold text-gray-900">Empleados Asignados</h4>
              <span className="text-sm font-medium text-purple-600">
                {turnoData.cantidadEmpleados} {turnoData.cantidadEmpleados === 1 ? 'empleado' : 'empleados'}
              </span>
            </div>
          </div>

          <div className="space-y-2">
            {turnoData.detalles.map((detalle) => (
              <EmpleadoAsignadoCard
                key={detalle.id}
                detalle={detalle}
                soloLectura={soloLectura}
                onEliminar={handleEliminarDetalle}
              />
            ))}
          </div>

          {turnoData.detalles.length === 0 && (
            <div className="text-center py-12 text-gray-500">
              <p>No hay empleados asignados a este turno</p>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50">
          <button
            onClick={onClose}
            className="w-full px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition-colors"
          >
            Cerrar
          </button>
        </div>
      </div>

      {/* Diálogo de confirmación */}
      <AlertDialog
        isOpen={confirmDialog.isOpen}
        onClose={() => setConfirmDialog({ isOpen: false, detalleId: null, nombreEmpleado: '' })}
        onConfirm={confirmarEliminacion}
        title="Eliminar Asignación"
        message={`¿Estás seguro de eliminar la asignación de ${confirmDialog.nombreEmpleado}? Esta acción no se puede deshacer.`}
        type="confirm"
        confirmText="Eliminar"
        cancelText="Cancelar"
      />
    </div>
  );
}
