import { useState, useEffect } from 'react';
import { horarioService } from '../../../../services/horario.service';
import { useAlert } from '../../../../shared/hooks/useAlert';
import { parseLocalDate } from '../CalendarioHorario/utils/calendarHelpers';
import {
  PanelEstadisticas,
  ConsolidadoHeader,
  DetallesAsignacionesModal,
  CalendarioAsignaciones,
  LeyendaEstados
} from '.';

/**
 * Componente principal de vista consolidada
 */
export default function VistaConsolidadaModal({ horario, onClose }) {
  const [view, setView] = useState('month');
  const [date, setDate] = useState(parseLocalDate(horario.fechaInicio));
  const [consolidado, setConsolidado] = useState(null);
  const [loading, setLoading] = useState(true);
  const [mostrarDetalles, setMostrarDetalles] = useState(false);
  const [asignacionesSeleccionadas, setAsignacionesSeleccionadas] = useState(null);
  const { error: showError } = useAlert();

  useEffect(() => {
    cargarConsolidado();
  }, [horario.id]);

  const cargarConsolidado = async () => {
    try {
      setLoading(true);
      const data = await horarioService.obtenerConsolidado(horario.id);
      console.log('Datos consolidados recibidos:', data);
      setConsolidado(data);
    } catch (err) {
      console.error('Error:', err);
      showError('Error al cargar vista consolidada');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectEvent = (event) => {
    setAsignacionesSeleccionadas({
      asignaciones: event.resource.asignaciones,
      fecha: event.resource.fecha,
      turno: event.resource.turnoNombre
    });
    setMostrarDetalles(true);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-7xl max-h-[90vh] overflow-hidden flex flex-col">
        <ConsolidadoHeader
          horario={horario}
          loading={loading}
          onRefresh={cargarConsolidado}
          onClose={onClose}
        />

        {/* Contenido */}
        <div className="flex-1 p-6 overflow-auto">
          {loading ? (
            <div className="h-full flex items-center justify-center">
              <div className="text-center">
                <div className="inline-block w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                <p className="text-gray-500 mt-4">Cargando vista consolidada...</p>
              </div>
            </div>
          ) : consolidado ? (
            <>
              <PanelEstadisticas consolidado={consolidado} />

              <CalendarioAsignaciones
                consolidado={consolidado}
                view={view}
                date={date}
                onView={setView}
                onNavigate={setDate}
                onSelectEvent={handleSelectEvent}
              />

              <LeyendaEstados />
            </>
          ) : (
            <div className="h-full flex items-center justify-center">
              <p className="text-gray-500">No hay datos consolidados disponibles</p>
            </div>
          )}
        </div>
      </div>

      {/* Modal de detalles */}
      {mostrarDetalles && asignacionesSeleccionadas && (
        <DetallesAsignacionesModal
          asignaciones={asignacionesSeleccionadas.asignaciones}
          fecha={asignacionesSeleccionadas.fecha}
          turno={asignacionesSeleccionadas.turno}
          onClose={() => {
            setMostrarDetalles(false);
            setAsignacionesSeleccionadas(null);
          }}
        />
      )}
    </div>
  );
}
