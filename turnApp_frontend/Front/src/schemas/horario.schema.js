import { z } from 'zod';

// Schema para crear/actualizar horario
export const horarioSchema = z.object({
  nombre: z.string()
    .min(1, 'El nombre es obligatorio')
    .max(100, 'El nombre no puede exceder 100 caracteres'),
  
  fechaInicio: z.string()
    .min(1, 'La fecha de inicio es obligatoria')
    .refine((date) => {
      const parsedDate = new Date(date);
      return !isNaN(parsedDate.getTime());
    }, 'Debe ser una fecha válida'),
  
  fechaFin: z.string()
    .min(1, 'La fecha de fin es obligatoria')
    .refine((date) => {
      const parsedDate = new Date(date);
      return !isNaN(parsedDate.getTime());
    }, 'Debe ser una fecha válida'),
  
  descripcion: z.string()
    .max(500, 'La descripción no puede exceder 500 caracteres')
    .optional()
    .or(z.literal('')),
}).refine((data) => {
  const inicio = new Date(data.fechaInicio);
  const fin = new Date(data.fechaFin);
  return fin >= inicio;
}, {
  message: 'La fecha de fin debe ser posterior o igual a la fecha de inicio',
  path: ['fechaFin'],
});

// Schema para agregar detalle individual
export const horarioDetalleSchema = z.object({
  usuarioId: z.string()
    .min(1, 'El empleado es obligatorio'),
  
  fecha: z.string()
    .min(1, 'La fecha es obligatoria')
    .refine((date) => {
      const parsedDate = new Date(date);
      return !isNaN(parsedDate.getTime());
    }, 'Debe ser una fecha válida'),
  
  turnoId: z.number()
    .int('El turno debe ser un número entero')
    .positive('Debe seleccionar un turno válido'),
  
  observaciones: z.string()
    .max(500, 'Las observaciones no pueden exceder 500 caracteres')
    .optional()
    .or(z.literal('')),
});

// Schema para agregar múltiples detalles
export const horarioDetalleLoteSchema = z.array(horarioDetalleSchema)
  .min(1, 'Debe agregar al menos un detalle');
