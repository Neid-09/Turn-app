import axios from 'axios';
import keycloak from '../config/keycloak.config';
import { API_CONFIG, REQUEST_TIMEOUT } from '../config/api.config';

// Crear instancia de axios
const apiClient = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: REQUEST_TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar el token JWT en cada petición
apiClient.interceptors.request.use(
  (config) => {
    if (keycloak.token) {
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores de autenticación
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Si el token expiró (401) y no hemos reintentado aún
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Intentar refrescar el token
        const refreshed = await keycloak.updateToken(5);
        
        if (refreshed) {
          // Reintentar la petición original con el nuevo token
          originalRequest.headers.Authorization = `Bearer ${keycloak.token}`;
          return apiClient(originalRequest);
        }
      } catch (refreshError) {
        // Si falla el refresh, redirigir al login
        keycloak.login();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
