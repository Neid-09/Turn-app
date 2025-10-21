export default function LoginScreen() {
  return (
    <div className="min-h-screen bg-[#f7f8fa] flex items-center justify-center font-sans">
      <div className="flex flex-col items-center w-full max-w-md px-4">
        {/* Logo Circle */}
        <div className="w-24 h-24 bg-gray-300 rounded-full mb-6"></div>
        
        {/* App Title */}
        <h1 className="text-2xl font-bold text-black mb-2">TURN-APP</h1>
        <p className="text-gray-500 mb-8">Gestión de turnos</p>
        
        {/* Login Card */}
        <div className="bg-white rounded-2xl shadow-sm p-8 w-full">
          <h2 className="text-xl font-semibold text-center mb-6">Iniciar sesión</h2>
          
          <form className="space-y-4">
            {/* Email Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Correo electrónico
              </label>
              <input
                type="email"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black"
                placeholder=""
              />
            </div>
            
            {/* Password Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Contraseña
              </label>
              <input
                type="password"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black"
                placeholder=""
              />
            </div>
            
            {/* Submit Button */}
            <button
              type="submit"
              className="w-full bg-black text-white py-3 rounded-lg font-medium hover:bg-gray-800 transition-colors mt-6"
            >
              Iniciar sesión
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
