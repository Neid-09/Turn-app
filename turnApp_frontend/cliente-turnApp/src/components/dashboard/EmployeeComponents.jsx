import { Card, CardContent, Button } from '../ui';
import { Clock, Settings, FileText } from 'lucide-react';

export const ShiftCard = ({ title, hours, status }) => {
  const isActive = status === 'Activo';
  
  return (
    <Card className={`p-6 border-2 ${isActive ? 'border-blue-500 bg-blue-50' : 'border-gray-200'}`}>
      <div className="text-center">
        <h3 className="text-2xl font-bold text-gray-900 mb-2">{hours}</h3>
        <p className="text-sm text-gray-600">{title}</p>
      </div>
    </Card>
  );
};

export const QuickActionEmployee = ({ title, icon: Icon, onClick }) => {
  return (
    <Card className="p-6 hover:shadow-lg transition-shadow cursor-pointer" onClick={onClick}>
      <div className="flex flex-col items-center space-y-3">
        <div className="p-3 bg-gray-50 rounded-full">
          <Icon className="h-6 w-6 text-gray-600" />
        </div>
        <span className="text-sm font-medium text-gray-900">{title}</span>
      </div>
    </Card>
  );
};

export const NextShiftCard = ({ time, description, isToday = false }) => {
  return (
    <Card className={`p-4 ${isToday ? 'border-blue-500 bg-blue-50' : ''}`}>
      <div className="flex items-center space-x-3">
        <div className="p-2 bg-blue-100 rounded-full">
          <Clock className="h-5 w-5 text-blue-600" />
        </div>
        <div className="flex-1">
          <p className="font-medium text-gray-900">{time}</p>
          <p className="text-sm text-gray-600">{description}</p>
        </div>
        {isToday && (
          <span className="text-xs font-medium text-blue-600 bg-blue-100 px-2 py-1 rounded-full">
            Hoy
          </span>
        )}
      </div>
    </Card>
  );
};