import React, { useState } from "react";
import { FiMapPin } from "react-icons/fi";

export default function Horario() {
  const [tab, setTab] = useState("proximos");

  const proximosTurnos = [
    { dia: "2025-10-25", hora: "08:00 - 16:00", ubicacion: "Sede Norte", estado: "Confirmado" },
    { dia: "2025-10-26", hora: "10:00 - 18:00", ubicacion: "Sede Sur", estado: "Confirmado" },
    { dia: "2025-10-28", hora: "09:00 - 15:00", ubicacion: "Sede Central", estado: "Pendiente" },
  ];

  const historialTurnos = [
    { dia: "2025-10-15", hora: "08:00 - 16:00", ubicacion: "Sede Norte", estado: "Completado" },
    { dia: "2025-10-17", hora: "09:00 - 17:00", ubicacion: "Sede Central", estado: "Completado" },
  ];

  const turnos = tab === "proximos" ? proximosTurnos : historialTurnos;

  return (
    <div className="p-4 font-sans bg-[#f7f8fa] min-h-screen">
      <h1 className="text-xl font-semibold mb-4">Horario</h1>

      {/* Tabs */}
      <div className="flex bg-gray-300 rounded-full p-1 mb-4">
        <button
          onClick={() => setTab("proximos")}
          className={`flex-1 py-2 rounded-full font-medium transition-all ${
            tab === "proximos"
              ? "bg-white text-black shadow-sm"
              : "text-gray-700"
          }`}
        >
          Próximos turnos
        </button>
        <button
          onClick={() => setTab("historial")}
          className={`flex-1 py-2 rounded-full font-medium transition-all ${
            tab === "historial"
              ? "bg-white text-black shadow-sm"
              : "text-gray-700"
          }`}
        >
          Historial
        </button>
      </div>

      {/* Lista de turnos */}
      <div className="space-y-3">
        {turnos.map((turno, i) => (
          <div key={i} className="bg-white p-4 rounded-md shadow-sm">
            <p className="font-semibold mb-2">Día</p>
            <div className="flex justify-between items-center">
              <div>
                <p className="text-sm">{turno.dia}</p>
                <p className="text-lg font-bold">{turno.hora}</p>
                <div className="flex items-center mt-1 text-sm text-gray-600">
                  <FiMapPin className="mr-1" />
                  <span>{turno.ubicacion}</span>
                </div>
              </div>
              <span
                className={`px-4 py-1 rounded-full text-xs font-medium ${
                  turno.estado === "Confirmado"
                    ? "bg-black text-white"
                    : turno.estado === "Pendiente"
                    ? "border border-black text-black"
                    : "bg-gray-200 text-gray-700"
                }`}
              >
                {turno.estado}
              </span>
            </div>
          </div>
        ))}
      </div>

      {/* Si no hay turnos */}
      {turnos.length === 0 && (
        <p className="text-center text-gray-500 mt-10">
          No hay {tab === "proximos" ? "próximos turnos" : "turnos anteriores"}.
        </p>
      )}
    </div>
  );
}
