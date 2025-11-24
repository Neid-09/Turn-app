import { FiCalendar } from 'react-icons/fi';
import FormInput from '../../../../shared/components/FormInput';

export default function Paso1InfoBasica({ register, errors }) {
  return (
    <div className="space-y-4">
      <div className="text-center mb-6">
        <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
          <FiCalendar className="w-8 h-8 text-purple-600" />
        </div>
        <h3 className="text-xl font-bold text-gray-900">Información del Horario</h3>
        <p className="text-sm text-gray-500 mt-1">Define el período y nombre del horario</p>
      </div>

      <FormInput
        label="Nombre del horario"
        placeholder="Ej: Horario Diciembre 2025"
        {...register('nombre')}
        error={errors.nombre?.message}
        required
      />

      <div className="grid grid-cols-2 gap-4">
        <FormInput
          label="Fecha inicio"
          type="date"
          {...register('fechaInicio')}
          error={errors.fechaInicio?.message}
          required
        />
        <FormInput
          label="Fecha fin"
          type="date"
          {...register('fechaFin')}
          error={errors.fechaFin?.message}
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Descripción
          <span className="text-gray-400 ml-1">(opcional)</span>
        </label>
        <textarea
          {...register('descripcion')}
          rows={3}
          placeholder="Descripción del horario..."
          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
        />
        {errors.descripcion && (
          <p className="text-sm text-red-600 mt-1">{errors.descripcion.message}</p>
        )}
      </div>
    </div>
  );
}
