// src/App.jsx
import { Routes, Route } from 'react-router-dom';
import LoginScreen from './components/LoginScreen';
import EmployeeLayout from './components/EmployeeLayout'; // <--- Nuevo Layout
import EmployeeDashboard from './components/EmployeeDashboard'; // <--- Nuevo Panel

function App() {
  return (
    <Routes>
      {/* Ruta para el Login */}
      <Route path="/login" element={<LoginScreen />} />

      {/* Rutas para el empleado (con barra de navegación inferior) */}
      <Route element={<EmployeeLayout />}>
        <Route path="/" element={<EmployeeDashboard />} />
        {/* Futuras rutas como /horario, /perfil irían aquí */}
      </Route>
    </Routes>
  );
}

export default App;