import { useState } from 'react';

export function useAsignaciones() {
  const [asignaciones, setAsignaciones] = useState([]);

  const agregarAsignaciones = (nuevasAsignaciones) => {
    setAsignaciones(prev => [...prev, ...nuevasAsignaciones]);
  };

  const eliminarAsignacion = (asignacionId) => {
    setAsignaciones(prev => prev.filter(a => a.id !== asignacionId));
  };

  const obtenerAsignacionesPorFechaTurno = (fecha, turnoId) => {
    return asignaciones.filter(a => a.fecha === fecha && a.turnoId === turnoId);
  };

  const obtenerAsignacionesPorEmpleado = () => {
    return asignaciones.reduce((acc, asig) => {
      if (!acc[asig.usuarioId]) {
        acc[asig.usuarioId] = {
          nombre: asig.nombreEmpleado,
          codigo: asig.codigoEmpleado,
          asignaciones: []
        };
      }
      acc[asig.usuarioId].asignaciones.push(asig);
      return acc;
    }, {});
  };

  const contarAsignacionesFueraPreferencia = () => {
    return asignaciones.filter(a => !a.cumplePreferencias && a.tienePreferencias).length;
  };

  return {
    asignaciones,
    setAsignaciones,
    agregarAsignaciones,
    eliminarAsignacion,
    obtenerAsignacionesPorFechaTurno,
    obtenerAsignacionesPorEmpleado,
    contarAsignacionesFueraPreferencia
  };
}
