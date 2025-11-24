import apiClient from './api.service';
import { API_CONFIG } from '../config/api.config';

// Considerar que para turnos, disponibilidades... hay un path diferente

const TURNO_BASE_URL = API_CONFIG.endpoints.turnos; 
const API_TURNOS = `${TURNO_BASE_URL}api/turnos`;
const API_ASIGNACIONES = `${TURNO_BASE_URL}api/asignaciones`;

export const turnoService = {
  // ==================== CRUD DE TURNOS ====================
  
  // Crear turno
  crear: async (data) => {
    try {
      const response = await apiClient.post(API_TURNOS, data);
      return response.data;
    } catch (error) {
      console.error('Error al crear turno:', error);
      throw error;
    }
  },

  // Listar todos los turnos
  listarTodos: async () => {
    try {
      const response = await apiClient.get(API_TURNOS);
      return response.data;
    } catch (error) {
      console.error('Error al listar turnos:', error);
      throw error;
    }
  },

  // Listar turnos activos
  listarActivos: async () => {
    try {
      const response = await apiClient.get(`${API_TURNOS}/activos`);
      return response.data;
    } catch (error) {
      console.error('Error al listar turnos activos:', error);
      throw error;
    }
  },

  // Obtener turno por ID
  obtenerPorId: async (id) => {
    try {
      const response = await apiClient.get(`${API_TURNOS}/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener turno:', error);
      throw error;
    }
  },

  // Actualizar turno
  actualizar: async (id, data) => {
    try {
      const response = await apiClient.put(`${API_TURNOS}/${id}`, data);
      return response.data;
    } catch (error) {
      console.error('Error al actualizar turno:', error);
      throw error;
    }
  },

  // Eliminar turno (soft delete)
  eliminar: async (id) => {
    try {
      await apiClient.delete(`${API_TURNOS}/${id}`);
    } catch (error) {
      console.error('Error al eliminar turno:', error);
      throw error;
    }
  },

  // ==================== ASIGNACIONES ====================

  // Crear asignación
  crearAsignacion: async (data) => {
    try {
      const response = await apiClient.post(`${API_ASIGNACIONES}`, data);
      return response.data;
    } catch (error) {
      console.error('Error al crear asignación:', error);
      throw error;
    }
  },

  // Obtener asignaciones por usuario
  obtenerAsignacionesPorUsuario: async (usuarioId) => {
    try {
      const response = await apiClient.get(
        `${API_ASIGNACIONES}/usuario/${usuarioId}`
      );
      return response.data;
    } catch (error) {
      console.error('Error al obtener asignaciones por usuario:', error);
      throw error;
    }
  },

  // Obtener asignaciones por fecha
  obtenerAsignacionesPorFecha: async (fecha) => {
    try {
      const response = await apiClient.get(
        `${API_ASIGNACIONES}/fecha`,
        { params: { fecha } }
      );
      return response.data;
    } catch (error) {
      console.error('Error al obtener asignaciones por fecha:', error);
      throw error;
    }
  },

  // Obtener asignaciones por período
  obtenerAsignacionesPorPeriodo: async (fechaInicio, fechaFin) => {
    try {
      const response = await apiClient.get(
        `${API_ASIGNACIONES}/periodo`,
        { params: { fechaInicio, fechaFin } }
      );
      return response.data;
    } catch (error) {
      console.error('Error al obtener asignaciones por período:', error);
      throw error;
    }
  },

  // Cancelar asignación
  cancelarAsignacion: async (id) => {
    try {
      await apiClient.patch(`${API_ASIGNACIONES}/${id}/cancelar`);
    } catch (error) {
      console.error('Error al cancelar asignación:', error);
      throw error;
    }
  },

  // Completar asignación
  completarAsignacion: async (id) => {
    try {
      await apiClient.patch(`${API_ASIGNACIONES}/${id}/completar`);
    } catch (error) {
      console.error('Error al completar asignación:', error);
      throw error;
    }
  },
};
