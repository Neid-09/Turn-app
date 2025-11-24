import apiClient from './api.service';
import { API_CONFIG } from '../config/api.config';

const HORARIO_BASE_URL = API_CONFIG.endpoints.horarios;

export const horarioService = {
  // ==================== CRUD DE HORARIOS ====================
  
  // Crear horario (estado: BORRADOR)
  crear: async (data) => {
    try {
      const response = await apiClient.post(HORARIO_BASE_URL, data);
      return response.data;
    } catch (error) {
      console.error('Error al crear horario:', error);
      throw error;
    }
  },

  // Listar todos los horarios
  listarTodos: async () => {
    try {
      const response = await apiClient.get(HORARIO_BASE_URL);
      return response.data;
    } catch (error) {
      console.error('Error al listar horarios:', error);
      throw error;
    }
  },

  // Obtener horario por ID
  obtenerPorId: async (id, incluirDetalles = true) => {
    try {
      const response = await apiClient.get(`${HORARIO_BASE_URL}/${id}`, {
        params: { incluirDetalles }
      });
      return response.data;
    } catch (error) {
      console.error('Error al obtener horario:', error);
      throw error;
    }
  },

  // Actualizar horario (solo BORRADOR)
  actualizar: async (id, data) => {
    try {
      const response = await apiClient.put(`${HORARIO_BASE_URL}/${id}`, data);
      return response.data;
    } catch (error) {
      console.error('Error al actualizar horario:', error);
      throw error;
    }
  },

  // Eliminar horario (solo BORRADOR)
  eliminar: async (id) => {
    try {
      await apiClient.delete(`${HORARIO_BASE_URL}/${id}`);
    } catch (error) {
      console.error('Error al eliminar horario:', error);
      throw error;
    }
  },

  // ==================== GESTIÓN DE DETALLES ====================

  // Agregar detalle individual
  agregarDetalle: async (horarioId, detalle) => {
    try {
      const response = await apiClient.post(
        `${HORARIO_BASE_URL}/${horarioId}/detalles`,
        detalle
      );
      return response.data;
    } catch (error) {
      console.error('Error al agregar detalle:', error);
      throw error;
    }
  },

  // Agregar múltiples detalles en lote
  agregarDetallesLote: async (horarioId, detalles) => {
    try {
      const response = await apiClient.post(
        `${HORARIO_BASE_URL}/${horarioId}/detalles/lote`,
        detalles
      );
      return response.data;
    } catch (error) {
      console.error('Error al agregar detalles en lote:', error);
      throw error;
    }
  },

  // Eliminar detalle
  eliminarDetalle: async (horarioId, detalleId) => {
    try {
      await apiClient.delete(
        `${HORARIO_BASE_URL}/${horarioId}/detalles/${detalleId}`
      );
    } catch (error) {
      console.error('Error al eliminar detalle:', error);
      throw error;
    }
  },

  // Eliminar múltiples detalles en lote
  eliminarDetallesLote: async (horarioId, detalleIds) => {
    try {
      await apiClient.delete(
        `${HORARIO_BASE_URL}/${horarioId}/detalles/lote`,
        { data: detalleIds }
      );
    } catch (error) {
      console.error('Error al eliminar detalles en lote:', error);
      throw error;
    }
  },

  // ==================== PUBLICACIÓN ====================

  // Publicar horario (crea asignaciones en turnos-microservice)
  publicar: async (id) => {
    try {
      const response = await apiClient.post(
        `${HORARIO_BASE_URL}/${id}/publicar`
      );
      return response.data;
    } catch (error) {
      console.error('Error al publicar horario:', error);
      throw error;
    }
  },

  // ==================== CONSULTAS CONSOLIDADAS ====================

  // Obtener vista consolidada (horario + asignaciones reales)
  obtenerConsolidado: async (id) => {
    try {
      const response = await apiClient.get(
        `${HORARIO_BASE_URL}/${id}/consolidado`
      );
      return response.data;
    } catch (error) {
      console.error('Error al obtener vista consolidada:', error);
      throw error;
    }
  },

  // Buscar horarios por fecha
  buscarPorFecha: async (fecha) => {
    try {
      const response = await apiClient.get(`${HORARIO_BASE_URL}/fecha`, {
        params: { fecha }
      });
      return response.data;
    } catch (error) {
      console.error('Error al buscar horarios por fecha:', error);
      throw error;
    }
  },
};
