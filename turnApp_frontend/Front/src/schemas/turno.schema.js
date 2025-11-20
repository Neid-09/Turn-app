import { z } from 'zod';

// Helper para calcular duración en minutos
const calcularDuracionMinutos = (horaInicio, horaFin) => {
  const [hI, mI] = horaInicio.split(':').map(Number);
  const [hF, mF] = horaFin.split(':').map(Number);
  const minutosInicio = hI * 60 + mI;
  const minutosFin = hF * 60 + mF;
  return minutosFin - minutosInicio;
};

// Schema para crear/actualizar turno
export const turnoSchema = z.object({
  nombre: z.string()
    .min(1, 'El nombre es obligatorio')
    .min(3, 'El nombre debe tener al menos 3 caracteres')
    .max(100, 'El nombre no puede exceder 100 caracteres'),
  
  horaInicio: z.string()
    .min(1, 'La hora de inicio es obligatoria')
    .regex(/^([01]\d|2[0-3]):([0-5]\d)$/, 'Debe ser una hora válida (HH:MM)'),
  
  horaFin: z.string()
    .min(1, 'La hora de fin es obligatoria')
    .regex(/^([01]\d|2[0-3]):([0-5]\d)$/, 'Debe ser una hora válida (HH:MM)'),
  
  estado: z.enum(['ACTIVO', 'INACTIVO'], {
    errorMap: () => ({ message: 'El estado debe ser ACTIVO o INACTIVO' })
  }),
}).refine((data) => {
  const duracion = calcularDuracionMinutos(data.horaInicio, data.horaFin);
  return duracion > 0;
}, {
  message: 'La hora de fin debe ser posterior a la hora de inicio',
  path: ['horaFin'],
}).transform((data) => {
  // Calcular duracionTotal automáticamente
  const duracionTotal = calcularDuracionMinutos(data.horaInicio, data.horaFin);
  return {
    ...data,
    duracionTotal
  };
});

// Schema para crear asignación
export const asignacionSchema = z.object({
  usuarioId: z.string()
    .min(1, 'El empleado es obligatorio'),
  
  turnoId: z.number()
    .int('El turno debe ser un número entero')
    .positive('Debe seleccionar un turno válido'),
  
  fecha: z.string()
    .min(1, 'La fecha es obligatoria')
    .refine((date) => {
      const parsedDate = new Date(date);
      return !isNaN(parsedDate.getTime());
    }, 'Debe ser una fecha válida'),
  
  observaciones: z.string()
    .max(500, 'Las observaciones no pueden exceder 500 caracteres')
    .optional()
    .or(z.literal('')),
});
