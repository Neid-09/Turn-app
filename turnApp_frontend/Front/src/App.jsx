import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import LoginScreen from './components/LoginScreen';
import ProtectedRoute from './components/ProtectedRoute';

import Horario from './pages/Horario';

// Rutas de Empleado
import EmployeeLayout from './components/EmployeeLayout';
import EmployeeDashboard from './components/EmployeeDashboard';
import ProfileScreen from './components/ProfileScreen'; 

// Rutas de Admin
import AdminLayout from './components/AdminLayout';
import AdminDashboard from './components/AdminDashboard'; 

function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* Ruta pública: Login */}
        <Route path="/login" element={<LoginScreen />} />

        {/* Rutas protegidas para empleados */}
        <Route
          path="/"
          element={
            <ProtectedRoute requiredRole="employee">
              <EmployeeLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<EmployeeDashboard />} />
          <Route path="horario" element={<Horario />} />
          <Route path="perfil" element={<ProfileScreen />} />
        </Route>

        {/* Rutas protegidas para administradores */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute requiredRole="admin">
              <AdminLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<AdminDashboard />} />
          <Route path="perfil" element={<ProfileScreen />} />
        </Route>

        {/* Ruta por defecto: redirigir al login */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
