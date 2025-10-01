import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { AdminDashboard } from './pages/AdminDashboard';
import { EmployeeDashboard } from './pages/EmployeeDashboard';
import './App.css'

const AppRoutes = () => {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Cargando...</p>
        </div>
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/login" element={
        user ? (
          <Navigate to={user.role === 'admin' ? '/admin' : '/empleado'} replace />
        ) : (
          <LoginPage />
        )
      } />
      
      <Route path="/admin" element={
        <ProtectedRoute requiredRole="admin">
          <AdminDashboard />
        </ProtectedRoute>
      } />
      
      <Route path="/empleado" element={
        <ProtectedRoute requiredRole="empleado">
          <EmployeeDashboard />
        </ProtectedRoute>
      } />
      
      <Route path="/" element={
        user ? (
          <Navigate to={user.role === 'admin' ? '/admin' : '/empleado'} replace />
        ) : (
          <Navigate to="/login" replace />
        )
      } />
      
      {/* Ruta 404 */}
      <Route path="*" element={
        <Navigate to={user ? (user.role === 'admin' ? '/admin' : '/empleado') : '/login'} replace />
      } />
    </Routes>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App
