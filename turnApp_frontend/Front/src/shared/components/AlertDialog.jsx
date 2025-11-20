import { FiAlertCircle, FiCheckCircle, FiX, FiAlertTriangle } from 'react-icons/fi';

/**
 * Componente de diálogo de alerta reutilizable
 * @param {Object} props
 * @param {boolean} props.isOpen - Controla si el diálogo está visible
 * @param {function} props.onClose - Función a ejecutar cuando se cierra el diálogo
 * @param {function} props.onConfirm - Función opcional a ejecutar cuando se confirma (para confirmaciones)
 * @param {string} props.title - Título del diálogo
 * @param {string} props.message - Mensaje del diálogo
 * @param {('success'|'error'|'warning'|'confirm')} props.type - Tipo de alerta
 * @param {string} props.confirmText - Texto del botón de confirmación (default: 'Aceptar')
 * @param {string} props.cancelText - Texto del botón de cancelar (default: 'Cancelar')
 */
export default function AlertDialog({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  type = 'confirm',
  confirmText = 'Aceptar',
  cancelText = 'Cancelar'
}) {
  if (!isOpen) return null;

  const handleConfirm = () => {
    if (onConfirm) {
      onConfirm();
    }
    onClose();
  };

  const handleCancel = () => {
    onClose();
  };

  // Configuraciones por tipo de alerta
  const typeConfig = {
    success: {
      icon: FiCheckCircle,
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
      borderColor: 'border-green-500',
      buttonBg: 'bg-green-600 hover:bg-green-700',
      showCancel: false
    },
    error: {
      icon: FiAlertCircle,
      iconBg: 'bg-red-100',
      iconColor: 'text-red-600',
      borderColor: 'border-red-500',
      buttonBg: 'bg-red-600 hover:bg-red-700',
      showCancel: false
    },
    warning: {
      icon: FiAlertTriangle,
      iconBg: 'bg-yellow-100',
      iconColor: 'text-yellow-600',
      borderColor: 'border-yellow-500',
      buttonBg: 'bg-yellow-600 hover:bg-yellow-700',
      showCancel: false
    },
    confirm: {
      icon: FiAlertCircle,
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
      borderColor: 'border-blue-500',
      buttonBg: 'bg-blue-600 hover:bg-blue-700',
      showCancel: true
    }
  };

  const config = typeConfig[type] || typeConfig.confirm;
  const Icon = config.icon;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-9999 animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full animate-slide-up overflow-hidden">
        {/* Header */}
        <div className="relative px-6 pt-6 pb-4">
          <button
            onClick={handleCancel}
            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <FiX className="w-5 h-5" />
          </button>
          
          <div className="flex items-start gap-4">
            <div className={`w-12 h-12 ${config.iconBg} rounded-full flex items-center justify-center shrink-0`}>
              <Icon className={`w-6 h-6 ${config.iconColor}`} />
            </div>
            
            <div className="flex-1 pt-1">
              <h3 className="text-xl font-bold text-gray-900 mb-2">
                {title}
              </h3>
              <p className="text-gray-600 text-sm leading-relaxed">
                {message}
              </p>
            </div>
          </div>
        </div>

        {/* Footer con botones */}
        <div className={`px-6 py-4 bg-gray-50 flex gap-3 ${config.showCancel ? 'justify-end' : 'justify-center'}`}>
          {config.showCancel && (
            <button
              onClick={handleCancel}
              className="px-6 py-2.5 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
            >
              {cancelText}
            </button>
          )}
          <button
            onClick={handleConfirm}
            className={`px-6 py-2.5 ${config.buttonBg} text-white rounded-lg transition-colors font-medium shadow-md hover:shadow-lg`}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
