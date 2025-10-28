// src/components/ProfileScreen.jsx

import React from 'react';
import { FiEdit } from 'react-icons/fi'; 

const ProfileScreen = () => {

  const user = {
    nombreUsuario: 'Nombre usuario',
    cargoRol: 'CARGO/ROL',
    estado: 'ACTIVO',
    nombreCompleto: 'Juan Pérez García',
    correoElectronico: 'juan.perez@empresa.com',
    telefono: '555-123-4567',
    direccion: 'Calle Falsa 123, Ciudad',
  };

  const handleEdit = () => {
    alert('Funcionalidad de edición pendiente.');
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      
      {/* Encabezado Principal */}
      <h1 className="text-xl font-semibold mb-4 text-gray-800">NAV - Perfil</h1> 
      <h2 className="text-lg font-medium mb-4 text-gray-900">Perfil de usuario</h2>

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
        <div className="w-20 h-20 bg-gray-300 rounded-full mb-3"></div>
        
        <p className="text-lg font-semibold text-gray-900">{user.nombreUsuario}</p>
        <p className="text-sm text-gray-600 mb-2">{user.cargoRol}</p>
        
        {/* Estado ACTIVO */}
        <span className="px-3 py-1 text-xs font-semibold text-green-700 bg-green-100 rounded-full">
          {user.estado}
        </span>
      </div>

      {/* Sección de Datos Personales */}
      <div className="bg-white p-6 rounded-xl shadow-md">
        
        {/* Encabezado de la Sección con Icono de Edición */}
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-base font-semibold text-gray-900">Datos personales</h3>
          <button 
            onClick={handleEdit} 
            className="text-gray-500 hover:text-black transition-colors"
            title="Editar"
          >
            <FiEdit className="w-5 h-5" /> {/* Icono de lápiz */}
          </button>
        </div>
        
        {/* Campos de Datos */}
        {[
          { label: 'Nombre completo', value: user.nombreCompleto, type: 'text' },
          { label: 'Correo electrónico', value: user.correoElectronico, type: 'email' },
          { label: 'Teléfono', value: user.telefono, type: 'tel' },
          { label: 'Dirección', value: user.direccion, type: 'text' },
        ].map((field, index) => (
          <div key={index} className="mb-4">
            <label className="block text-sm font-medium text-gray-500 mb-1">
              {field.label}
            </label>
            <input 
              type={field.type} 
              value={field.value} 
              readOnly 
              className="w-full border-b border-gray-300 pb-1 text-gray-800 focus:outline-none bg-white text-base"
            />
          </div>
        ))}

      </div>
      
      {/* Aquí no se incluye el BottomNav, ya que lo maneja EmployeeLayout */}
      {/* Añadimos un espacio final para que el contenido no quede bajo la barra de navegación fija */}
      <div className="h-20"></div> 
    </div>
  );
};

export default ProfileScreen;