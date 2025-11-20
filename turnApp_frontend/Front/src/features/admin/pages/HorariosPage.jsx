import { useState } from 'react';
import { FiCalendar, FiClock } from 'react-icons/fi';
import HorarioListTab from '../components/HorarioListTab';
import GestionTurnosTab from '../components/GestionTurnosTab';

export default function HorariosPage() {
  const [activeTab, setActiveTab] = useState('horarios'); // 'horarios' | 'turnos'

  return (
    <div className="min-h-screen bg-linear-to-br from-gray-50 to-gray-100">
      {/* Header */}
      <div className="bg-white shadow-md border-b border-gray-200">
        <div className="px-6 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
                <div className="w-10 h-10 bg-linear-to-br from-purple-500 to-purple-600 rounded-lg flex items-center justify-center">
                  <FiCalendar className="w-6 h-6 text-white" />
                </div>
                Gestión de Horarios
              </h1>
              <p className="text-sm text-gray-500 mt-2 ml-13">
                Planifica y administra horarios y turnos de trabajo
              </p>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="px-6">
          <div className="flex gap-4 border-b border-gray-200">
            <button
              onClick={() => setActiveTab('horarios')}
              className={`
                flex items-center gap-2 px-4 py-3 font-medium transition-all
                ${activeTab === 'horarios'
                  ? 'text-purple-600 border-b-2 border-purple-600'
                  : 'text-gray-500 hover:text-gray-700'
                }
              `}
            >
              <FiCalendar className="w-5 h-5" />
              Horarios Planificados
            </button>
            <button
              onClick={() => setActiveTab('turnos')}
              className={`
                flex items-center gap-2 px-4 py-3 font-medium transition-all
                ${activeTab === 'turnos'
                  ? 'text-purple-600 border-b-2 border-purple-600'
                  : 'text-gray-500 hover:text-gray-700'
                }
              `}
            >
              <FiClock className="w-5 h-5" />
              Gestión de Turnos
            </button>
          </div>
        </div>
      </div>

      {/* Contenido de tabs */}
      <div className="px-6 py-8 max-w-7xl mx-auto">
        {activeTab === 'horarios' && <HorarioListTab />}
        {activeTab === 'turnos' && <GestionTurnosTab />}
      </div>
    </div>
  );
}
