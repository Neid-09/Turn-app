import apiClient from './api.service';
import { API_CONFIG } from '../config/api.config';

const TURNO_BASE_URL = API_CONFIG.endpoints.turnos;

export const turnoService = {
  // ==================== CRUD DE TURNOS ====================
  
  // Crear turno
  crear: async (data) => {
    try {
      const response = await apiClient.post(TURNO_BASE_URL, data);
      return response.data;
    } catch (error) {
      console.error('Error al crear turno:', error);
      throw error;
    }
  },

  // Listar todos los turnos
  listarTodos: async () => {
    try {
      const response = await apiClient.get(TURNO_BASE_URL);
      return response.data;
    } catch (error) {
      console.error('Error al listar turnos:', error);
      throw error;
    }
  },

  // Listar turnos activos
  listarActivos: async () => {
    try {
      const response = await apiClient.get(`${TURNO_BASE_URL}/activos`);
      return response.data;
    } catch (error) {
      console.error('Error al listar turnos activos:', error);
      throw error;
    }
  },

  // Obtener turno por ID
  obtenerPorId: async (id) => {
    try {
      const response = await apiClient.get(`${TURNO_BASE_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener turno:', error);
      throw error;
    }
  },

  // Actualizar turno
  actualizar: async (id, data) => {
    try {
      const response = await apiClient.put(`${TURNO_BASE_URL}/${id}`, data);
      return response.data;
    } catch (error) {
      console.error('Error al actualizar turno:', error);
      throw error;
    }
  },

  // Eliminar turno (soft delete)
  eliminar: async (id) => {
    try {
      await apiClient.delete(`${TURNO_BASE_URL}/${id}`);
    } catch (error) {
      console.error('Error al eliminar turno:', error);
      throw error;
    }
  },

  // ==================== ASIGNACIONES ====================

  // Crear asignación
  crearAsignacion: async (data) => {
    try {
      const response = await apiClient.post(`${TURNO_BASE_URL}/asignaciones`, data);
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
        `${TURNO_BASE_URL}/asignaciones/usuario/${usuarioId}`
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
        `${TURNO_BASE_URL}/asignaciones/fecha`,
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
        `${TURNO_BASE_URL}/asignaciones/periodo`,
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
      await apiClient.patch(`${TURNO_BASE_URL}/asignaciones/${id}/cancelar`);
    } catch (error) {
      console.error('Error al cancelar asignación:', error);
      throw error;
    }
  },

  // Completar asignación
  completarAsignacion: async (id) => {
    try {
      await apiClient.patch(`${TURNO_BASE_URL}/asignaciones/${id}/completar`);
    } catch (error) {
      console.error('Error al completar asignación:', error);
      throw error;
    }
  },
};
