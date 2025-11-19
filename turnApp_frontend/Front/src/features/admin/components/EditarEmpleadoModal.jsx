import { useState, useEffect } from 'react';
import { usuarioService } from '../../../services/usuario.service';
import { FiX, FiEdit, FiUser, FiMail, FiLock, FiBriefcase, FiPhone, FiCreditCard, FiShield, FiSave } from 'react-icons/fi';
import { editarEmpleadoSchema, cambiarPasswordSchema } from '../../../schemas/empleado.schema';
import FormInput from '../../../shared/components/FormInput';

export default function EditarEmpleadoModal({ empleado, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    codigoEmpleado: '',
    cargo: '',
    telefono: '',
    rol: 'EMPLEADO'
  });
  const [cambiarPassword, setCambiarPassword] = useState(false);
  const [nuevaPassword, setNuevaPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  useEffect(() => {
    if (empleado) {
      setFormData({
        nombre: empleado.nombre || empleado.firstName || '',
        apellido: empleado.apellido || empleado.lastName || '',
        email: empleado.email || '',
        codigoEmpleado: empleado.codigoEmpleado || '',
        cargo: empleado.cargo || '',
        telefono: empleado.telefono || '',
        rol: empleado.rol || empleado.rolApp || 'EMPLEADO'
      });
    }
  }, [empleado]);

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
    
    // Validar datos del formulario con Zod
    const validation = editarEmpleadoSchema.safeParse(formData);
    
    if (!validation.success) {
      const errors = {};
      validation.error.issues.forEach(issue => {
        errors[issue.path[0]] = issue.message;
      });
      setFieldErrors(errors);
      setError('Por favor, corrija los errores en el formulario');
      return;
    }

    // Validar password si se quiere cambiar
    if (cambiarPassword) {
      const passwordValidation = cambiarPasswordSchema.safeParse({ nuevaPassword });
      
      if (!passwordValidation.success) {
        setError(passwordValidation.error.issues[0].message);
        return;
      }
    }

    try {
      setLoading(true);
      setError(null);
      setFieldErrors({});
      
      // Mapear los datos al formato que espera el backend
      const requestData = {
        email: formData.email,
        firstName: formData.nombre,
        lastName: formData.apellido,
        cargo: formData.cargo,
        telefono: formData.telefono,
        rolApp: formData.rol
      };
      
      // Actualizar datos del usuario
      await usuarioService.update(empleado.id, requestData);
      
      // Si se quiere cambiar la contraseña, hacerlo en una petición separada
      if (cambiarPassword && nuevaPassword) {
        await usuarioService.changePassword(empleado.id, nuevaPassword);
      }
      
      alert('Empleado actualizado exitosamente');
      onSuccess();
    } catch (err) {
      console.error('Error al actualizar empleado:', err);
      const errorMessage = err.response?.data?.mensaje || err.response?.data?.message || 'Error al actualizar el empleado';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-white/30 backdrop-blur-md flex items-center justify-center p-4 z-100 animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-hidden animate-slide-up">
        {/* Header mejorado */}
        <div className="relative bg-linear-to-r from-green-600 to-green-700 px-8 py-6">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-2xl font-bold text-white flex items-center gap-3">
                <div className="w-10 h-10 bg-white/20 rounded-lg flex items-center justify-center">
                  <FiEdit className="w-6 h-6 text-white" />
                </div>
                Editar Empleado
              </h2>
              <p className="text-green-100 text-sm mt-1">Actualice la información del empleado</p>
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
            <div className="flex items-center gap-2 mb-4 pb-2 border-b-2 border-green-100">
              <FiUser className="w-5 h-5 text-green-600" />
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
                label="Teléfono"
                name="telefono"
                type="tel"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="+573001234567"
                icon={FiPhone}
                error={fieldErrors.telefono}
                className="md:col-span-2"
              />
            </div>
          </div>

          {/* Sección: Información Laboral */}
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4 pb-2 border-b-2 border-green-100">
              <FiBriefcase className="w-5 h-5 text-green-600" />
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
                disabled
                icon={FiCreditCard}
                inputClassName="font-mono"
                helpText="⚠️ El código de empleado no se puede modificar"
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

          {/* Cambiar contraseña - Sección mejorada */}
          <div className="mb-6">
            <div className="bg-linear-to-br from-blue-50 to-indigo-50 rounded-xl p-5 border border-blue-200">
              <div className="flex items-center mb-3">
                <input
                  type="checkbox"
                  id="cambiarPassword"
                  checked={cambiarPassword}
                  onChange={(e) => setCambiarPassword(e.target.checked)}
                  className="w-5 h-5 text-blue-600 rounded focus:ring-2 focus:ring-blue-500 cursor-pointer"
                />
                <label htmlFor="cambiarPassword" className="ml-3 cursor-pointer flex-1">
                  <div className="flex items-center gap-2">
                    <FiLock className="w-4 h-4 text-blue-600" />
                    <span className="text-sm font-semibold text-gray-800">
                      Cambiar contraseña
                    </span>
                  </div>
                  <p className="text-xs text-gray-600 mt-1">
                    Marque esta opción si desea actualizar la contraseña del empleado
                  </p>
                </label>
              </div>

              {cambiarPassword && (
                <div className="mt-4 space-y-2 animate-slide-down">
                  <label className="block text-sm font-semibold text-gray-700">
                    Nueva Contraseña <span className="text-red-500">*</span>
                  </label>
                  <div className="relative">
                    <FiLock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <input
                      type="password"
                      value={nuevaPassword}
                      onChange={(e) => setNuevaPassword(e.target.value)}
                      className="w-full pl-11 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all bg-white"
                      placeholder="Mínimo 8 caracteres"
                    />
                  </div>
                  <div className="flex items-start gap-2 mt-2 p-3 bg-white rounded-lg border border-blue-100">
                    <span className="text-blue-600 text-xs mt-0.5">ℹ️</span>
                    <p className="text-xs text-blue-700">
                      <strong>Requisitos:</strong> La contraseña debe tener al menos 8 caracteres
                    </p>
                  </div>
                </div>
              )}
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
              className="sm:flex-1 px-6 py-3.5 bg-linear-to-r from-green-600 to-green-700 text-white rounded-lg hover:from-green-700 hover:to-green-800 transition-all font-semibold shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed transform hover:-translate-y-0.5 order-1 sm:order-2 flex items-center justify-center gap-2"
              disabled={loading}
            >
              {loading ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  <span>Guardando cambios...</span>
                </>
              ) : (
                <>
                  <FiSave className="w-5 h-5" />
                  <span>Guardar Cambios</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
