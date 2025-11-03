import { Routes, Route } from 'react-router-dom';
import LoginScreen from './components/LoginScreen';

import Horario from './pages/Horario';

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

      {/* Rutas para el empleado */}
      <Route element={<EmployeeLayout />}>
        <Route path="/" element={<EmployeeDashboard />} />
        <Route path="/horario" element={<Horario />} /> {/* 👈 NUEVA RUTA */}
      </Route>
      
      {/* Nueva ruta para el perfil */}
        <Route path="/perfil" element={<ProfileScreen />} /> {/* <--- RUTA DEL PERFIL */}

      {/* Rutas para el administrador */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<AdminDashboard />} />
      </Route>
    </Routes>
  );
}

export default App;
