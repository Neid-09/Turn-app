// src/components/EmployeeDashboard.jsx
import React from 'react';
import { FiClock, FiSettings, FiFileText } from 'react-icons/fi';

export default function EmployeeDashboard() {
  return (
    <div className="p-4">
      {/* --- Cabecera --- */}
      <header className="mb-4">
        <h1 className="text-2xl font-bold text-black">Panel empleado</h1>
        <p className="text-lg text-gray-600">¡Hola, Juan Pérez!</p>
        <span className="text-sm text-gray-400">Empleado(a)</span>
      </header>

      {/* --- Próximo Turno --- */}
      <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center space-x-2">
            <FiClock className="w-6 h-6 text-gray-500" />
            <span className="text-lg font-semibold text-black">Tu próximo turno es hoy a las 00:00</span>
          </div>
        </div>
        <div className="flex items-center justify-between">
          <div>
            <p className="text-gray-800 font-medium">00:00 - 00:00</p>
            <p className="text-sm text-gray-500">Descanso: 00:00 - 00:00</p>
          </div>
          <button className="bg-black text-white px-5 py-2 rounded-lg font-medium text-sm">
            Activo
          </button>
        </div>
      </div>

      {/* --- Estadísticas --- */}
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div className="bg-white rounded-2xl shadow-sm p-4 flex flex-col items-center justify-center h-28">
          <p className="text-3xl font-bold text-black">00h</p>
          <p className="text-sm text-gray-500">Esta semana</p>
        </div>
        <div className="bg-white rounded-2xl shadow-sm p-4 flex flex-col items-center justify-center h-28">
          <p className="text-3xl font-bold text-black">00</p>
          <p className="text-sm text-gray-500">Próximos turnos</p>
        </div>
      </div>

      {/* --- Acciones Rápidas --- */}
      <div>
        <h2 className="text-lg font-semibold text-black mb-3">Acciones rápidas</h2>
        <div className="space-y-3">
          {/* Acción 1 */}
          <div className="bg-white rounded-2xl shadow-sm p-4 flex items-center space-x-4">
            <div className="bg-gray-100 p-3 rounded-full">
              <FiSettings className="w-5 h-5 text-gray-600" />
            </div>
            <span className="text-base font-medium text-gray-800">Configuración</span>
          </div>
          {/* Acción 2 */}
          <div className="bg-white rounded-2xl shadow-sm p-4 flex items-center space-x-4">
            <div className="bg-gray-100 p-3 rounded-full">
              <FiFileText className="w-5 h-5 text-gray-600" />
            </div>
            <span className="text-base font-medium text-gray-800">Solicitudes</span>
          </div>
        </div>
      </div>
    </div>
  );
}