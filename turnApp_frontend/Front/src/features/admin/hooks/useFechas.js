import { useMemo } from 'react';

export function useFechas(fechaInicio, fechaFin) {
  const fechas = useMemo(() => {
    const listaFechas = [];
    if (fechaInicio && fechaFin) {
      const inicio = new Date(fechaInicio);
      const fin = new Date(fechaFin);
      for (let fecha = new Date(inicio); fecha <= fin; fecha.setDate(fecha.getDate() + 1)) {
        listaFechas.push(fecha.toISOString().split('T')[0]);
      }
    }
    return listaFechas;
  }, [fechaInicio, fechaFin]);

  return fechas;
}
