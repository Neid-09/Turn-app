import { NavLink } from 'react-router-dom';
import {
  FiHome,
  FiCalendar,
  FiClock,
  FiBell,
  FiUser
} from 'react-icons/fi';


function NavItem({ icon: Icon, label, to }) {

  return (
    <NavLink
      to={to}
      // La función className recibe 'isActive' de NavLink
      className={({ isActive }) => 
        // Contenedor principal del ítem
        `flex flex-col items-center justify-center p-0 text-xs font-medium 
         ${isActive ? 'text-black' : 'text-gray-500'}`
      }
    >
      {/* Contenedor del ícono para el estilo de fondo gris */}
      <div className={`flex flex-col items-center justify-center p-2 rounded-lg 
        ${({ isActive }) => isActive ? 'bg-gray-100' : ''}`}
      >
        <Icon className={`w-6 h-6`} /> {/* Ya no necesita color aquí, NavLink lo maneja en el padre */}
      </div>
      
      {/* Texto de la etiqueta */}
      <span className="text-xs mt-1">
        {label}
      </span>
    </NavLink>
  );
}

export default function BottomNav() {
  // Puedes usar useLocation() si necesitas lógica de activación más compleja, 
  // pero NavLink manejará la activación por nosotros.
  
  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white shadow-[0_-2px_5px_rgba(0,0,0,0.05)] h-20 flex justify-around items-center px-4 z-50">
      {/* 2. Modificación: Pasamos la ruta 'to' a cada NavItem */}
      <NavItem icon={FiHome} label="Inicio" to="/" /> 
      <NavItem icon={FiCalendar} label="Horario" to="/horario" /> 
      <NavItem icon={FiClock} label="Asistencia" to="/asistencia" />
      <NavItem icon={FiBell} label="Avisos" to="/avisos" />
      
      {/* EL ENLACE AL PERFIL */}
      <NavItem icon={FiUser} label="Perfil" to="/perfil" /> 
    </nav>
  );
}
