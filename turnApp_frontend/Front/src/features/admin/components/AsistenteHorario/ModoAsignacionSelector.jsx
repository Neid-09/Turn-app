export default function ModoAsignacionSelector({ 
  modoAsignacion, 
  setModoAsignacion,
  fechas,
  fechaSeleccionada,
  diasSeleccionados,
  setDiasSeleccionados 
}) {
  const diasSemana = [
    { valor: 0, label: 'L' },   // Lunes
    { valor: 1, label: 'M' },   // Martes
    { valor: 2, label: 'X' },   // Miércoles
    { valor: 3, label: 'J' },   // Jueves
    { valor: 4, label: 'V' },   // Viernes
    { valor: 5, label: 'S' },   // Sábado
    { valor: 6, label: 'D' }    // Domingo
  ];

  const toggleDia = (diaIndex) => {
    setDiasSeleccionados(prev =>
      prev.includes(diaIndex)
        ? prev.filter(d => d !== diaIndex)
        : [...prev, diaIndex]
    );
  };

  const contarDiasSemana = () => {
    return fechas.filter(f => {
      const d = new Date(f + 'T00:00:00').getDay();
      return d >= 1 && d <= 5;
    }).length;
  };

  const contarFinSemana = () => {
    return fechas.filter(f => {
      const d = new Date(f + 'T00:00:00').getDay();
      return d === 0 || d === 6;
    }).length;
  };

  const contarDiasPersonalizados = () => {
    return fechas.filter(f => {
      const dia = new Date(f + 'T00:00:00').getDay();
      const diaAjustado = dia === 0 ? 6 : dia - 1;
      return diasSeleccionados.includes(diaAjustado);
    }).length;
  };

  return (
    <div className="mb-6 bg-gray-50 rounded-lg p-4 border border-gray-200">
      <h4 className="text-sm font-semibold text-gray-700 mb-3">Modo de Asignación</h4>
      <div className="space-y-2">
        <label className="flex items-center gap-3 p-3 bg-white border border-gray-200 rounded-lg hover:bg-purple-50 cursor-pointer transition-colors">
          <input
            type="radio"
            name="modoAsignacion"
            value="individual"
            checked={modoAsignacion === 'individual'}
            onChange={(e) => setModoAsignacion(e.target.value)}
            className="w-4 h-4 text-purple-600"
          />
          <div className="flex-1">
            <p className="font-medium text-gray-900 text-sm">Solo este día</p>
            <p className="text-xs text-gray-500">
              Asignar únicamente para {new Date(fechaSeleccionada + 'T00:00:00').toLocaleDateString('es-ES', { day: 'numeric', month: 'short' })}
            </p>
          </div>
        </label>

        <label className="flex items-center gap-3 p-3 bg-white border border-gray-200 rounded-lg hover:bg-purple-50 cursor-pointer transition-colors">
          <input
            type="radio"
            name="modoAsignacion"
            value="periodo"
            checked={modoAsignacion === 'periodo'}
            onChange={(e) => setModoAsignacion(e.target.value)}
            className="w-4 h-4 text-purple-600"
          />
          <div className="flex-1">
            <p className="font-medium text-gray-900 text-sm">Todo el período</p>
            <p className="text-xs text-gray-500">Asignar todos los días del horario ({fechas.length} días)</p>
          </div>
        </label>

        <label className="flex items-center gap-3 p-3 bg-white border border-gray-200 rounded-lg hover:bg-purple-50 cursor-pointer transition-colors">
          <input
            type="radio"
            name="modoAsignacion"
            value="semana"
            checked={modoAsignacion === 'semana'}
            onChange={(e) => setModoAsignacion(e.target.value)}
            className="w-4 h-4 text-purple-600"
          />
          <div className="flex-1">
            <p className="font-medium text-gray-900 text-sm">Entre semana (Lun-Vie)</p>
            <p className="text-xs text-gray-500">
              Asignar solo días laborales ({contarDiasSemana()} días)
            </p>
          </div>
        </label>

        <label className="flex items-center gap-3 p-3 bg-white border border-gray-200 rounded-lg hover:bg-purple-50 cursor-pointer transition-colors">
          <input
            type="radio"
            name="modoAsignacion"
            value="finSemana"
            checked={modoAsignacion === 'finSemana'}
            onChange={(e) => setModoAsignacion(e.target.value)}
            className="w-4 h-4 text-purple-600"
          />
          <div className="flex-1">
            <p className="font-medium text-gray-900 text-sm">Fines de semana (Sáb-Dom)</p>
            <p className="text-xs text-gray-500">
              Asignar solo sábados y domingos ({contarFinSemana()} días)
            </p>
          </div>
        </label>

        <label className="flex items-center gap-3 p-3 bg-white border border-gray-200 rounded-lg hover:bg-purple-50 cursor-pointer transition-colors">
          <input
            type="radio"
            name="modoAsignacion"
            value="personalizado"
            checked={modoAsignacion === 'personalizado'}
            onChange={(e) => setModoAsignacion(e.target.value)}
            className="w-4 h-4 text-purple-600"
          />
          <div className="flex-1">
            <p className="font-medium text-gray-900 text-sm">Días personalizados</p>
            <p className="text-xs text-gray-500">Elegir qué días de la semana asignar</p>
          </div>
        </label>
      </div>

      {/* Selector de días personalizados */}
      {modoAsignacion === 'personalizado' && (
        <div className="mt-4 p-3 bg-white border border-gray-200 rounded-lg">
          <p className="text-xs font-medium text-gray-700 mb-2">Selecciona los días:</p>
          <div className="grid grid-cols-7 gap-2">
            {diasSemana.map(dia => (
              <button
                key={dia.valor}
                type="button"
                onClick={() => toggleDia(dia.valor)}
                className={`p-2 text-xs font-medium rounded transition-colors ${
                  diasSeleccionados.includes(dia.valor)
                    ? 'bg-purple-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                {dia.label}
              </button>
            ))}
          </div>
          {diasSeleccionados.length > 0 && (
            <p className="text-xs text-gray-600 mt-2">
              Se asignarán {contarDiasPersonalizados()} días
            </p>
          )}
        </div>
      )}
    </div>
  );
}
