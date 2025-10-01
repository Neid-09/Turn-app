import { Layout, Card, CardHeader, CardContent, CardTitle, Button } from '../components/ui';
import { ShiftCard, QuickActionEmployee, NextShiftCard } from '../components/dashboard/EmployeeComponents';
import { Clock, Settings, FileText, Bell, Calendar, Home } from 'lucide-react';

export const EmployeeDashboard = () => {
  const currentShift = {
    time: 'Tu próximo turno es hoy a las 00:00',
    schedule: '00:00 - 00:00',
    description: 'Descanso 00:00 - 00:00',
    status: 'Activo'
  };

  const weeklyStats = [
    { title: '00h', subtitle: 'Esta semana' },
    { title: '00', subtitle: 'Próximos turnos' }
  ];

  const quickActions = [
    {
      title: 'Configuración',
      icon: Settings,
      onClick: () => alert('Función en desarrollo')
    },
    {
      title: 'Informes',
      icon: FileText,
      onClick: () => alert('Función en desarrollo')
    }
  ];

  return (
    <Layout>
      <div className="space-y-8 pb-24">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Panel empleado</h1>
          <p className="text-gray-600">¡Hola, Juan Pérez!</p>
          <p className="text-sm text-gray-500">Empleado(a)</p>
        </div>

        {/* Current Shift */}
        <Card className="p-6 border-l-4 border-l-blue-500">
          <div className="flex items-center space-x-3 mb-4">
            <Clock className="h-5 w-5 text-blue-600" />
            <span className="text-sm font-medium text-gray-900">{currentShift.time}</span>
          </div>
          <div className="text-lg font-semibold text-gray-900 mb-2">{currentShift.schedule}</div>
          <div className="text-sm text-gray-600 mb-4">{currentShift.description}</div>
          <Button size="sm" className="bg-blue-600 hover:bg-blue-700">
            Activo
          </Button>
        </Card>

        {/* Weekly Stats */}
        <div className="grid grid-cols-2 gap-4">
          {weeklyStats.map((stat, index) => (
            <ShiftCard
              key={index}
              title={stat.subtitle}
              hours={stat.title}
              status={index === 0 ? 'Activo' : 'Inactive'}
            />
          ))}
        </div>

        {/* Quick Actions */}
        <div>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Acciones rápidas</h2>
          <div className="grid grid-cols-2 gap-4">
            {quickActions.map((action, index) => (
              <QuickActionEmployee
                key={index}
                title={action.title}
                icon={action.icon}
                onClick={action.onClick}
              />
            ))}
          </div>
        </div>

        {/* Bottom Navigation */}
        <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-3">
          <div className="flex justify-around max-w-md mx-auto">
            <button className="flex flex-col items-center space-y-1 text-blue-600">
              <div className="p-2">
                <Home className="h-6 w-6" />
              </div>
              <span className="text-xs">Inicio</span>
            </button>
            <button className="flex flex-col items-center space-y-1 text-gray-400">
              <div className="p-2">
                <Calendar className="h-6 w-6" />
              </div>
              <span className="text-xs">Horarios</span>
            </button>
            <button className="flex flex-col items-center space-y-1 text-gray-400">
              <div className="p-2">
                <Clock className="h-6 w-6" />
              </div>
              <span className="text-xs">Asistencia</span>
            </button>
            <button className="flex flex-col items-center space-y-1 text-gray-400">
              <div className="p-2">
                <Bell className="h-6 w-6" />
              </div>
              <span className="text-xs">Avisos</span>
            </button>
            <button className="flex flex-col items-center space-y-1 text-gray-400">
              <div className="p-2">
                <Settings className="h-6 w-6" />
              </div>
              <span className="text-xs">Perfil</span>
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
};