import { useState, useEffect } from 'react';
import { usuarioService } from '../../../services/usuario.service';
import { FiPlus, FiSearch, FiUsers } from 'react-icons/fi';
import CrearEmpleadoModal from '../components/Empleado/CrearEmpleadoModal';
import EditarEmpleadoModal from '../components/Empleado/EditarEmpleadoModal';
import EmpleadoCard from '../components/Empleado/EmpleadoCard';
import AlertDialog from '../../../shared/components/AlertDialog';
import { useAlert } from '../../../shared/hooks/useAlert';

export default function EmpleadosPage() {
  const [empleados, setEmpleados] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCrearModal, setShowCrearModal] = useState(false);
  const [showEditarModal, setShowEditarModal] = useState(false);
  const [empleadoSeleccionado, setEmpleadoSeleccionado] = useState(null);
  const [error, setError] = useState(null);
  const { alert, confirm, success, error: showError, close } = useAlert();

  // Cargar empleados al montar el componente
  useEffect(() => {
    cargarEmpleados();
  }, []);

  const cargarEmpleados = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await usuarioService.getAll();
      setEmpleados(data);
    } catch (err) {
      console.error('Error al cargar empleados:', err);
      setError('Error al cargar la lista de empleados');
    } finally {
      setLoading(false);
    }
  };

  const handleBuscar = async () => {
    if (!searchTerm.trim()) {
      cargarEmpleados();
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const data = await usuarioService.search(searchTerm);
      setEmpleados(data.content || data);
    } catch (err) {
      console.error('Error en búsqueda:', err);
      setError('Error al buscar empleados');
    } finally {
      setLoading(false);
    }
  };

  const handleEditar = (empleado) => {
    setEmpleadoSeleccionado(empleado);
    setShowEditarModal(true);
  };

  const handleEliminar = (id, nombre) => {
    confirm(
      `¿Estás seguro de eliminar a ${nombre}? Esta acción no se puede deshacer.`,
      async () => {
        try {
          await usuarioService.delete(id);
          success('Empleado eliminado exitosamente');
          cargarEmpleados();
        } catch (err) {
          console.error('Error al eliminar:', err);
          showError('Error al eliminar el empleado');
        }
      },
      'Eliminar Empleado'
    );
  };

  const handleCambiarEstado = (id, estadoActual, nombre) => {
    const nuevoEstado = !estadoActual;
    const accion = nuevoEstado ? 'habilitar' : 'deshabilitar';
    
    confirm(
      `¿Estás seguro de ${accion} a ${nombre}?`,
      async () => {
        try {
          await usuarioService.changeStatus(id, nuevoEstado);
          success(`Empleado ${accion === 'habilitar' ? 'habilitado' : 'deshabilitado'} exitosamente`);
          cargarEmpleados();
        } catch (err) {
          console.error('Error al cambiar estado:', err);
          showError('Error al cambiar el estado del empleado');
        }
      },
      `${nuevoEstado ? 'Habilitar' : 'Deshabilitar'} Empleado`
    );
  };

  const handleCrearExitoso = (mensaje) => {
    setShowCrearModal(false);
    // Esperar a que el modal se cierre antes de mostrar la alerta
    setTimeout(() => {
      success(mensaje);
      cargarEmpleados();
    }, 100);
  };

  const handleEditarExitoso = (mensaje) => {
    setShowEditarModal(false);
    setEmpleadoSeleccionado(null);
    // Esperar a que el modal se cierre antes de mostrar la alerta
    setTimeout(() => {
      success(mensaje);
      cargarEmpleados();
    }, 100);
  };

  return (
    <div className="min-h-screen bg-linear-to-br from-gray-50 to-gray-100">
      {/* Header mejorado */}
      <div className="bg-white shadow-md border-b border-gray-200">
        <div className="px-6 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
                <div className="w-10 h-10 bg-linear-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center">
                  <FiUsers className="w-6 h-6 text-white" />
                </div>
                Gestión de Empleados
              </h1>
              <p className="text-sm text-gray-500 mt-2 ml-13">
                Administra y controla el personal del sistema
              </p>
            </div>
            <div className="hidden md:flex items-center gap-2 px-4 py-2 bg-blue-50 rounded-lg border border-blue-100">
              <div className="text-right">
                <p className="text-xs text-gray-500">Total empleados</p>
                <p className="text-xl font-bold text-blue-600">{empleados.length}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="px-6 py-8 max-w-7xl mx-auto">
        {/* Barra de búsqueda y botón crear mejorada */}
        <div className="mb-8">
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="flex-1 relative group">
              <FiSearch className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5 group-focus-within:text-blue-500 transition-colors" />
              <input
                type="text"
                placeholder="Buscar por nombre, email o código de empleado..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleBuscar()}
                className="w-full pl-12 pr-4 py-3.5 bg-white border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent shadow-sm hover:shadow-md transition-all"
              />
            </div>
            <button
              onClick={() => setShowCrearModal(true)}
              className="bg-linear-to-r from-blue-600 to-blue-700 text-white px-6 py-3.5 rounded-xl flex items-center justify-center gap-2 hover:from-blue-700 hover:to-blue-800 transition-all shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 font-medium"
            >
              <FiPlus className="w-5 h-5" />
              <span>Nuevo Empleado</span>
            </button>
          </div>
        </div>

        {/* Mensaje de error mejorado */}
        {error && (
          <div className="bg-linear-to-r from-red-50 to-red-100 border-l-4 border-red-500 text-red-700 px-6 py-4 rounded-lg mb-6 shadow-md">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center shrink-0">
                <span className="text-white font-bold">!</span>
              </div>
              <p className="font-medium">{error}</p>
            </div>
          </div>
        )}

        {/* Loading mejorado */}
        {loading ? (
          <div className="text-center py-20">
            <div className="inline-block relative">
              <div className="animate-spin rounded-full h-16 w-16 border-4 border-gray-200 border-t-blue-600"></div>
              <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
                <FiUsers className="w-6 h-6 text-blue-600" />
              </div>
            </div>
            <p className="text-gray-600 mt-6 font-medium">Cargando empleados...</p>
          </div>
        ) : empleados.length === 0 ? (
          <div className="text-center py-20 bg-white rounded-2xl shadow-lg">
            <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <FiUsers className="w-12 h-12 text-gray-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-700 mb-2">No se encontraron empleados</h3>
            <p className="text-gray-500 mb-6">
              {searchTerm ? 'Intenta con otros términos de búsqueda' : 'Comienza agregando tu primer empleado'}
            </p>
            {searchTerm ? (
              <button
                onClick={() => {
                  setSearchTerm('');
                  cargarEmpleados();
                }}
                className="px-6 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
              >
                Limpiar búsqueda
              </button>
            ) : (
              <button
                onClick={() => setShowCrearModal(true)}
                className="px-6 py-2.5 bg-linear-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 transition-all font-medium shadow-lg"
              >
                Agregar Empleado
              </button>
            )}
          </div>
        ) : (
          /* Lista de empleados mejorada */
          <div className="grid gap-4">
            {empleados.map((empleado) => (
              <EmpleadoCard
                key={empleado.id}
                empleado={empleado}
                onEditar={handleEditar}
                onEliminar={handleEliminar}
                onCambiarEstado={handleCambiarEstado}
              />
            ))}
          </div>
        )}
      </div>

      {/* Modales */}
      {showCrearModal && (
        <CrearEmpleadoModal
          onClose={() => setShowCrearModal(false)}
          onSuccess={handleCrearExitoso}
        />
      )}

      {showEditarModal && empleadoSeleccionado && (
        <EditarEmpleadoModal
          empleado={empleadoSeleccionado}
          onClose={() => {
            setShowEditarModal(false);
            setEmpleadoSeleccionado(null);
          }}
          onSuccess={handleEditarExitoso}
        />
      )}

      {/* Componente de alertas */}
      <AlertDialog
        isOpen={alert.isOpen}
        onClose={close}
        onConfirm={alert.onConfirm}
        title={alert.title}
        message={alert.message}
        type={alert.type}
        confirmText={alert.confirmText}
        cancelText={alert.cancelText}
      />
    </div>
  );
}
