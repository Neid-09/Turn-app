/**
 * Parsea una fecha ISO sin conversión de zona horaria
 * @param {string} dateString - Fecha en formato ISO
 * @returns {Date} Objeto Date
 */
export const parseLocalDate = (dateString) => {
  if (!dateString) return new Date();
  const [year, month, day] = dateString.split('T')[0].split('-');
  return new Date(year, month - 1, day);
};

/**
 * Genera un color consistente basado en el ID del turno
 * @param {string|number} turnoId - ID del turno
 * @returns {string} Color en formato hexadecimal
 */
export const generarColorPorTurno = (turnoId) => {
  const colores = [
    '#8B5CF6', // purple
    '#3B82F6', // blue
    '#10B981', // green
    '#F59E0B', // yellow
    '#EF4444', // red
    '#EC4899', // pink
  ];
  const hash = turnoId ? turnoId.toString().charCodeAt(0) : 0;
  return colores[hash % colores.length];
};

/**
 * Agrupa detalles por fecha y turno
 * @param {Array} detalles - Lista de detalles
 * @returns {Object} Detalles agrupados por fecha-turno
 */
export const agruparDetallesPorFechaTurno = (detalles) => {
  return detalles.reduce((acc, detalle) => {
    const key = `${detalle.fecha}-${detalle.turnoId}`;
    if (!acc[key]) {
      acc[key] = {
        fecha: detalle.fecha,
        turnoId: detalle.turnoId,
        nombreTurno: detalle.nombreTurno,
        detalles: []
      };
    }
    acc[key].detalles.push(detalle);
    return acc;
  }, {});
};

/**
 * Convierte grupos de detalles a eventos del calendario
 * @param {Object} gruposPorFechaTurno - Detalles agrupados
 * @param {Array} turnos - Lista de turnos disponibles
 * @returns {Array} Lista de eventos para el calendario
 */
export const convertirGruposAEventos = (gruposPorFechaTurno, turnos) => {
  return Object.values(gruposPorFechaTurno).map((grupo) => {
    const turno = turnos.find(t => t.id === grupo.turnoId);
    
    const [horaInicioH, horaInicioM] = (turno?.horaInicio || '08:00').split(':').map(Number);
    const [horaFinH, horaFinM] = (turno?.horaFin || '17:00').split(':').map(Number);

    const start = parseLocalDate(grupo.fecha);
    start.setHours(horaInicioH, horaInicioM, 0);

    const end = parseLocalDate(grupo.fecha);
    end.setHours(horaFinH, horaFinM, 0);

    const nombreTurno = grupo.nombreTurno || turno?.nombre || 'Turno';
    const cantidadEmpleados = grupo.detalles.length;

    return {
      id: `${grupo.fecha}-${grupo.turnoId}`,
      title: `${nombreTurno} (${cantidadEmpleados})`,
      start,
      end,
      resource: {
        fecha: grupo.fecha,
        turnoId: grupo.turnoId,
        turnoNombre: nombreTurno,
        turno: turno,
        detalles: grupo.detalles,
        cantidadEmpleados,
        color: generarColorPorTurno(grupo.turnoId)
      }
    };
  });
};
