import { FiClock, FiInfo, FiCheckCircle, FiLogIn, FiLogOut } from 'react-icons/fi';

export default function Asistencia() {
  return (
    <div className="p-4">
      <header className="mb-4">
        <h1 className="text-2xl font-bold text-black">Marcar Asistencia</h1>
      </header>

      {/* Reloj / tarjeta superior */}
      <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
        <div className="bg-gray-50 rounded-xl p-4 text-center mb-3">
          <p className="text-lg font-mono text-black">00:00:00</p>
          <p className="text-sm text-gray-500">día, 00 de mes de año</p>
        </div>
      </div>

      {/* Interfaz: Fuera de turno (primera tarjeta) */}
      <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
        <div className="flex items-start space-x-3">
          <div className="p-3 rounded-full bg-gray-100">
            <FiInfo className="w-5 h-5 text-gray-600" />
          </div>
          <div className="flex-1">
            <h3 className="text-base font-semibold text-black">Fuera de turno</h3>
            <p className="text-sm text-gray-500 mt-2">Aun no has registrado tu entrada</p>
          </div>
        </div>
      </div>

      {/* Horario de hoy - info compacta (reutilizable) */}
      <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
        <div className="flex items-center space-x-3 mb-3">
          <div className="p-3 rounded-full bg-gray-100">
            <FiClock className="w-5 h-5 text-gray-600" />
          </div>
          <h4 className="text-base font-semibold text-black">Horario de hoy</h4>
        </div>
        <div className="text-sm text-gray-600 grid grid-cols-2 gap-2">
          <div>
            <p className="text-xs text-gray-500">Entrada</p>
            <p className="text-sm font-medium">00:00:00</p>
          </div>
          <div>
            <p className="text-xs text-gray-500">Tiempo trabajado</p>
            <p className="text-sm font-medium">00:00:00</p>
          </div>
          <div>
            <p className="text-xs text-gray-500">Descanso</p>
            <p className="text-sm font-medium">00:00:00</p>
          </div>
        </div>
      </div>

      {/* Botones asociados a estado 'Fuera de turno' */}
      <div className="space-y-3 mb-6">
        <button className="w-full bg-black text-white py-3 rounded-lg font-medium flex items-center justify-center space-x-2">
          <FiLogIn />
          <span>Registrar entrada</span>
        </button>
        <button className="w-full border border-gray-300 text-gray-700 py-3 rounded-lg font-medium flex items-center justify-center space-x-2 bg-white">
          <span>Ver historial de asistencias</span>
        </button>
      </div>

      {/* Segunda interfaz: Registrado (apilada debajo) */}
      <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
        <div className="flex items-start space-x-3 border rounded-lg p-3">
          <div className="p-2 rounded-full bg-green-50">
            <FiCheckCircle className="w-6 h-6 text-green-600" />
          </div>
          <div className="flex-1">
            <h3 className="text-base font-semibold text-black">Registrado</h3>
            <p className="text-sm text-gray-500 mt-1">Entrada registrada</p>

            <div className="mt-3 text-sm text-gray-700">
              <p className="font-medium">00:00:00</p>
              <p className="text-xs text-gray-500">Tiempo trabajado</p>
              <p className="font-medium mt-2">00:00:00</p>
            </div>
          </div>
        </div>
      </div>

      {/* Botones asociados a estado 'Registrado' */}
      <div className="space-y-3 mb-24">
        <button className="w-full bg-black text-white py-3 rounded-lg font-medium flex items-center justify-center space-x-2">
          <FiLogOut />
          <span>Registrar salida</span>
        </button>
        <button className="w-full border border-gray-300 text-gray-700 py-3 rounded-lg font-medium flex items-center justify-center space-x-2 bg-white">
          <span>Ver historial de asistencias</span>
        </button>
      </div>
    </div>
  );
}
