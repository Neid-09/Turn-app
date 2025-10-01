import { createContext, useContext, useState, useEffect } from 'react';

// Usuarios de prueba con contraseñas iguales al username
const USERS = {
  admin: {
    username: 'admin',
    password: 'admin',
    role: 'admin',
    name: 'Administrador',
    fullName: 'Administrador del Sistema',
  },
  empleado: {
    username: 'empleado',
    password: 'empleado',
    role: 'empleado',
    name: 'Juan Pérez',
    fullName: 'Juan Pérez',
  }
};

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser usado dentro de un AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Verificar si hay un usuario guardado en localStorage
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setIsLoading(false);
  }, []);

  const login = async (username, password) => {
    // Simular delay de autenticación
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const foundUser = Object.values(USERS).find(
      u => u.username === username && u.password === password
    );

    if (foundUser) {
      const { password: _, ...userWithoutPassword } = foundUser;
      setUser(userWithoutPassword);
      localStorage.setItem('user', JSON.stringify(userWithoutPassword));
      return { success: true };
    } else {
      return { success: false, error: 'Credenciales inválidas' };
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
  };

  const value = {
    user,
    login,
    logout,
    isLoading,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'admin',
    isEmpleado: user?.role === 'empleado',
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};