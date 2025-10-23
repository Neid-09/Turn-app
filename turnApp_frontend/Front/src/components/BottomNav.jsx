// src/components/BottomNav.jsx
import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  FiHome,
  FiCalendar,
  FiClock,
  FiBell,
  FiUser
} from 'react-icons/fi';

// Componente para cada ítem de navegación
function NavItem({ to, icon: Icon, label }) {
  const location = useLocation();
  const isActive = location.pathname === to;

  return (
    <Link
      to={to}
      className={`flex flex-col items-center justify-center p-2 rounded-lg ${
        isActive ? 'bg-gray-100' : ''
      }`}
    >
      <Icon
        className={`w-6 h-6 ${
          isActive ? 'text-black' : 'text-gray-500'
        }`}
      />
      <span
        className={`text-xs mt-1 ${
          isActive ? 'font-medium text-black' : 'text-gray-500'
        }`}
      >
        {label}
      </span>
    </Link>
  );
}

export default function BottomNav() {
  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white shadow-[0_-2px_5px_rgba(0,0,0,0.05)] h-20 flex justify-around items-center px-4">
      <NavItem to="/" icon={FiHome} label="Inicio" />
      <NavItem to="/horario" icon={FiCalendar} label="Horario" />
      <NavItem to="/asistencia" icon={FiClock} label="Asistencia" />
      <NavItem to="/avisos" icon={FiBell} label="Avisos" />
      <NavItem to="/perfil" icon={FiUser} label="Perfil" />
    </nav>
  );
}
