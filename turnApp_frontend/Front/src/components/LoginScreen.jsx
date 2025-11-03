import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showCredentials, setShowCredentials] = useState(false);
  
  const navigate = useNavigate();
  const { login, user } = useAuth();

  // Redirigir si el usuario ya está autenticado
  useEffect(() => {
    if (user) {
      const redirectPath = user.role === 'admin' ? '/admin' : '/';
      navigate(redirectPath, { replace: true });
    }
  }, [user, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const result = await login(email, password);
      
      if (result.success) {
        // Redirigir según el rol del usuario
        const redirectPath = result.user.role === 'admin' ? '/admin' : '/';
        navigate(redirectPath, { replace: true });
      } else {
        setError(result.error || 'Error al iniciar sesión');
      }
    } catch (err) {
      setError('Error inesperado. Por favor, intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  const fillDemoCredentials = (role) => {
    if (role === 'admin') {
      setEmail('admin@turnapp.com');
      setPassword('admin123');
    } else {
      setEmail('empleado@turnapp.com');
      setPassword('empleado123');
    }
    setError('');
  };

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

          {/* Error Message */}
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-600 text-sm text-center">{error}</p>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Email Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Correo electrónico
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black"
                placeholder="correo@ejemplo.com"
                required
                disabled={loading}
              />
            </div>

            {/* Password Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Contraseña
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black"
                placeholder="••••••••"
                required
                disabled={loading}
              />
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-black text-white py-3 rounded-lg font-medium hover:bg-gray-800 transition-colors mt-6 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
            </button>
          </form>

          {/* Demo Credentials Section */}
          <div className="mt-6 pt-6 border-t border-gray-200">
            <button
              type="button"
              onClick={() => setShowCredentials(!showCredentials)}
              className="text-sm text-gray-600 hover:text-black transition-colors mb-2 flex items-center gap-2 mx-auto"
            >
              <svg 
                className={`w-4 h-4 transition-transform ${showCredentials ? 'rotate-180' : ''}`} 
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24"
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
              </svg>
              Usuarios de prueba
            </button>
            
            {showCredentials && (
              <div className="space-y-2 mt-3">
                <button
                  type="button"
                  onClick={() => fillDemoCredentials('admin')}
                  className="w-full text-left p-3 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors"
                >
                  <p className="text-sm font-medium text-gray-900">👨‍💼 Administrador</p>
                  <p className="text-xs text-gray-600 mt-1">admin@turnapp.com / admin123</p>
                </button>
                
                <button
                  type="button"
                  onClick={() => fillDemoCredentials('employee')}
                  className="w-full text-left p-3 bg-green-50 hover:bg-green-100 rounded-lg transition-colors"
                >
                  <p className="text-sm font-medium text-gray-900">👤 Empleado</p>
                  <p className="text-xs text-gray-600 mt-1">empleado@turnapp.com / empleado123</p>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
