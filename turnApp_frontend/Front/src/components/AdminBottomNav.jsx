// src/components/AdminBottomNav.jsx
import { NavLink } from 'react-router-dom';
import {
  FiHome,
  FiCalendar,
  FiUsers,
  FiFileText,
  FiBell,
  FiUser
} from 'react-icons/fi';

// Componente para cada ítem de navegación
function NavItem({ icon: Icon, label, to }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => 
        `flex flex-col items-center justify-center p-0 text-xs font-medium 
         ${isActive ? 'text-black' : 'text-gray-500'}`
      }
    >
      {({ isActive }) => (
        <>
          <div className={`flex flex-col items-center justify-center p-2 rounded-lg 
            ${isActive ? 'bg-gray-100' : ''}`}
          >
            <Icon className="w-6 h-6" />
          </div>
          <span className="text-xs mt-1">
            {label}
          </span>
        </>
      )}
    </NavLink>
  );
}

export default function AdminBottomNav() {
  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white shadow-[0_-2px_5px_rgba(0,0,0,0.05)] h-20 flex justify-around items-center px-4 z-50">
      <NavItem icon={FiHome} label="Inicio" to="/admin" />
      <NavItem icon={FiCalendar} label="Horarios" to="/admin/horarios" />
      <NavItem icon={FiUsers} label="Empleados" to="/admin/empleados" />
      <NavItem icon={FiFileText} label="Solicitudes" to="/admin/solicitudes" />
      <NavItem icon={FiBell} label="Avisos" to="/admin/avisos" />
      <NavItem icon={FiUser} label="Perfil" to="/admin/perfil" />
    </nav>
  );
}