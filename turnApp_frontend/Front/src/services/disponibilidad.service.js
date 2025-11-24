import apiClient from './api.service';
import { API_CONFIG } from '../config/api.config';

const DISPONIBILIDAD_BASE_URL = `${API_CONFIG.endpoints.turnos}api/disponibilidades`;

export const disponibilidadService = {
  // ==================== CRUD DE DISPONIBILIDADES ====================
  
  // Crear disponibilidad
  crear: async (data) => {
    try {
      const response = await apiClient.post(DISPONIBILIDAD_BASE_URL, data);
      return response.data;
    } catch (error) {
      console.error('Error al crear disponibilidad:', error);
      throw error;
    }
  },

  // Obtener disponibilidad por ID
  obtenerPorId: async (id) => {
    try {
      const response = await apiClient.get(`${DISPONIBILIDAD_BASE_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener disponibilidad:', error);
      throw error;
    }
  },

  // Obtener disponibilidades por usuario
  obtenerPorUsuario: async (keycloakId) => {
    try {
      const response = await apiClient.get(
        `${DISPONIBILIDAD_BASE_URL}/usuario/${keycloakId}`
      );
      return response.data;
    } catch (error) {
      console.error('Error al obtener disponibilidades por usuario:', error);
      throw error;
    }
  },

  // Obtener disponibilidades por período
  obtenerPorPeriodo: async (fechaInicio, fechaFin) => {
    try {
      const response = await apiClient.get(
        `${DISPONIBILIDAD_BASE_URL}/periodo`,
        { params: { fechaInicio, fechaFin } }
      );
      return response.data;
    } catch (error) {
      console.error('Error al obtener disponibilidades por período:', error);
      throw error;
    }
  },

  // Actualizar disponibilidad
  actualizar: async (id, data) => {
    try {
      const response = await apiClient.put(
        `${DISPONIBILIDAD_BASE_URL}/${id}`,
        data
      );
      return response.data;
    } catch (error) {
      console.error('Error al actualizar disponibilidad:', error);
      throw error;
    }
  },

  // Desactivar disponibilidad
  desactivar: async (id) => {
    try {
      await apiClient.patch(`${DISPONIBILIDAD_BASE_URL}/${id}/desactivar`);
    } catch (error) {
      console.error('Error al desactivar disponibilidad:', error);
      throw error;
    }
  },

  // Eliminar disponibilidad
  eliminar: async (id) => {
    try {
      await apiClient.delete(`${DISPONIBILIDAD_BASE_URL}/${id}`);
    } catch (error) {
      console.error('Error al eliminar disponibilidad:', error);
      throw error;
    }
  },

  /**
   * Obtener usuarios disponibles aplicando lógica HÍBRIDA del sistema.
   * 
   * Este endpoint:
   * - Consulta todos los usuarios del microservicio de usuarios
   * - Valida preferencias horarias si las hay configuradas
   * - Excluye usuarios que ya tienen asignaciones en la fecha especificada
   * 
   * @param {string} fecha - Fecha en formato YYYY-MM-DD (ej: 2025-11-27)
   * @param {string} horaInicio - Hora de inicio en formato HH:mm (ej: 08:00)
   * @param {string} horaFin - Hora de fin en formato HH:mm (ej: 16:00)
   * @returns {Promise<Array>} Lista de usuarios disponibles con información de preferencias
   */
  obtenerUsuariosDisponibles: async (fecha, horaInicio, horaFin) => {
    try {
      const response = await apiClient.get(
        `${DISPONIBILIDAD_BASE_URL}/usuarios-disponibles`,
        { 
          params: { 
            fecha,      // yyyy-MM-dd
            horaInicio, // HH:mm
            horaFin     // HH:mm
          } 
        }
      );
      return response.data;
    } catch (error) {
      console.error('Error al obtener usuarios disponibles:', error);
      throw error;
    }
  },
};
