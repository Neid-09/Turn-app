import apiClient from './api.service';
import { API_CONFIG } from '../config/api.config';

const USUARIO_BASE_URL = API_CONFIG.endpoints.usuarios;

export const usuarioService = {
  // Obtener perfil del usuario actual
  getProfile: async () => {
    try {
      const response = await apiClient.get(`${USUARIO_BASE_URL}/me`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener perfil:', error);
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

  // Listar todos los usuarios (solo admin)
  getAll: async () => {
    try {
      const response = await apiClient.get(USUARIO_BASE_URL);
      return response.data;
    } catch (error) {
      console.error('Error al listar usuarios:', error);
      throw error;
    }
  },

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

  // Eliminar usuario (solo admin)
  delete: async (id) => {
    try {
      await apiClient.delete(`${USUARIO_BASE_URL}/${id}`);
    } catch (error) {
      console.error('Error al eliminar usuario:', error);
      throw error;
    }
  }
};
