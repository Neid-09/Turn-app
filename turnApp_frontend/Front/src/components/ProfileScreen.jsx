// src/components/ProfileScreen.jsx

import React from 'react';

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
    <div className="profile-screen-container">
      {/* Encabezado y Pestañas */}
      <h2 className="nav-profile-title">Perfil de usuario</h2>
      <div className="nav-tabs">
        {/* Usamos enlaces o botones reales que cambian la ruta o el estado */}
        <button className="tab active">Perfil</button>
        <button className="tab">Solicitudes</button>
        <button className="tab">Configuración</button>
      </div>

      {/* Tarjeta de Información Superior */}
      <div className="user-info-card">
        {/* Círculo de la foto de perfil (simulado) */}
        <div className="profile-picture-placeholder"></div>
        <p className="user-name">{user.nombreUsuario}</p>
        <p className="user-role">{user.cargoRol}</p>
        <span className={`status-badge ${user.estado.toLowerCase()}`}>{user.estado}</span>
      </div>

      {/* Sección de Datos Personales */}
      <div className="personal-data-section">
        <div className="section-header">
          <h3>Datos personales</h3>
          {/* Icono de edición/guardar (simulado con emoji) */}
          <button onClick={handleEdit} className="edit-button">
            ✏️ {/* O un icono de componente como <EditIcon /> */}
          </button>
        </div>
        
        <div className="data-field">
          <label>Nombre completo</label>
          <input type="text" value={user.nombreCompleto} readOnly />
        </div>
        
        <div className="data-field">
          <label>Correo electrónico</label>
          <input type="email" value={user.correoElectronico} readOnly />
        </div>
        
        <div className="data-field">
          <label>Teléfono</label>
          <input type="tel" value={user.telefono} readOnly />
        </div>
        
        <div className="data-field">
          <label>Dirección</label>
          <input type="text" value={user.direccion} readOnly />
        </div>
      </div>
      
      {/* Nota: La barra de navegación inferior (Home, Horario, etc.)
          sería parte de EmployeeLayout, no de este componente. */}
    </div>
  );
};

export default ProfileScreen;