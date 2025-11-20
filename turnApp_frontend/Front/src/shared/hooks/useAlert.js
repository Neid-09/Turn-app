import { useState, useCallback } from 'react';

/**
 * Hook personalizado para manejar alertas
 * @returns {Object} Objeto con métodos y estado de alertas
 */
export function useAlert() {
  const [alert, setAlert] = useState({
    isOpen: false,
    title: '',
    message: '',
    type: 'confirm',
    onConfirm: null,
    confirmText: 'Aceptar',
    cancelText: 'Cancelar'
  });

  /**
   * Muestra una alerta de confirmación
   * @param {string} message - Mensaje a mostrar
   * @param {function} onConfirm - Función a ejecutar si se confirma
   * @param {string} title - Título opcional
   */
  const confirm = useCallback((message, onConfirm, title = 'Confirmar acción') => {
    setAlert({
      isOpen: true,
      title,
      message,
      type: 'confirm',
      onConfirm,
      confirmText: 'Confirmar',
      cancelText: 'Cancelar'
    });
  }, []);

  /**
   * Muestra una alerta de éxito
   * @param {string} message - Mensaje a mostrar
   * @param {string} title - Título opcional
   */
  const success = useCallback((message, title = 'Éxito') => {
    setAlert({
      isOpen: true,
      title,
      message,
      type: 'success',
      onConfirm: null,
      confirmText: 'Aceptar',
      cancelText: 'Cancelar'
    });
  }, []);

  /**
   * Muestra una alerta de error
   * @param {string} message - Mensaje a mostrar
   * @param {string} title - Título opcional
   */
  const error = useCallback((message, title = 'Error') => {
    setAlert({
      isOpen: true,
      title,
      message,
      type: 'error',
      onConfirm: null,
      confirmText: 'Aceptar',
      cancelText: 'Cancelar'
    });
  }, []);

  /**
   * Muestra una alerta de advertencia
   * @param {string} message - Mensaje a mostrar
   * @param {string} title - Título opcional
   */
  const warning = useCallback((message, title = 'Advertencia') => {
    setAlert({
      isOpen: true,
      title,
      message,
      type: 'warning',
      onConfirm: null,
      confirmText: 'Aceptar',
      cancelText: 'Cancelar'
    });
  }, []);

  /**
   * Cierra la alerta actual
   */
  const close = useCallback(() => {
    setAlert(prev => ({ ...prev, isOpen: false }));
  }, []);

  return {
    alert,
    confirm,
    success,
    error,
    warning,
    close
  };
}
