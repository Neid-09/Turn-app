import { Routes, Route } from 'react-router-dom';
import LoginScreen from './components/LoginScreen';

// Rutas de Empleado
import EmployeeLayout from './components/EmployeeLayout';
import EmployeeDashboard from './components/EmployeeDashboard';
import Horario from './pages/Horario';

// Rutas de Admin
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

      {/* Rutas para el administrador */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<AdminDashboard />} />
      </Route>
    </Routes>
  );
}

export default App;
