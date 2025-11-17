import { createContext, useContext, useState, useEffect } from 'react';
import keycloak, { initOptions } from '../../config/keycloak.config';
import { usuarioService } from '../../services/usuario.service';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [keycloakReady, setKeycloakReady] = useState(false);

  // Inicializar Keycloak al montar el componente
  useEffect(() => {
    const initKeycloak = async () => {
      try {
        const authenticated = await keycloak.init(initOptions);
        setKeycloakReady(true);

        if (authenticated) {
          // Si está autenticado, cargar datos del usuario
          await loadUserProfile();
        }
      } catch (error) {
        console.error('Error al inicializar Keycloak:', error);
      } finally {
        setLoading(false);
      }
    };

    initKeycloak();

    // Actualizar token automáticamente cada 60 segundos
    const tokenRefreshInterval = setInterval(() => {
      keycloak.updateToken(70).catch(() => {
        console.error('Error al refrescar token');
      });
    }, 60000);

    return () => clearInterval(tokenRefreshInterval);
  }, []);

  // Cargar perfil del usuario desde el backend
  const loadUserProfile = async () => {
    try {
      // Obtener información de Keycloak
      const keycloakProfile = keycloak.tokenParsed;
      
      // Extraer roles
      const roles = keycloakProfile?.realm_access?.roles || [];
      const isAdmin = roles.includes('ADMIN');
      const role = isAdmin ? 'admin' : 'employee';

      // Intentar obtener datos adicionales del backend
      let backendProfile = null;
      try {
        backendProfile = await usuarioService.getProfile();
      } catch (error) {
        console.warn('No se pudo obtener perfil del backend:', error);
      }

      // Combinar información de Keycloak y backend
      const userProfile = {
        id: keycloakProfile?.sub,
        email: keycloakProfile?.email,
        name: backendProfile?.nombre || keycloakProfile?.name || keycloakProfile?.preferred_username,
        role: role,
        roles: roles,
        avatar: backendProfile?.avatar || null,
        ...backendProfile
      };

      setUser(userProfile);
    } catch (error) {
      console.error('Error al cargar perfil de usuario:', error);
    }
  };

  // Login con Keycloak
  const login = async () => {
    try {
      await keycloak.login({
        redirectUri: window.location.origin
      });
      return { success: true };
    } catch (error) {
      console.error('Error en login:', error);
      return { success: false, error: error.message };
    }
  };

  // Logout con Keycloak
  const logout = async () => {
    try {
      await keycloak.logout({
        redirectUri: window.location.origin
      });
      setUser(null);
    } catch (error) {
      console.error('Error en logout:', error);
    }
  };

  // Actualizar perfil del usuario
  const updateProfile = async (updates) => {
    try {
      if (!user?.id) return;

      const updatedData = await usuarioService.update(user.id, updates);
      const updatedUser = { ...user, ...updatedData };
      setUser(updatedUser);
      return { success: true, user: updatedUser };
    } catch (error) {
      console.error('Error al actualizar perfil:', error);
      return { success: false, error: error.message };
    }
  };

  // Obtener token de acceso
  const getToken = () => keycloak.token;

  // Verificar si tiene un rol específico
  const hasRole = (role) => {
    return user?.roles?.includes(role) || false;
  };

  const value = {
    user,
    login,
    logout,
    updateProfile,
    loading,
    keycloakReady,
    isAuthenticated: keycloak.authenticated || false,
    isAdmin: user?.role === 'admin',
    isEmployee: user?.role === 'employee',
    getToken,
    hasRole,
    keycloak // Exponer instancia de keycloak por si se necesita
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
