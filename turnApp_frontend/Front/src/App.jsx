import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './shared/context/AuthContext';
import LoginScreen from './shared/components/LoginScreen';
import ProtectedRoute from './shared/components/ProtectedRoute';
import ProfileScreen from './shared/components/ProfileScreen';

// Rutas de Empleado
import EmployeeLayout from './features/employee/components/EmployeeLayout';
import EmployeeDashboard from './features/employee/components/EmployeeDashboard';
import Horario from './features/employee/pages/Horario';

// Rutas de Admin
import AdminLayout from './features/admin/components/AdminLayout';
import AdminDashboard from './features/admin/components/AdminDashboard'; 

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
