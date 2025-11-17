import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

// Usuarios base del sistema
const BASE_USERS = [
  {
    id: 1,
    email: 'admin@turnapp.com',
    password: 'admin123',
    role: 'admin',
    name: 'Administrador',
    avatar: null
  },
  {
    id: 2,
    email: 'empleado@turnapp.com',
    password: 'empleado123',
    role: 'employee',
    name: 'Empleado Demo',
    avatar: null
  }
];

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Verificar si hay una sesión activa al cargar la app
  useEffect(() => {
    const storedUser = localStorage.getItem('turnapp_user');
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (error) {
        console.error('Error al cargar usuario:', error);
        localStorage.removeItem('turnapp_user');
      }
    }
    setLoading(false);
  }, []);

  // Login
  const login = async (email, password) => {
    try {
      // Buscar usuario en la base de usuarios
      const foundUser = BASE_USERS.find(
        u => u.email === email && u.password === password
      );

      if (!foundUser) {
        throw new Error('Credenciales incorrectas');
      }

      // Crear objeto de usuario sin la contraseña
      const { password: _, ...userWithoutPassword } = foundUser;
      
      // Guardar en localStorage y estado
      localStorage.setItem('turnapp_user', JSON.stringify(userWithoutPassword));
      setUser(userWithoutPassword);

      return { success: true, user: userWithoutPassword };
    } catch (error) {
      return { success: false, error: error.message };
    }
  };

  // Logout
  const logout = () => {
    localStorage.removeItem('turnapp_user');
    setUser(null);
  };

  // Actualizar perfil
  const updateProfile = (updates) => {
    const updatedUser = { ...user, ...updates };
    localStorage.setItem('turnapp_user', JSON.stringify(updatedUser));
    setUser(updatedUser);
  };

  const value = {
    user,
    login,
    logout,
    updateProfile,
    loading,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'admin',
    isEmployee: user?.role === 'employee'
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook personalizado para usar el contexto
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser usado dentro de un AuthProvider');
  }
  return context;
};

export default AuthContext;
