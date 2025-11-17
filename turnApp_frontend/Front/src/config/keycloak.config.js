import Keycloak from 'keycloak-js';

// Configuración de Keycloak
const keycloakConfig = {
  url: 'http://localhost:9091',
  realm: 'myturnapp-microservices-realm',
  clientId: 'turnapp-frontend-client'
};

// Instancia de Keycloak
const keycloak = new Keycloak(keycloakConfig);

// Opciones de inicialización
export const initOptions = {
  onLoad: 'check-sso', // Verifica si hay sesión activa sin forzar login
  checkLoginIframe: false, // Mejora performance
  pkceMethod: 'S256', // Seguridad adicional
  enableLogging: true, // Habilitar logging para debug
  flow: 'standard' // Usar flujo estándar de autenticación
};

export default keycloak;
