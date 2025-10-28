// src/App.jsx
import { Routes, Route } from 'react-router-dom';
import LoginScreen from './components/LoginScreen';

// Rutas de Empleado
import EmployeeLayout from './components/EmployeeLayout';
import EmployeeDashboard from './components/EmployeeDashboard';

// Rutas de Admin (NUEVAS)
import AdminLayout from './components/AdminLayout';       // <--- Importa esto
import AdminDashboard from './components/AdminDashboard'; // <--- Importa esto

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

      {/* Rutas para el administrador (NUEVAS) */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<AdminDashboard />} />
        {/* 'index' hace que /admin sea la ruta por defecto */}
        {/* Futuras rutas de admin como /admin/horarios, /admin/empleados irían aquí */}




        
      </Route>
    </Routes>
  );
}

export default App;