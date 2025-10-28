// src/App.jsx
import { Routes, Route } from 'react-router-dom';
import LoginScreen from './components/LoginScreen';

// Rutas de Empleado
import EmployeeLayout from './components/EmployeeLayout';
import EmployeeDashboard from './components/EmployeeDashboard';
import ProfileScreen from './components/ProfileScreen'; 

// Rutas de Admin (NUEVAS)
import AdminLayout from './components/AdminLayout';
import AdminDashboard from './components/AdminDashboard'; 

function App() {
  return (
    <Routes>
      {/* Ruta para el Login */}
      <Route path="/login" element={<LoginScreen />} />

      {/* Rutas para el empleado (con barra de navegación inferior) */}
      <Route element={<EmployeeLayout />}>
        <Route path="/" element={<EmployeeDashboard />} />
        {/* Futuras rutas de empleado */}
      </Route>
      
      {/* Nueva ruta para el perfil */}
        <Route path="/perfil" element={<ProfileScreen />} /> {/* <--- RUTA DEL PERFIL */}

      {/* Rutas para el administrador (NUEVAS) */}
      <Route path="/admin" element={<AdminLayout />}>
       
        {/* 'index' hace que /admin sea la ruta por defecto */}
        {/* Futuras rutas de admin como /admin/horarios, /admin/empleados irían aquí */}
      </Route>
    </Routes>
  );
}

export default App;