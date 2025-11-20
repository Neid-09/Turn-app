import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginScreen() {
  const navigate = useNavigate();
  const { login, user, loading: authLoading, keycloakReady } = useAuth();

  // Redirigir si el usuario ya está autenticado
  useEffect(() => {
    if (user) {
      const redirectPath = user.role === 'admin' ? '/admin' : '/';
      navigate(redirectPath, { replace: true });
    }
  }, [user, navigate]);

  // Manejar inicio de sesión con Keycloak
  const handleLogin = async () => {
    try {
      await login();
    } catch (err) {
      console.error('Error al iniciar sesión:', err);
    }
  };

  // Mostrar cargando mientras Keycloak se inicializa
  if (authLoading || !keycloakReady) {
    return (
      <div className="min-h-screen bg-[#f7f8fa] flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-gray-300 border-t-black rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Iniciando aplicación...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#f7f8fa] flex items-center justify-center font-sans">
      <div className="flex flex-col items-center w-full max-w-md px-4">
        {/* Logo Circle */}
        <div className="w-24 h-24 bg-linear-to-br from-gray-400 to-gray-600 rounded-full mb-6 flex items-center justify-center shadow-lg">
          <span className="text-white text-3xl font-bold">TA</span>
        </div>

        {/* App Title */}
        <h1 className="text-2xl font-bold text-black mb-2">TURN-APP</h1>
        <p className="text-gray-500 mb-8">Gestión de turnos</p>

        {/* Login Card */}
        <div className="bg-white rounded-2xl shadow-sm p-8 w-full">
          <h2 className="text-xl font-semibold text-center mb-6">Iniciar sesión</h2>

          <p className="text-gray-600 text-center mb-6">
            Inicia sesión con tu cuenta de Keycloak
          </p>

          {/* Login Button */}
          <button
            onClick={handleLogin}
            className="w-full bg-black text-white py-3 rounded-lg font-medium hover:bg-gray-800 transition-colors flex items-center justify-center gap-2 cursor-pointer"
          >
            <svg 
              className="w-5 h-5" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" 
              />
            </svg>
            Iniciar sesión con Keycloak
          </button>

          {/* Info Section */}
          <div className="mt-6 pt-6 border-t border-gray-200">
            <p className="text-xs text-gray-500 text-center">
              Serás redirigido a la página de inicio de sesión de Keycloak
            </p>
            <div className="mt-4 space-y-2">
              <div className="text-xs text-gray-600">
                <p className="font-medium mb-1">🔒 Autenticación segura</p>
                <p>OAuth 2.0 / OpenID Connect</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
