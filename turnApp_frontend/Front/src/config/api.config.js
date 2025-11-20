// Configuración de URLs de la API
export const API_CONFIG = {
  baseURL: 'http://localhost:8080', // API Gateway
  endpoints: {
    usuarios: '/usuarios-microservices/usuarios',
    turnos: '/turnos-microservice/api/turnos',
    horarios: '/horarios-microservice/api/horarios'
  }
};

// Timeout por defecto para las peticiones
export const REQUEST_TIMEOUT = 10000;
