import { Layout, Card, CardHeader, CardContent, CardTitle, Button } from '../components/ui';
import { StatsCard, QuickActionCard, RecentScheduleItem } from '../components/dashboard/AdminComponents';
import { Users, Clock, FileText, Bell, Calendar, UserPlus, Settings } from 'lucide-react';

export const AdminDashboard = () => {
  const stats = [
    { title: 'Empleados', value: '00', icon: Users, color: 'blue' },
    { title: 'Horarios', value: '00', icon: Clock, color: 'green' },
    { title: 'Solicitudes', value: '00', icon: FileText, color: 'yellow' },
    { title: 'Avisos', value: '00', icon: Bell, color: 'red' }
  ];

  const quickActions = [
    {
      title: 'Crear nuevo horario',
      description: 'Programa turnos para tus empleados',
      icon: Calendar,
      onClick: () => alert('Función en desarrollo')
    },
    {
      title: 'Gestionar empleados',
      description: 'Administra tu equipo de trabajo',
      icon: UserPlus,
      onClick: () => alert('Función en desarrollo')
    }
  ];

  const recentSchedules = [
    {
      employeeName: 'Horario Turno Mañana',
      department: 'Departamento • 00 empleados',
      hours: '',
      status: 'Publicado'
    },
    {
      employeeName: 'Horario Turno Mañana',
      department: 'Departamento • 00 empleados',
      hours: '',
      status: 'Borrador'
    },
    {
      employeeName: 'Horario Turno Mañana',
      department: 'Departamento • 00 empleados',
      hours: '',
      status: 'Pendiente'
    }
  ];

  return (
    <Layout>
      <div className="space-y-8">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Panel Administrativo</h1>
          <p className="text-gray-600">Bienvenido, nombre.user</p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((stat, index) => (
            <StatsCard
              key={index}
              title={stat.title}
              value={stat.value}
              icon={stat.icon}
              color={stat.color}
            />
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Quick Actions */}
          <div className="space-y-6">
            <h2 className="text-lg font-semibold text-gray-900">Acciones rápidas</h2>
            <div className="space-y-4">
              {quickActions.map((action, index) => (
                <QuickActionCard
                  key={index}
                  title={action.title}
                  description={action.description}
                  icon={action.icon}
                  onClick={action.onClick}
                />
              ))}
            </div>
          </div>

          {/* Recent Schedules */}
          <div>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold text-gray-900">Horarios recientes</h2>
              <Button variant="outline" size="sm">
                Ver todos
              </Button>
            </div>
            
            <Card>
              <CardContent className="p-0">
                <div className="divide-y divide-gray-200">
                  {recentSchedules.map((schedule, index) => (
                    <div key={index} className="p-4">
                      <RecentScheduleItem {...schedule} />
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Bottom Navigation Icons */}
        <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-3">
          <div className="flex justify-around max-w-md mx-auto">
            <button className="flex flex-col items-center space-y-1 text-blue-600">
              <div className="p-2">
                <Users className="h-6 w-6" />
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
                <Users className="h-6 w-6" />
              </div>
              <span className="text-xs">Empleados</span>
            </button>
            <button className="flex flex-col items-center space-y-1 text-gray-400">
              <div className="p-2">
                <FileText className="h-6 w-6" />
              </div>
              <span className="text-xs">Solicitudes</span>
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