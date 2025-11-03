// src/components/ProfileScreen.jsx

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit, FiLogOut } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

const ProfileScreen = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isEditing, setIsEditing] = useState(false);

  const handleEdit = () => {
    setIsEditing(!isEditing);
    if (isEditing) {
      alert('Funcionalidad de guardar cambios pendiente.');
    }
  };

  const handleLogout = () => {
    if (window.confirm('¿Estás seguro de que deseas cerrar sesión?')) {
      logout();
      navigate('/login', { replace: true });
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      
      {/* Encabezado Principal */}
      <div className="flex justify-between items-center mb-4">
        <div>
          <h1 className="text-xl font-semibold text-gray-800">Perfil</h1>
          <h2 className="text-lg font-medium text-gray-900">Perfil de usuario</h2>
        </div>
        <button
          onClick={handleLogout}
          className="flex items-center gap-2 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
          title="Cerrar sesión"
        >
          <FiLogOut className="w-5 h-5" />
          <span className="hidden sm:inline">Cerrar sesión</span>
        </button>
      </div>

      {/* Pestañas de Navegación */}
      <div className="flex bg-white rounded-lg shadow-sm p-1 mb-6">
        <button className="flex-1 py-2 text-sm font-semibold text-white bg-black rounded-lg transition-colors">
          Perfil
        </button>
        <button className="flex-1 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
          Solicitudes
        </button>
        <button className="flex-1 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
          Configuración
        </button>
      </div>

      {/* Tarjeta de Información Superior */}
      <div className="bg-white p-6 rounded-xl shadow-md flex flex-col items-center mb-6">
        {/* Círculo de la foto de perfil (simulado) */}
        <div className="w-20 h-20 bg-linear-to-br from-gray-400 to-gray-600 rounded-full mb-3 flex items-center justify-center">
          <span className="text-white text-2xl font-bold">
            {user?.name?.charAt(0).toUpperCase()}
          </span>
        </div>
        
        <p className="text-lg font-semibold text-gray-900">{user?.name || 'Usuario'}</p>
        <p className="text-sm text-gray-600 mb-2">
          {user?.role === 'admin' ? 'Administrador' : 'Empleado'}
        </p>
        
        {/* Estado ACTIVO */}
        <span className="px-3 py-1 text-xs font-semibold text-green-700 bg-green-100 rounded-full">
          ACTIVO
        </span>
      </div>

      {/* Sección de Datos Personales */}
      <div className="bg-white p-6 rounded-xl shadow-md">
        
        {/* Encabezado de la Sección con Icono de Edición */}
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-base font-semibold text-gray-900">Datos personales</h3>
          <button 
            onClick={handleEdit} 
            className={`text-gray-500 hover:text-black transition-colors ${isEditing ? 'text-blue-500' : ''}`}
            title={isEditing ? 'Guardar' : 'Editar'}
          >
            <FiEdit className="w-5 h-5" />
          </button>
        </div>
        
        {/* Campos de Datos */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-500 mb-1">
            Nombre completo
          </label>
          <input 
            type="text" 
            value={user?.name || ''} 
            readOnly={!isEditing}
            className={`w-full border-b border-gray-300 pb-1 text-gray-800 focus:outline-none bg-white text-base ${isEditing ? 'focus:border-blue-500' : ''}`}
          />
        </div>

        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-500 mb-1">
            Correo electrónico
          </label>
          <input 
            type="email" 
            value={user?.email || ''} 
            readOnly
            className="w-full border-b border-gray-300 pb-1 text-gray-800 focus:outline-none bg-white text-base"
          />
        </div>

        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-500 mb-1">
            Rol
          </label>
          <input 
            type="text" 
            value={user?.role === 'admin' ? 'Administrador' : 'Empleado'} 
            readOnly
            className="w-full border-b border-gray-300 pb-1 text-gray-800 focus:outline-none bg-white text-base"
          />
        </div>

        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-500 mb-1">
            ID de usuario
          </label>
          <input 
            type="text" 
            value={user?.id || ''} 
            readOnly
            className="w-full border-b border-gray-300 pb-1 text-gray-800 focus:outline-none bg-white text-base"
          />
        </div>

      </div>
      
      {/* Espacio final para que el contenido no quede bajo la barra de navegación fija */}
      <div className="h-20"></div> 
    </div>
  );
};

export default ProfileScreen;