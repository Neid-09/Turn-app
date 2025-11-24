import { useState, useEffect, useMemo } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'moment/locale/es';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { horarioService } from '../../../../services/horario.service';
import { turnoService } from '../../../../services/turno.service';
import { useAlert } from '../../../../shared/hooks/useAlert';
import ReportePublicacionModal from '../ReportePublicacionModal';
import CustomToolbar from './CustomToolbar';
import AgregarDetalleModal from './AgregarDetalleModal';
import DetallesTurnoModal from './DetallesTurnoModal';
import CalendarioHeader from './CalendarioHeader';
import CalendarioFooter from './CalendarioFooter';
import { 
  parseLocalDate, 
  agruparDetallesPorFechaTurno, 
  convertirGruposAEventos 
} from './utils/calendarHelpers';

moment.locale('es');
const localizer = momentLocalizer(moment);

/**
 * Modal principal del calendario de horarios
 * @param {Object} props
 * @param {Object} props.horario - Datos del horario
 * @param {string} props.modoVista - Modo de vista ('ver' | 'editar')
 * @param {Function} props.onClose - Callback al cerrar
 * @param {Function} props.onPublicar - Callback al publicar
 */
export default function CalendarioHorarioModal({ horario, modoVista = 'ver', onClose, onPublicar }) {
  const [view, setView] = useState('month');
  const [date, setDate] = useState(parseLocalDate(horario.fechaInicio));
  const [detalles, setDetalles] = useState([]);
  const [turnos, setTurnos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [mostrarAgregar, setMostrarAgregar] = useState(false);
  const [mostrarReporte, setMostrarReporte] = useState(false);
  const [reporteData, setReporteData] = useState(null);
  const [fechaSeleccionada, setFechaSeleccionada] = useState(null);
  const [mostrarDetalles, setMostrarDetalles] = useState(false);
  const [turnoSeleccionado, setTurnoSeleccionado] = useState(null);
  const { confirm, success, error: showError } = useAlert();

  const soloLectura = modoVista === 'ver';

  useEffect(() => {
    cargarDatos();
  }, [horario.id]);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [horarioDetallado, turnosData] = await Promise.all([
        horarioService.obtenerPorId(horario.id),
        turnoService.listarActivos()
      ]);
      setDetalles(horarioDetallado.detalles || []);
      setTurnos(turnosData);
    } catch (err) {
      console.error('Error:', err);
      showError('Error al cargar datos');
    } finally {
      setLoading(false);
    }
  };

  // Convertir detalles a eventos del calendario
  const eventos = useMemo(() => {
    const gruposPorFechaTurno = agruparDetallesPorFechaTurno(detalles);
    return convertirGruposAEventos(gruposPorFechaTurno, turnos);
  }, [detalles, turnos]);

  const handleSelectSlot = ({ start }) => {
    if (soloLectura) return;
    
    const fechaInicio = parseLocalDate(horario.fechaInicio);
    const fechaFin = parseLocalDate(horario.fechaFin);
    const fechaSeleccionada = new Date(start);

    if (fechaSeleccionada >= fechaInicio && fechaSeleccionada <= fechaFin) {
      setFechaSeleccionada(start.toISOString().split('T')[0]);
      setMostrarAgregar(true);
    } else {
      showError('La fecha debe estar dentro del rango del horario');
    }
  };

  const handleSelectEvent = (event) => {
    setTurnoSeleccionado(event.resource);
    setMostrarDetalles(true);
  };

  const handlePublicar = () => {
    confirm(
      `¿Publicar el horario "${horario.nombre}"? Se crearán ${detalles.length} asignaciones.`,
      async () => {
        try {
          const reporte = await horarioService.publicar(horario.id);
          setReporteData(reporte);
          setMostrarReporte(true);
        } catch (err) {
          console.error('Error:', err);
          showError('Error al publicar horario');
        }
      },
      'Publicar Horario'
    );
  };

  const puedePublicar = horario.estado === 'BORRADOR' && detalles.length > 0;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-6xl max-h-[90vh] overflow-hidden flex flex-col">
        <CalendarioHeader
          horario={horario}
          puedePublicar={puedePublicar}
          onPublicar={handlePublicar}
          onClose={onClose}
        />

        {/* Contenido */}
        <div className="flex-1 p-6 overflow-auto">
          {loading ? (
            <div className="h-full flex items-center justify-center">
              <div className="text-center">
                <div className="inline-block w-12 h-12 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
                <p className="text-gray-500 mt-4">Cargando calendario...</p>
              </div>
            </div>
          ) : (
            <div style={{ height: '600px' }}>
              <Calendar
                localizer={localizer}
                events={eventos}
                startAccessor="start"
                endAccessor="end"
                view={view}
                onView={setView}
                date={date}
                onNavigate={setDate}
                onSelectSlot={soloLectura ? undefined : handleSelectSlot}
                onSelectEvent={handleSelectEvent}
                selectable={!soloLectura}
                components={{
                  toolbar: (props) => <CustomToolbar {...props} view={view} onView={setView} />
                }}
                eventPropGetter={(event) => ({
                  style: {
                    backgroundColor: event.resource.color,
                    borderColor: event.resource.color,
                    color: 'white',
                    borderRadius: '6px',
                    padding: '2px 6px',
                    fontSize: '0.875rem',
                    fontWeight: '500'
                  }
                })}
                messages={{
                  today: 'Hoy',
                  previous: 'Anterior',
                  next: 'Siguiente',
                  month: 'Mes',
                  week: 'Semana',
                  day: 'Día',
                  agenda: 'Agenda',
                  date: 'Fecha',
                  time: 'Hora',
                  event: 'Evento',
                  noEventsInRange: 'No hay turnos asignados en este rango',
                  showMore: (total) => `+ Ver más (${total})`
                }}
              />
            </div>
          )}
        </div>

        <CalendarioFooter
          totalDetalles={detalles.length}
          soloLectura={soloLectura}
        />
      </div>

      {/* Modal agregar detalle */}
      {mostrarAgregar && (
        <AgregarDetalleModal
          fecha={fechaSeleccionada}
          horarioId={horario.id}
          onClose={() => setMostrarAgregar(false)}
          onSuccess={() => {
            setMostrarAgregar(false);
            success('Turno agregado');
            cargarDatos();
          }}
        />
      )}

      {/* Modal reporte publicación */}
      {mostrarReporte && reporteData && (
        <ReportePublicacionModal
          reporte={reporteData}
          onClose={() => {
            setMostrarReporte(false);
            onPublicar(reporteData);
          }}
        />
      )}

      {/* Modal detalles del turno */}
      {mostrarDetalles && turnoSeleccionado && (
        <DetallesTurnoModal
          turnoData={turnoSeleccionado}
          horarioId={horario.id}
          soloLectura={soloLectura}
          onClose={() => {
            setMostrarDetalles(false);
            setTurnoSeleccionado(null);
          }}
          onEliminar={() => {
            setMostrarDetalles(false);
            setTurnoSeleccionado(null);
            cargarDatos();
          }}
          confirm={confirm}
          success={success}
          showError={showError}
        />
      )}
    </div>
  );
}
