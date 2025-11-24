import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { horarioSchema } from '../../../../schemas/horario.schema';
import { horarioService } from '../../../../services/horario.service';
import { turnoService } from '../../../../services/turno.service';
import { FiX, FiChevronLeft, FiChevronRight, FiCheck } from 'react-icons/fi';
import StepIndicator from './StepIndicator';
import Paso1InfoBasica from './Paso1InfoBasica';
import Paso2AsignarTurnos from './Paso2AsignarTurnos';
import Paso3Revision from './Paso3Revision';
import { useAsignaciones } from '../../hooks/useAsignaciones';

// Componente principal
export default function AsistenteHorarioModal({ onClose, onSuccess }) {
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [loadingUsuarios, setLoadingUsuarios] = useState(false);

  // Estados para los datos
  const [turnos, setTurnos] = useState([]);
  
  // Hook personalizado para manejar asignaciones
  const {
    asignaciones,
    agregarAsignaciones,
    eliminarAsignacion
  } = useAsignaciones();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(horarioSchema),
    defaultValues: {
      nombre: '',
      fechaInicio: '',
      fechaFin: '',
      descripcion: '',
    },
  });

  const watchedData = watch();

  // Cargar turnos al iniciar
  useEffect(() => {
    const cargarTurnos = async () => {
      try {
        const turns = await turnoService.listarActivos();
        setTurnos(turns);
      } catch (err) {
        console.error('Error al cargar turnos:', err);
      }
    };
    cargarTurnos();
  }, []);

  const siguientePaso = () => {
    setError(null);
    setCurrentStep((prev) => Math.min(prev + 1, 3));
  };

  const pasoAnterior = () => {
    setError(null);
    setCurrentStep((prev) => Math.max(prev - 1, 1));
  };

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      setError(null);

      // 1. Crear el horario
      const horarioCreado = await horarioService.crear(data);

      // 2. Agregar detalles en lote si hay asignaciones
      if (asignaciones.length > 0) {
        const detallesParaEnviar = asignaciones.map(a => ({
          usuarioId: a.usuarioId,
          fecha: a.fecha,
          turnoId: a.turnoId,
          observaciones: a.observaciones
        }));

        await horarioService.agregarDetallesLote(horarioCreado.id, detallesParaEnviar);
      }

      onSuccess();
    } catch (err) {
      console.error('Error al crear horario:', err);
      setError(err.response?.data?.message || 'Error al crear el horario');
    } finally {
      setLoading(false);
    }
  };
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between rounded-t-2xl z-10">
          <h2 className="text-xl font-bold text-gray-900">Crear Nuevo Horario</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <FiX className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        {/* Contenido */}
        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          <StepIndicator currentStep={currentStep} totalSteps={3} />

          {error && (
            <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-sm text-red-600">{error}</p>
            </div>
          )}

          {currentStep === 1 && <Paso1InfoBasica register={register} errors={errors} />}
          {currentStep === 2 && (
            <Paso2AsignarTurnos
              fechaInicio={watchedData.fechaInicio}
              fechaFin={watchedData.fechaFin}
              turnos={turnos}
              asignaciones={asignaciones}
              onAgregarAsignaciones={agregarAsignaciones}
              onEliminarAsignacion={eliminarAsignacion}
              loadingUsuarios={loadingUsuarios}
              setLoadingUsuarios={setLoadingUsuarios}
            />
          )}
          {currentStep === 3 && (
            <Paso3Revision
              datosHorario={watchedData}
              asignaciones={asignaciones}
            />
          )}

          {/* Botones de navegación */}
          <div className="flex gap-3 pt-6 mt-6 border-t border-gray-200">
            {currentStep > 1 && (
              <button
                type="button"
                onClick={pasoAnterior}
                className="flex items-center gap-2 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium transition-colors"
              >
                <FiChevronLeft className="w-4 h-4" />
                Anterior
              </button>
            )}

            {currentStep < 3 ? (
              <button
                type="button"
                onClick={siguientePaso}
                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-medium transition-colors"
              >
                Siguiente
                <FiChevronRight className="w-4 h-4" />
              </button>
            ) : (
              <button
                type="submit"
                disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 disabled:bg-green-300 text-white rounded-lg font-medium transition-colors"
              >
                {loading ? (
                  <>
                    <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    Creando...
                  </>
                ) : (
                  <>
                    <FiCheck className="w-5 h-5" />
                    Crear Horario
                  </>
                )}
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
