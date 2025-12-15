import { useMemo } from 'react';
import moment from 'moment';
import 'moment/locale/es';
import CalendarioBase from '../CalendarioHorario/CalendarioBase';
import { ESTADO_CONFIG, CALENDAR_MESSAGES } from './constants';

moment.locale('es');

/**
 * Componente de calendario de asignaciones
 */
export default function CalendarioAsignaciones({ 
  consolidado, 
  view, 
  date, 
  onView, 
  onNavigate, 
  onSelectEvent 
}) {
  // Convertir asignaciones a eventos del calendario
  const eventos = useMemo(() => {
    if (!consolidado || !consolidado.asignacionesPorFecha) return [];

    // Aplanar y agrupar por fecha + turno
    const gruposPorFechaTurno = {};
    
    Object.entries(consolidado.asignacionesPorFecha).forEach(([fecha, asignacionesDia]) => {
      if (Array.isArray(asignacionesDia)) {
        asignacionesDia.forEach(asignacion => {
          const key = `${asignacion.fecha}-${asignacion.turnoId}`;
          if (!gruposPorFechaTurno[key]) {
            gruposPorFechaTurno[key] = {
              fecha: asignacion.fecha,
              turnoId: asignacion.turnoId,
              nombreTurno: asignacion.nombreTurno,
              horaInicio: asignacion.horaInicio,
              horaFin: asignacion.horaFin,
              asignaciones: []
            };
          }
          gruposPorFechaTurno[key].asignaciones.push(asignacion);
        });
      }
    });

    console.log('Grupos por fecha y turno:', Object.keys(gruposPorFechaTurno).length);

    // Convertir grupos a eventos
    const eventosGenerados = Object.values(gruposPorFechaTurno).map((grupo) => {
      // Determinar el color basado en el estado predominante
      const estadosPorConteo = grupo.asignaciones.reduce((acc, a) => {
        acc[a.estado] = (acc[a.estado] || 0) + 1;
        return acc;
      }, {});

      const estadoPredominante = Object.entries(estadosPorConteo)
        .sort((a, b) => b[1] - a[1])[0][0];

      const config = ESTADO_CONFIG[estadoPredominante] || ESTADO_CONFIG.PLANIFICADO;

      // Parsear fecha correctamente sin problemas de zona horaria
      const [year, month, day] = grupo.fecha.split('-').map(Number);
      const [horaInicioH, horaInicioM] = (grupo.horaInicio || '08:00:00').split(':').map(Number);
      const [horaFinH, horaFinM] = (grupo.horaFin || '17:00:00').split(':').map(Number);

      // Crear fechas en zona horaria local (no UTC)
      const start = new Date(year, month - 1, day, horaInicioH, horaInicioM, 0);
      const end = new Date(year, month - 1, day, horaFinH, horaFinM, 0);

      const cantidadAsignaciones = grupo.asignaciones.length;

      return {
        id: `${grupo.fecha}-${grupo.turnoId}`,
        title: `${grupo.nombreTurno} (${cantidadAsignaciones})`,
        start,
        end,
        resource: {
          fecha: grupo.fecha,
          turnoId: grupo.turnoId,
          turnoNombre: grupo.nombreTurno,
          asignaciones: grupo.asignaciones,
          cantidadAsignaciones,
          color: config.colorHex,
          estadoPredominante
        }
      };
    });

    console.log('Eventos generados para el calendario:', eventosGenerados.length);
    if (eventosGenerados.length > 0) {
      console.log('Primer evento:', eventosGenerados[0]);
    }

    return eventosGenerados;
  }, [consolidado]);

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6">
      <h3 className="text-lg font-bold text-gray-900 mb-4">Calendario de Asignaciones</h3>
      <CalendarioBase
        eventos={eventos}
        view={view}
        date={date}
        onView={onView}
        onNavigate={onNavigate}
        onSelectEvent={onSelectEvent}
        selectable={false}
        accentColor="blue"
        messages={CALENDAR_MESSAGES}
      />
    </div>
  );
}
