import { FiBarChart2 } from 'react-icons/fi';

/**
 * Panel de estadísticas de sincronización
 */
export default function PanelEstadisticas({ consolidado }) {
  const stats = consolidado.estadisticas || {};
  
  const estadisticas = [
    {
      label: 'Planificadas',
      valor: stats.totalPlanificadas || 0,
      color: 'bg-blue-100 text-blue-700 border-blue-200',
      icon: '📋'
    },
    {
      label: 'Confirmadas',
      valor: stats.totalConfirmadas || 0,
      color: 'bg-green-100 text-green-700 border-green-200',
      icon: '✅'
    },
    {
      label: 'Completadas',
      valor: stats.totalCompletadas || 0,
      color: 'bg-gray-100 text-gray-700 border-gray-200',
      icon: '✓'
    },
    {
      label: 'Canceladas',
      valor: stats.totalCanceladas || 0,
      color: 'bg-red-100 text-red-700 border-red-200',
      icon: '✗'
    }
  ];

  const porcentajeSincronizacion = stats.porcentajeSincronizacion || 0;

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6 mb-6">
      <div className="flex items-center gap-2 mb-4">
        <FiBarChart2 className="w-5 h-5 text-gray-700" />
        <h3 className="text-lg font-bold text-gray-900">Estadísticas de Sincronización</h3>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {estadisticas.map((stat, index) => (
          <div key={index} className={`rounded-lg border p-4 ${stat.color}`}>
            <div className="flex items-center gap-2 mb-2">
              <span className="text-2xl">{stat.icon}</span>
              <p className="text-sm font-medium">{stat.label}</p>
            </div>
            <p className="text-3xl font-bold">{stat.valor}</p>
          </div>
        ))}
      </div>

      <div>
        <div className="flex items-center justify-between mb-2">
          <p className="text-sm font-medium text-gray-700">Porcentaje de Sincronización</p>
          <span className="text-lg font-bold text-gray-900">{porcentajeSincronizacion}%</span>
        </div>
        <div className="h-4 bg-gray-200 rounded-full overflow-hidden">
          <div
            className={`h-full transition-all ${
              porcentajeSincronizacion === 100
                ? 'bg-green-500'
                : porcentajeSincronizacion >= 70
                ? 'bg-yellow-500'
                : 'bg-red-500'
            }`}
            style={{ width: `${porcentajeSincronizacion}%` }}
          ></div>
        </div>
        <p className="text-xs text-gray-500 mt-2">
          Indica qué porcentaje de asignaciones planificadas están confirmadas en el sistema real
        </p>
      </div>
    </div>
  );
}
