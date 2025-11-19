import apiClient from './api.service';
import { API_CONFIG } from '../config/api.config';

const USUARIO_BASE_URL = API_CONFIG.endpoints.usuarios;

export const usuarioService = {
  // ==================== ENDPOINTS PÚBLICOS (Autenticados) ====================
  
  // Obtener perfil del usuario actual
  getProfile: async () => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/me`);
      return response.data;
    } catch (error) {
      // No loguear como error si es 404 o 500 (usuario no existe en BD)
      if (error.response?.status === 404 || error.response?.status === 500) {
        console.warn('Usuario no encontrado en la base de datos');
      } else {
        console.error('Error al obtener perfil:', error);
      }
      throw error;
    }
  },

  // ==================== ENDPOINTS ADMIN - CRUD ====================

  // Crear usuario (solo admin)
  create: async (data) => {
    try {
      const response = await apiClient.post(USUARIO_BASE_URL, data);
      return response.data;
    } catch (error) {
      console.error('Error al crear usuario:', error);
      throw error;
    }
  },

  // Obtener usuario por ID
  getById: async (id) => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener usuario:', error);
      throw error;
    }
  },

  // Obtener usuario por Keycloak ID
  getByKeycloakId: async (keycloakId) => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/keycloak/${keycloakId}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener usuario por Keycloak ID:', error);
      throw error;
    }
  },

  // Obtener usuario por email
  getByEmail: async (email) => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/email/${email}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener usuario por email:', error);
      throw error;
    }
  },

  // Listar usuarios con paginación
  getListado: async (page = 0, size = 20, sort = 'codigoEmpleado') => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/listado`, {
        params: { page, size, sort }
      });
      return response.data;
    } catch (error) {
      console.error('Error al listar usuarios:', error);
      throw error;
    }
  },

  // Listar todos los usuarios sin paginación (solo admin)
  getAll: async () => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/todos`);
      return response.data;
    } catch (error) {
      console.error('Error al listar todos los usuarios:', error);
      console.error('Detalles del error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        headers: error.response?.headers
      });
      throw error;
    }
  },

  // Buscar usuarios
  search: async (searchTerm, page = 0, size = 20) => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/buscar`, {
        params: { searchTerm, page, size }
      });
      return response.data;
    } catch (error) {
      console.error('Error al buscar usuarios:', error);
      throw error;
    }
  },

  // Actualizar usuario
  update: async (id, data) => {
    try {
      const response = await apiClient.put(`${USUARIO_BASE_URL}/${id}`, data);
      return response.data;
    } catch (error) {
      console.error('Error al actualizar usuario:', error);
      throw error;
    }
  },

  // Cambiar contraseña
  changePassword: async (id, password) => {
    try {
      const response = await apiClient.patch(`${USUARIO_BASE_URL}/${id}/password`, {
        nuevaPassword: password
      });
      return response.data;
    } catch (error) {
      console.error('Error al cambiar contraseña:', error);
      throw error;
    }
  },

  // Cambiar estado (habilitar/deshabilitar)
  changeStatus: async (id, enabled) => {
    try {
      const response = await apiClient.patch(`${USUARIO_BASE_URL}/${id}/estado`, null, {
        params: { enabled }
      });
      return response.data;
    } catch (error) {
      console.error('Error al cambiar estado:', error);
      throw error;
    }
  },

  // Eliminar usuario (solo admin)
  delete: async (id) => {
    try {
      const response = await apiClient.delete(`${USUARIO_BASE_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error al eliminar usuario:', error);
      throw error;
    }
  }
};
