import { useState, useEffect } from 'react';
import { turnoService } from '../../../services/turno.service';
import { FiPlus, FiClock, FiEdit, FiTrash2, FiPower } from 'react-icons/fi';
import { useAlert } from '../../../shared/hooks/useAlert';
import CrearTurnoModal from './CrearTurnoModal';
import EditarTurnoModal from './EditarTurnoModal';

// Card de turno
function TurnoCard({ turno, onEditar, onEliminar, onCambiarEstado }) {
  const calcularDuracion = (inicio, fin) => {
    const [hI, mI] = inicio.split(':').map(Number);
    const [hF, mF] = fin.split(':').map(Number);
    const minutosInicio = hI * 60 + mI;
    const minutosFin = hF * 60 + mF;
    const duracionMinutos = minutosFin - minutosInicio;
    const horas = Math.floor(duracionMinutos / 60);
    const minutos = duracionMinutos % 60;
    return `${horas}h ${minutos}m`;
  };

  return (
    <div className="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow border border-gray-100">
      <div className="p-5">
        {/* Header */}
        <div className="flex items-start justify-between mb-3">
          <div className="flex-1">
            <h3 className="text-lg font-semibold text-gray-900 mb-1">
              {turno.nombre}
            </h3>
            <p className="text-sm text-gray-500">
              {turno.horaInicio} - {turno.horaFin}
            </p>
          </div>
          <span
            className={`px-3 py-1 rounded-full text-xs font-medium ${
              turno.estado === 'ACTIVO'
                ? 'bg-green-100 text-green-700'
                : 'bg-gray-100 text-gray-700'
            }`}
          >
            {turno.estado === 'ACTIVO' ? 'Activo' : 'Inactivo'}
          </span>
        </div>

        {/* Información */}
        <div className="space-y-2 mb-4">
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <FiClock className="w-4 h-4" />
            <span>Duración: {calcularDuracion(turno.horaInicio, turno.horaFin)}</span>
          </div>
          {turno.descripcion && (
            <p className="text-sm text-gray-600 line-clamp-2">
              {turno.descripcion}
            </p>
          )}
        </div>

        {/* Acciones */}
        <div className="flex items-center gap-2 pt-3 border-t border-gray-100">
          <button
            onClick={() => onEditar(turno)}
            className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm font-medium text-blue-700 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors"
          >
            <FiEdit className="w-4 h-4" />
            Editar
          </button>

          <button
            onClick={() => onCambiarEstado(turno)}
            className={`flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
              turno.estado === 'ACTIVO'
                ? 'text-gray-700 bg-gray-50 hover:bg-gray-100'
                : 'text-green-700 bg-green-50 hover:bg-green-100'
            }`}
          >
            {turno.estado === 'ACTIVO' ? (
              <>
                <FiPower className="w-4 h-4" />
                Desactivar
              </>
            ) : (
              <>
                <FiPower className="w-4 h-4" />
                Activar
              </>
            )}
          </button>

          <button
            onClick={() => onEliminar(turno)}
            className="px-3 py-2 text-sm font-medium text-red-700 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
          >
            <FiTrash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}

export default function GestionTurnosTab() {
  const [turnos, setTurnos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCrearModal, setShowCrearModal] = useState(false);
  const [showEditarModal, setShowEditarModal] = useState(false);
  const [turnoSeleccionado, setTurnoSeleccionado] = useState(null);
  const [filtroEstado, setFiltroEstado] = useState('TODOS');
  const { confirm, success, error: showError } = useAlert();

  useEffect(() => {
    cargarTurnos();
  }, []);

  const cargarTurnos = async () => {
    try {
      setLoading(true);
      const data = await turnoService.listarTodos();
      setTurnos(data);
    } catch (err) {
      console.error('Error al cargar turnos:', err);
      showError('Error al cargar la lista de turnos');
    } finally {
      setLoading(false);
    }
  };

  const turnosFiltrados = turnos.filter(t =>
    filtroEstado === 'TODOS' || t.estado === filtroEstado
  );

  const handleEditar = (turno) => {
    setTurnoSeleccionado(turno);
    setShowEditarModal(true);
  };

  const handleEliminar = (turno) => {
    confirm(
      `¿Estás seguro de eliminar el turno "${turno.nombre}"? Esta acción no se puede deshacer.`,
      async () => {
        try {
          await turnoService.eliminar(turno.id);
          success('Turno eliminado exitosamente');
          cargarTurnos();
        } catch (err) {
          console.error('Error al eliminar:', err);
          showError('Error al eliminar el turno');
        }
      },
      'Eliminar Turno'
    );
  };

  const handleCambiarEstado = (turno) => {
    const nuevoEstado = turno.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    const accion = nuevoEstado === 'ACTIVO' ? 'activar' : 'desactivar';

    confirm(
      `¿Estás seguro de ${accion} el turno "${turno.nombre}"?`,
      async () => {
        try {
          // Actualizamos el turno cambiando su estado
          await turnoService.actualizar(turno.id, { ...turno, estado: nuevoEstado });
          success(`Turno ${accion === 'activar' ? 'activado' : 'desactivado'} exitosamente`);
          cargarTurnos();
        } catch (err) {
          console.error('Error al cambiar estado:', err);
          showError('Error al cambiar el estado del turno');
        }
      },
      `${nuevoEstado === 'ACTIVO' ? 'Activar' : 'Desactivar'} Turno`
    );
  };

  const handleCrearExitoso = () => {
    setShowCrearModal(false);
    setTimeout(() => {
      success('Turno creado exitosamente');
      cargarTurnos();
    }, 100);
  };

  const handleEditarExitoso = () => {
    setShowEditarModal(false);
    setTurnoSeleccionado(null);
    setTimeout(() => {
      success('Turno actualizado exitosamente');
      cargarTurnos();
    }, 100);
  };

  return (
    <div>
      {/* Barra de acciones */}
      <div className="mb-6 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
        {/* Filtros */}
        <div className="flex items-center gap-3">
          <select
            value={filtroEstado}
            onChange={(e) => setFiltroEstado(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
          >
            <option value="TODOS">Todos los turnos</option>
            <option value="ACTIVO">Activos</option>
            <option value="INACTIVO">Inactivos</option>
          </select>
        </div>

        {/* Botón crear */}
        <button
          onClick={() => setShowCrearModal(true)}
          className="flex items-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition-colors"
        >
          <FiPlus className="w-5 h-5" />
          Crear Turno
        </button>
      </div>

      {/* Lista de turnos */}
      {loading ? (
        <div className="text-center py-12">
          <div className="inline-block w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-gray-500 mt-4">Cargando turnos...</p>
        </div>
      ) : turnosFiltrados.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm p-12 text-center">
          <FiClock className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-700 mb-2">
            No hay turnos
          </h3>
          <p className="text-gray-500 mb-6">
            {filtroEstado === 'TODOS'
              ? 'Crea tu primera plantilla de turno para comenzar'
              : `No hay turnos ${filtroEstado === 'ACTIVO' ? 'activos' : 'inactivos'}`
            }
          </p>
          <button
            onClick={() => setShowCrearModal(true)}
            className="inline-flex items-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition-colors"
          >
            <FiPlus className="w-5 h-5" />
            Crear Turno
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {turnosFiltrados.map((turno) => (
            <TurnoCard
              key={turno.id}
              turno={turno}
              onEditar={handleEditar}
              onEliminar={handleEliminar}
              onCambiarEstado={handleCambiarEstado}
            />
          ))}
        </div>
      )}

      {/* Modales */}
      {showCrearModal && (
        <CrearTurnoModal
          onClose={() => setShowCrearModal(false)}
          onSuccess={handleCrearExitoso}
        />
      )}

      {showEditarModal && turnoSeleccionado && (
        <EditarTurnoModal
          turno={turnoSeleccionado}
          onClose={() => {
            setShowEditarModal(false);
            setTurnoSeleccionado(null);
          }}
          onSuccess={handleEditarExitoso}
        />
      )}
    </div>
  );
}
