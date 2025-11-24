import { useState } from 'react';
import { usuarioService } from '../../../../services/usuario.service';
import { FiX, FiPlus, FiUser, FiMail, FiLock, FiBriefcase, FiPhone, FiCreditCard, FiShield } from 'react-icons/fi';
import { crearEmpleadoSchema } from '../../../../schemas/empleado.schema';
import FormInput from '../../../../shared/components/FormInput';

export default function CrearEmpleadoModal({ onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    username: '',
    password: '',
    codigoEmpleado: '',
    cargo: '',
    telefono: '',
    numeroIdentificacion: '',
    rol: 'EMPLEADO'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Limpiar error del campo al modificarlo
    if (fieldErrors[name]) {
      setFieldErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validar con Zod
    const validation = crearEmpleadoSchema.safeParse(formData);
    
    if (!validation.success) {
      const errors = {};
      validation.error.issues.forEach(issue => {
        errors[issue.path[0]] = issue.message;
      });
      setFieldErrors(errors);
      setError('Por favor, corrija los errores en el formulario');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setFieldErrors({});
      
      // Mapear los datos al formato que espera el backend
      const requestData = {
        username: formData.username,
        email: formData.email,
        firstName: formData.nombre,
        lastName: formData.apellido,
        password: formData.password,
        codigoEmpleado: formData.codigoEmpleado,
        cargo: formData.cargo || 'Empleado',
        fechaContratacion: new Date().toISOString().split('T')[0], // Fecha actual
        rolApp: formData.rol,
        numeroIdentificacion: formData.numeroIdentificacion || '0000000000',
        telefono: formData.telefono || '+573000000000'
      };
      
      await usuarioService.create(requestData);
      
      onSuccess('Empleado creado exitosamente');
    } catch (err) {
      console.error('Error al crear empleado:', err);
      const errorMessage = err.response?.data?.mensaje || err.response?.data?.message || 'Error al crear el empleado';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-white/30 backdrop-blur-md flex items-center justify-center p-4 z-100 animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-hidden animate-slide-up">
        {/* Header mejorado */}
        <div className="relative bg-linear-to-r from-blue-600 to-blue-700 px-8 py-6">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-2xl font-bold text-white flex items-center gap-3">
                <div className="w-10 h-10 bg-white/20 rounded-lg flex items-center justify-center">
                  <FiPlus className="w-6 h-6 text-white" />
                </div>
                Crear Nuevo Empleado
              </h2>
              <p className="text-blue-100 text-sm mt-1">Complete la información del nuevo empleado</p>
            </div>
            <button
              onClick={onClose}
              className="text-white hover:bg-white/20 p-2 rounded-lg transition-colors"
            >
              <FiX className="w-6 h-6" />
            </button>
          </div>
        </div>

        {/* Form mejorado */}
        <form onSubmit={handleSubmit} className="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
          {/* Error message mejorado */}
          {error && (
            <div className="bg-red-50 border-l-4 border-red-500 text-red-700 px-4 py-3 rounded-lg text-sm mb-6 flex items-start gap-3 animate-shake">
              <div className="w-6 h-6 bg-red-500 rounded-full flex items-center justify-center shrink-0 mt-0.5">
                <span className="text-white text-xs font-bold">!</span>
              </div>
              <p>{error}</p>
            </div>
          )}

          {/* Sección: Información Personal */}
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4 pb-2 border-b-2 border-blue-100">
              <FiUser className="w-5 h-5 text-blue-600" />
              <h3 className="text-base font-bold text-gray-800">Información Personal</h3>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <FormInput
                label="Nombre"
                name="nombre"
                type="text"
                value={formData.nombre}
                onChange={handleChange}
                placeholder="Ingrese el nombre"
                required
                icon={FiUser}
                error={fieldErrors.nombre}
              />

              <FormInput
                label="Apellido"
                name="apellido"
                type="text"
                value={formData.apellido}
                onChange={handleChange}
                placeholder="Ingrese el apellido"
                required
                icon={FiUser}
                error={fieldErrors.apellido}
              />

              <FormInput
                label="Correo Electrónico"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="ejemplo@empresa.com"
                required
                icon={FiMail}
                error={fieldErrors.email}
                className="md:col-span-2"
              />

              <FormInput
                label="Nombre de Usuario"
                name="username"
                type="text"
                value={formData.username}
                onChange={handleChange}
                placeholder="usuario123"
                required
                icon={FiUser}
                error={fieldErrors.username}
                className="md:col-span-2"
              />

              <FormInput
                label="Número de Identificación"
                name="numeroIdentificacion"
                type="text"
                value={formData.numeroIdentificacion}
                onChange={handleChange}
                placeholder="1234567890"
                icon={FiCreditCard}
                error={fieldErrors.numeroIdentificacion}
                inputClassName="font-mono"
              />

              <FormInput
                label="Teléfono"
                name="telefono"
                type="tel"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="+573001234567"
                icon={FiPhone}
                error={fieldErrors.telefono}
              />
            </div>
          </div>

          {/* Sección: Información Laboral */}
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4 pb-2 border-b-2 border-blue-100">
              <FiBriefcase className="w-5 h-5 text-blue-600" />
              <h3 className="text-base font-bold text-gray-800">Información Laboral</h3>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <FormInput
                label="Código de Empleado"
                name="codigoEmpleado"
                type="text"
                value={formData.codigoEmpleado}
                onChange={handleChange}
                placeholder="EMP001"
                required
                icon={FiCreditCard}
                error={fieldErrors.codigoEmpleado}
                inputClassName="font-mono bg-blue-50"
                helpText="Código único e identificador"
              />

              <FormInput
                label="Cargo"
                name="cargo"
                type="text"
                value={formData.cargo}
                onChange={handleChange}
                placeholder="Ej: Desarrollador"
                icon={FiBriefcase}
                error={fieldErrors.cargo}
              />

              <FormInput
                label="Rol en el Sistema"
                name="rol"
                type="select"
                value={formData.rol}
                onChange={handleChange}
                required
                icon={FiShield}
                error={fieldErrors.rol}
                className="md:col-span-2"
              >
                <option value="EMPLEADO">👤 Empleado</option>
                <option value="ADMIN">👑 Administrador</option>
              </FormInput>
            </div>
          </div>

          {/* Sección: Seguridad */}
          <div className="mb-6">
            <div className="flex items-center gap-2 mb-4 pb-2 border-b-2 border-blue-100">
              <FiLock className="w-5 h-5 text-blue-600" />
              <h3 className="text-base font-bold text-gray-800">Seguridad</h3>
            </div>
            
            <FormInput
              label="Contraseña"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Mínimo 8 caracteres"
              required
              icon={FiLock}
              error={fieldErrors.password}
            />

            <div className="flex items-start gap-2 mt-2 p-3 bg-blue-50 rounded-lg border border-blue-100">
              <span className="text-blue-600 text-xs mt-0.5">ℹ️</span>
              <p className="text-xs text-blue-700">
                <strong>Requisitos:</strong> La contraseña debe tener al menos 8 caracteres
              </p>
            </div>
          </div>

          {/* Buttons mejorados */}
          <div className="flex flex-col sm:flex-row gap-3 pt-6 mt-6 border-t-2 border-gray-100">
            <button
              type="button"
              onClick={onClose}
              className="sm:flex-1 px-6 py-3.5 border-2 border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-all font-semibold order-2 sm:order-1"
              disabled={loading}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="sm:flex-1 px-6 py-3.5 bg-linear-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 transition-all font-semibold shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed transform hover:-translate-y-0.5 order-1 sm:order-2 flex items-center justify-center gap-2"
              disabled={loading}
            >
              {loading ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  <span>Creando empleado...</span>
                </>
              ) : (
                <>
                  <FiPlus className="w-5 h-5" />
                  <span>Crear Empleado</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
