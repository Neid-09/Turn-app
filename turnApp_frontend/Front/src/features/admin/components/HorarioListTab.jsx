import { useState, useEffect } from 'react';
import { horarioService } from '../../../services/horario.service';
import { FiPlus, FiFilter, FiCalendar, FiEye, FiEdit, FiTrash2, FiSend } from 'react-icons/fi';
import { useAlert } from '../../../shared/hooks/useAlert';
import AsistenteHorarioModal from './AsistenteHorarioModal';
import { CalendarioHorarioModal } from './CalendarioHorario';
import ReportePublicacionModal from './ReportePublicacionModal';
import VistaConsolidadaModal from './VistaConsolidadaModal';

// Función helper para parsear fechas ISO sin conversión de zona horaria
const parseLocalDate = (dateString) => {
  if (!dateString) return new Date();
  const [year, month, day] = dateString.split('T')[0].split('-');
  return new Date(year, month - 1, day);
};

// Badge de estado
function EstadoBadge({ estado }) {
  const estadoConfig = {
    BORRADOR: { bg: 'bg-gray-100', text: 'text-gray-700', label: 'Borrador' },
    PUBLICADO: { bg: 'bg-blue-100', text: 'text-blue-700', label: 'Publicado' },
    ACTIVO: { bg: 'bg-green-100', text: 'text-green-700', label: 'Activo' },
    FINALIZADO: { bg: 'bg-gray-200', text: 'text-gray-600', label: 'Finalizado' },
    CANCELADO: { bg: 'bg-red-100', text: 'text-red-700', label: 'Cancelado' },
  };

  const config = estadoConfig[estado] || estadoConfig.BORRADOR;

  return (
    <span className={`px-3 py-1 rounded-full text-xs font-medium ${config.bg} ${config.text}`}>
      {config.label}
    </span>
  );
}

