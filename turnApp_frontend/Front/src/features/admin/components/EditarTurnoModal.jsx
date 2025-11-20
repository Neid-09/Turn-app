import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { turnoSchema } from '../../../schemas/turno.schema';
import { turnoService } from '../../../services/turno.service';
import { FiX, FiSave } from 'react-icons/fi';
import FormInput from '../../../shared/components/FormInput';

export default function EditarTurnoModal({ turno, onClose, onSuccess }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(turnoSchema),
    defaultValues: {
      nombre: turno.nombre,
      horaInicio: turno.horaInicio,
      horaFin: turno.horaFin,
      estado: turno.estado,
    },
  });

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      setError(null);
      await turnoService.actualizar(turno.id, data);
      onSuccess();
    } catch (err) {
      console.error('Error al actualizar turno:', err);
      setError(err.response?.data?.message || 'Error al actualizar el turno');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between rounded-t-2xl">
          <h2 className="text-xl font-bold text-gray-900">Editar Turno</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <FiX className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        {/* Formulario */}
        <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">
          {error && (
            <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-sm text-red-600">{error}</p>
            </div>
          )}

          <FormInput
            label="Nombre del turno"
            placeholder="Ej: Turno Mañana"
            {...register('nombre')}
            error={errors.nombre?.message}
            required
          />

          <div className="grid grid-cols-2 gap-4">
            <FormInput
              label="Hora inicio"
              type="time"
              {...register('horaInicio')}
              error={errors.horaInicio?.message}
              required
            />

            <FormInput
              label="Hora fin"
              type="time"
              {...register('horaFin')}
              error={errors.horaFin?.message}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Estado
              <span className="text-red-500 ml-1">*</span>
            </label>
            <select
              {...register('estado')}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
            >
              <option value="ACTIVO">Activo</option>
              <option value="INACTIVO">Inactivo</option>
            </select>
            {errors.estado && (
              <p className="text-sm text-red-600 mt-1">{errors.estado.message}</p>
            )}
          </div>

          {/* Botones */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-purple-300 text-white rounded-lg font-medium transition-colors"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  Guardando...
                </>
              ) : (
                <>
                  <FiSave className="w-4 h-4" />
                  Guardar Cambios
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
