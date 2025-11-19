import { z } from 'zod';

export const crearEmpleadoSchema = z.object({
  nombre: z.string()
    .min(1, 'El nombre es obligatorio')
    .min(2, 'El nombre debe tener al menos 2 caracteres')
    .max(50, 'El nombre no puede exceder 50 caracteres')
    .regex(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, 'El nombre solo puede contener letras'),
  
  apellido: z.string()
    .min(1, 'El apellido es obligatorio')
    .min(2, 'El apellido debe tener al menos 2 caracteres')
    .max(50, 'El apellido no puede exceder 50 caracteres')
    .regex(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, 'El apellido solo puede contener letras'),
  
  email: z.string()
    .min(1, 'El correo electrónico es obligatorio')
    .email('Debe ser un correo electrónico válido')
    .toLowerCase(),
  
  username: z.string()
    .min(1, 'El nombre de usuario es obligatorio')
    .min(3, 'El nombre de usuario debe tener al menos 3 caracteres')
    .max(30, 'El nombre de usuario no puede exceder 30 caracteres')
    .regex(/^[a-zA-Z0-9_-]+$/, 'El nombre de usuario solo puede contener letras, números, guiones y guiones bajos'),
  
  password: z.string()
    .min(8, 'La contraseña debe tener al menos 8 caracteres')
    .max(100, 'La contraseña no puede exceder 100 caracteres'),
  
  codigoEmpleado: z.string()
    .min(1, 'El código de empleado es obligatorio')
    .min(3, 'El código debe tener al menos 3 caracteres')
    .max(20, 'El código no puede exceder 20 caracteres')
    .regex(/^[A-Z0-9_-]+$/i, 'El código solo puede contener letras, números, guiones y guiones bajos'),
  
  cargo: z.string()
    .max(100, 'El cargo no puede exceder 100 caracteres')
    .optional()
    .default('Empleado'),
  
  telefono: z.string()
    .regex(/^\+?[0-9]{10,15}$/, 'El teléfono debe tener entre 10 y 15 dígitos')
    .optional()
    .or(z.literal('')),
  
  numeroIdentificacion: z.string()
    .regex(/^[0-9]{6,20}$/, 'El número de identificación debe tener entre 6 y 20 dígitos')
    .optional()
    .or(z.literal('')),
  
  rol: z.enum(['EMPLEADO', 'ADMIN'], {
    errorMap: () => ({ message: 'El rol debe ser EMPLEADO o ADMIN' })
  })
});

export const editarEmpleadoSchema = z.object({
  nombre: z.string()
    .min(1, 'El nombre es obligatorio')
    .min(2, 'El nombre debe tener al menos 2 caracteres')
    .max(50, 'El nombre no puede exceder 50 caracteres')
    .regex(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, 'El nombre solo puede contener letras'),
  
  apellido: z.string()
    .min(1, 'El apellido es obligatorio')
    .min(2, 'El apellido debe tener al menos 2 caracteres')
    .max(50, 'El apellido no puede exceder 50 caracteres')
    .regex(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, 'El apellido solo puede contener letras'),
  
  email: z.email()
    .min(1, 'El correo electrónico es obligatorio')
    .toLowerCase(),
  
  codigoEmpleado: z.string()
    .min(1, 'El código de empleado es obligatorio'),
  
  cargo: z.string()
    .max(100, 'El cargo no puede exceder 100 caracteres')
    .optional()
    .default(''),
  
  telefono: z.string()
    .regex(/^\+?[0-9]{10,15}$/, 'El teléfono debe tener entre 10 y 15 dígitos')
    .optional()
    .or(z.literal('')),
  
  rol: z.enum(['EMPLEADO', 'ADMIN'], {
    errorMap: () => ({ message: 'El rol debe ser EMPLEADO o ADMIN' })
  })
});

export const cambiarPasswordSchema = z.object({
  nuevaPassword: z.string()
    .min(8, 'La contraseña debe tener al menos 8 caracteres')
    .max(100, 'La contraseña no puede exceder 100 caracteres')
});