// Card de horario
function HorarioCard({ horario, onVer, onEditar, onEliminar, onPublicar, onVerConsolidado }) {
  const formatFecha = (fecha) => {
    return parseLocalDate(fecha).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const puedeEditar = horario.estado === 'BORRADOR';
  const puedePublicar = horario.estado === 'BORRADOR' && horario.cantidadDetalles > 0;
  const estaPublicado = horario.estado === 'PUBLICADO' || horario.estado === 'ACTIVO';

  return (
    <div className="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow border border-gray-100">
      <div className="p-5">
        {/* Header */}
        <div className="flex items-start justify-between mb-3">
          <div className="flex-1">
            <h3 className="text-lg font-semibold text-gray-900 mb-1">
              {horario.nombre}
            </h3>
            <p className="text-sm text-gray-500">
              {formatFecha(horario.fechaInicio)} - {formatFecha(horario.fechaFin)}
            </p>
          </div>
          <EstadoBadge estado={horario.estado} />
        </div>

        {/* Información */}
        <div className="space-y-2 mb-4">
          {horario.descripcion && (
            <p className="text-sm text-gray-600 line-clamp-2">
              {horario.descripcion}
            </p>
          )}
          <div className="flex items-center gap-4 text-xs text-gray-500">
            <span className="flex items-center gap-1">
              <FiCalendar className="w-3 h-3" />
              {horario.cantidadDetalles} detalles
            </span>
            {horario.publicadoEn && (
              <span>
                Publicado {formatFecha(horario.publicadoEn)}
              </span>
            )}
          </div>
        </div>

        {/* Acciones */}
        <div className="flex items-center gap-2 pt-3 border-t border-gray-100">
          <button
            onClick={() => onVer(horario)}
            className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm font-medium text-gray-700 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <FiEye className="w-4 h-4" />
            Ver
          </button>
          
          {puedeEditar && (
            <button
              onClick={() => onEditar(horario)}
              className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm font-medium text-blue-700 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors"
            >
              <FiEdit className="w-4 h-4" />
              Editar
            </button>
          )}
          
          {puedePublicar && (
            <button
              onClick={() => onPublicar(horario)}
              className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm font-medium text-green-700 bg-green-50 hover:bg-green-100 rounded-lg transition-colors"
            >
              <FiSend className="w-4 h-4" />
              Publicar
            </button>
          )}

          {estaPublicado && (
            <button
              onClick={() => onVerConsolidado(horario)}
              className="flex-1 flex items-center justify-center gap-2 px-3 py-2 text-sm font-medium text-purple-700 bg-purple-50 hover:bg-purple-100 rounded-lg transition-colors"
            >
              <FiCalendar className="w-4 h-4" />
              Consolidado
            </button>
          )}
          
          {puedeEditar && (
            <button
              onClick={() => onEliminar(horario)}
              className="px-3 py-2 text-sm font-medium text-red-700 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
            >
              <FiTrash2 className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default function HorarioListTab() {
  const [horarios, setHorarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState('TODOS');
  const [mostrarAsistente, setMostrarAsistente] = useState(false);
  const [mostrarCalendario, setMostrarCalendario] = useState(false);
  const [mostrarReporte, setMostrarReporte] = useState(false);
  const [mostrarConsolidado, setMostrarConsolidado] = useState(false);
  const [reporteData, setReporteData] = useState(null);
  const [horarioSeleccionado, setHorarioSeleccionado] = useState(null);
  const [modoVista, setModoVista] = useState('ver'); // 'ver' o 'editar'
  const { confirm, success, error: showError } = useAlert();

  useEffect(() => {
    cargarHorarios();
  }, []);

  const cargarHorarios = async () => {
    try {
      setLoading(true);
      const data = await horarioService.listarTodos(false);
      setHorarios(data);
    } catch (err) {
      console.error('Error al cargar horarios:', err);
      showError('Error al cargar la lista de horarios');
    } finally {
      setLoading(false);
    }
  };

  const horariosFiltrados = horarios.filter(h => 
    filtroEstado === 'TODOS' || h.estado === filtroEstado
  );

  const handleVer = (horario) => {
    setHorarioSeleccionado(horario);
    setModoVista('ver'); // Solo lectura
    setMostrarCalendario(true);
  };

  const handleEditar = (horario) => {
    setHorarioSeleccionado(horario);
    setModoVista('editar'); // Modo edición
    setMostrarCalendario(true);
  };

  const handleEliminar = (horario) => {
    confirm(
      `¿Estás seguro de eliminar el horario "${horario.nombre}"? Esta acción no se puede deshacer.`,
      async () => {
        try {
          await horarioService.eliminar(horario.id);
          success('Horario eliminado exitosamente');
          cargarHorarios();
        } catch (err) {
          console.error('Error al eliminar:', err);
          showError('Error al eliminar el horario');
        }
      },
      'Eliminar Horario'
    );
  };

  const handlePublicar = (horario) => {
    confirm(
      `¿Publicar el horario "${horario.nombre}"? Se crearán ${horario.cantidadDetalles} asignaciones en el sistema.`,
      async () => {
        try {
          const reporte = await horarioService.publicar(horario.id);
          setReporteData(reporte);
          setMostrarReporte(true);
        } catch (err) {
          console.error('Error al publicar:', err);
          showError('Error al publicar el horario');
        }
      },
      'Publicar Horario'
    );
  };

  const handleVerConsolidado = (horario) => {
    setHorarioSeleccionado(horario);
    setMostrarConsolidado(true);
  };

  const handleCrearNuevo = () => {
    setMostrarAsistente(true);
  };

  const handleAsistenteSuccess = () => {
    setMostrarAsistente(false);
    success('Horario creado exitosamente');
    cargarHorarios();
  };

  return (
    <div>
      {/* Barra de acciones */}
      <div className="mb-6 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
        {/* Filtros */}
        <div className="flex items-center gap-3">
          <FiFilter className="w-5 h-5 text-gray-400" />
          <select
            value={filtroEstado}
            onChange={(e) => setFiltroEstado(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
          >
            <option value="TODOS">Todos los estados</option>
            <option value="BORRADOR">Borrador</option>
            <option value="PUBLICADO">Publicado</option>
            <option value="ACTIVO">Activo</option>
            <option value="FINALIZADO">Finalizado</option>
            <option value="CANCELADO">Cancelado</option>
          </select>
        </div>

        {/* Botón crear */}
        <button
          onClick={handleCrearNuevo}
          className="flex items-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition-colors"
        >
          <FiPlus className="w-5 h-5" />
          Crear Horario
        </button>
      </div>

      {/* Lista de horarios */}
      {loading ? (
        <div className="text-center py-12">
          <div className="inline-block w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-gray-500 mt-4">Cargando horarios...</p>
        </div>
      ) : horariosFiltrados.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm p-12 text-center">
          <FiCalendar className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-700 mb-2">
            No hay horarios
          </h3>
          <p className="text-gray-500 mb-6">
            {filtroEstado === 'TODOS' 
              ? 'Crea tu primer horario para comenzar'
              : `No hay horarios en estado "${filtroEstado}"`
            }
          </p>
          <button
            onClick={handleCrearNuevo}
            className="inline-flex items-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition-colors"
          >
            <FiPlus className="w-5 h-5" />
            Crear Horario
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {horariosFiltrados.map((horario) => (
            <HorarioCard
              key={horario.id}
              horario={horario}
              onVer={handleVer}
              onEditar={handleEditar}
              onEliminar={handleEliminar}
              onPublicar={handlePublicar}
              onVerConsolidado={handleVerConsolidado}
            />
          ))}
        </div>
      )}

      {/* Modales */}
      {mostrarAsistente && (
        <AsistenteHorarioModal
          onClose={() => setMostrarAsistente(false)}
          onSuccess={handleAsistenteSuccess}
        />
      )}

      {mostrarCalendario && horarioSeleccionado && (
        <CalendarioHorarioModal
          horario={horarioSeleccionado}
          modoVista={modoVista}
          onClose={() => {
            setMostrarCalendario(false);
            setHorarioSeleccionado(null);
            cargarHorarios();
          }}
          onPublicar={(reporte) => {
            setMostrarCalendario(false);
            setHorarioSeleccionado(null);
            setReporteData(reporte);
            setMostrarReporte(true);
          }}
        />
      )}

      {mostrarReporte && reporteData && (
        <ReportePublicacionModal
          reporte={reporteData}
          onClose={() => {
            setMostrarReporte(false);
            setReporteData(null);
            success(`Publicado: ${reporteData.totalExitosos}/${reporteData.totalProcesados} asignaciones`);
            cargarHorarios();
          }}
        />
      )}

      {mostrarConsolidado && horarioSeleccionado && (
        <VistaConsolidadaModal
          horario={horarioSeleccionado}
          onClose={() => {
            setMostrarConsolidado(false);
            setHorarioSeleccionado(null);
          }}
        />
      )}
    </div>
  );
}
