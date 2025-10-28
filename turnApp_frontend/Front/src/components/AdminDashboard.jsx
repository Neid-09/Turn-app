// src/components/AdminDashboard.jsx
import React from 'react';
import {
  FiUsers,
  FiCalendar,
  FiFileText,
  FiBell
} from 'react-icons/fi';

function StatCard({ title, value, icon: Icon }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-4 flex items-center justify-between">
      <div>
        <p className="text-gray-500">{title}</p>
        <p className="text-2xl font-bold text-black">{value}</p>
      </div>
      <Icon className="w-8 h-8 text-gray-400" />
    </div>
  );
}

// Botón de Acción Rápida
function ActionButton({ label, icon: Icon }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-4 flex items-center space-x-4 cursor-pointer hover:bg-gray-50">
      <div className="bg-gray-100 p-3 rounded-full">
        <Icon className="w-5 h-5 text-gray-600" />
      </div>
      <span className="text-base font-medium text-gray-800">{label}</span>
    </div>
  );
}

// Item de Horario Reciente
function ScheduleItem({ title, subtitle, status }) {
  let statusClass = '';
  switch (status) {
    case 'Publicado':
      statusClass = 'border-green-500 text-green-600 bg-green-50';
      break;
    case 'Borrador':
      statusClass = 'border-yellow-500 text-yellow-600 bg-yellow-50';
      break;
    case 'Pendiente':
      statusClass = 'border-blue-500 text-blue-600 bg-blue-50';
      break;
    default:
      statusClass = 'border-gray-400 text-gray-500 bg-gray-50';
  }

  return (
    <div className="bg-white rounded-2xl shadow-sm p-4 flex items-center justify-between">
      <div>
        <p className="font-semibold text-black">{title}</p>
        <p className="text-sm text-gray-500">{subtitle}</p>
      </div>
      <span className={`text-xs font-medium px-3 py-1 border rounded-full ${statusClass}`}>
        {status}
      </span>
    </div>
  );
}

// --- Componente Principal del Dashboard ---
export default function AdminDashboard() {
  return (
    <div className="p-4">
      {/* --- Cabecera --- */}
      <header className="mb-4">
        <h1 className="text-2xl font-bold text-black">Panel Administrativo</h1>
        <p className="text-lg text-gray-600">Bienvenido, nombre_user</p>
      </header>

      {/* --- Tarjetas de Estadísticas --- */}
      <div className="grid grid-cols-2 gap-4 mb-6">
        <StatCard title="Empleados" value="00" icon={FiUsers} />
        <StatCard title="Horarios" value="00" icon={FiCalendar} />
        <StatCard title="Solicitudes" value="00" icon={FiFileText} />
        <StatCard title="Avisos" value="00" icon={FiBell} />
      </div>

      {/* --- Acciones Rápidas --- */}
      <div className="mb-6">
        <h2 className="text-lg font-semibold text-black mb-3">Acciones rápidas</h2>
        <div className="space-y-3">
          <ActionButton label="Crear nuevo horario" icon={FiCalendar} />
          <ActionButton label="Gestionar empleados" icon={FiUsers} />
        </div>
      </div>

      {/* --- Horarios Recientes --- */}
      <div>
        <div className="flex justify-between items-center mb-3">
          <h2 className="text-lg font-semibold text-black">Horarios recientes</h2>
          <button className="bg-black text-white px-4 py-1.5 rounded-lg font-medium text-sm">
            Ver todos
          </button>
        </div>
        <div className="space-y-3">
          <ScheduleItem
            title="Horario Turno Mañana"
            subtitle="Departamento - 00 empleados"
            status="Publicado"
          />
          <ScheduleItem
            title="Horario Turno Tarde"
            subtitle="Departamento - 00 empleados"
            status="Borrador"
          />
          <ScheduleItem
            title="Horario Turno Noche"
            subtitle="Departamento - 00 empleados"
            status="Pendiente"
          />
        </div>
      </div>
    </div>
  );
}