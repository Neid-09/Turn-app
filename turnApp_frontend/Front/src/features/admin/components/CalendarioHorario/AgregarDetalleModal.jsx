import { useState, useEffect } from 'react';
import moment from 'moment';
import { FiX } from 'react-icons/fi';
import { horarioService } from '../../../../services/horario.service';
import { turnoService } from '../../../../services/turno.service';
import { usuarioService } from '../../../../services/usuario.service';
import { useAlert } from '../../../../shared/hooks/useAlert';

/**
 * Modal para agregar detalle de turno
 * @param {Object} props
 * @param {string} props.fecha - Fecha seleccionada
 * @param {number} props.horarioId - ID del horario
 * @param {Function} props.onClose - Callback al cerrar
 * @param {Function} props.onSuccess - Callback al guardar exitosamente
 */
export default function AgregarDetalleModal({ fecha, horarioId, onClose, onSuccess }) {
  const [empleados, setEmpleados] = useState([]);
  const [turnos, setTurnos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [enviando, setEnviando] = useState(false);
  const [formData, setFormData] = useState({
    usuarioId: '',
    turnoId: '',
    observaciones: ''
  });
  const { error: showError } = useAlert();

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        const [emps, turns] = await Promise.all([
          usuarioService.getAll(),
          turnoService.listarActivos()
        ]);
        setEmpleados(emps);
        setTurnos(turns);
      } catch (err) {
        console.error('Error:', err);
        showError('Error al cargar datos');
      } finally {
        setLoading(false);
      }
    };
    cargarDatos();
  }, [fecha]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setEnviando(true);
      await horarioService.agregarDetalle(horarioId, {
        ...formData,
        fecha: fecha
      });
      onSuccess();
    } catch (err) {
      console.error('Error:', err);
      showError('Error al agregar detalle');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-60 p-4">
      <div className="bg-white rounded-xl shadow-xl max-w-md w-full">
        <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <h3 className="text-lg font-bold text-gray-900">
            Agregar Turno - {moment(fecha).format('DD/MM/YYYY')}
          </h3>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg">
            <FiX className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {loading ? (
            <div className="py-12 text-center">
              <div className="inline-block w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
            </div>
          ) : (
            <>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Empleado *
                </label>
                <select
                  required
                  value={formData.usuarioId}
                  onChange={(e) => setFormData({ ...formData, usuarioId: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 text-gray-900 bg-white"
                >
                  <option value="" className="text-gray-500">Seleccionar empleado</option>
                  {empleados.map((emp) => (
                    <option key={emp.keycloakId} value={emp.keycloakId} className="text-gray-900">
                      {emp.firstName} {emp.lastName}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Turno *
                </label>
                <select
                  required
                  value={formData.turnoId}
                  onChange={(e) => setFormData({ ...formData, turnoId: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 text-gray-900 bg-white"
                >
                  <option value="" className="text-gray-500">Seleccionar turno</option>
                  {turnos.map((turno) => (
                    <option key={turno.id} value={turno.id} className="text-gray-900">
                      {turno.nombre} ({turno.horaInicio} - {turno.horaFin})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Observaciones
                </label>
                <textarea
                  value={formData.observaciones}
                  onChange={(e) => setFormData({ ...formData, observaciones: e.target.value })}
                  rows={2}
                  placeholder="Observaciones opcionales..."
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                />
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={onClose}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={enviando}
                  className="flex-1 px-4 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-purple-300 text-white rounded-lg font-medium"
                >
                  {enviando ? 'Guardando...' : 'Agregar'}
                </button>
              </div>
            </>
          )}
        </form>
      </div>
    </div>
  );
}
