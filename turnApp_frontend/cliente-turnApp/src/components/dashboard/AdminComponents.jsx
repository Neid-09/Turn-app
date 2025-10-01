import { Card, CardHeader, CardContent, CardTitle, Button } from '../ui';
import { Users, Clock, FileText, Bell } from 'lucide-react';

export const StatsCard = ({ title, value, icon: Icon, color = 'blue' }) => {
  const colorClasses = {
    blue: 'bg-blue-50 text-blue-600',
    green: 'bg-green-50 text-green-600',
    yellow: 'bg-yellow-50 text-yellow-600',
    red: 'bg-red-50 text-red-600'
  };

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-3xl font-bold text-gray-900">{value}</p>
        </div>
        <div className={`p-3 rounded-full ${colorClasses[color]}`}>
          <Icon className="h-6 w-6" />
        </div>
      </div>
    </Card>
  );
};

export const QuickActionCard = ({ title, description, icon: Icon, onClick }) => {
  return (
    <Card className="p-6 hover:shadow-lg transition-shadow cursor-pointer" onClick={onClick}>
      <div className="flex items-center space-x-4">
        <div className="p-3 bg-blue-50 rounded-full">
          <Icon className="h-6 w-6 text-blue-600" />
        </div>
        <div className="flex-1">
          <h3 className="font-medium text-gray-900">{title}</h3>
          <p className="text-sm text-gray-600">{description}</p>
        </div>
      </div>
    </Card>
  );
};

export const RecentScheduleItem = ({ employeeName, department, hours, status }) => {
  const statusColors = {
    'Publicado': 'bg-green-100 text-green-800',
    'Borrador': 'bg-yellow-100 text-yellow-800',
    'Pendiente': 'bg-blue-100 text-blue-800'
  };

  return (
    <div className="flex items-center justify-between py-3 border-b border-gray-200 last:border-b-0">
      <div className="flex-1">
        <p className="font-medium text-gray-900">{employeeName}</p>
        <p className="text-sm text-gray-600">{department} • {hours}</p>
      </div>
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${statusColors[status]}`}>
        {status}
      </span>
      <Button variant="ghost" size="sm" className="ml-2">
        Ver detalles
      </Button>
    </div>
  );
};