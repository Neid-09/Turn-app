// src/components/EmployeeLayout.jsx
import React from 'react';
import { Outlet } from 'react-router-dom';
import BottomNav from './BottomNav';







export default function EmployeeLayout() {
  return (
    <div className="min-h-screen bg-[#f7f8fa] font-sans">
      {/* El Outlet renderiza el componente de la ruta actual (ej: EmployeeDashboard) */}
      {/* Añadimos padding-bottom para que el contenido no quede oculto por el BottomNav */}
      <main className="pb-24">
        <Outlet />
      </main>

      {/* La barra de navegación fija */}
      <BottomNav />
    </div>
  );
}