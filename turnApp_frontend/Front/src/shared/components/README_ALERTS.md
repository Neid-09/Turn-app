# AlertDialog - Componente de Alertas

Componente reutilizable para mostrar alertas, confirmaciones y notificaciones en la aplicación.

## Uso

### 1. Importar el hook y el componente

```jsx
import AlertDialog from '../../../shared/components/AlertDialog';
import { useAlert } from '../../../shared/hooks/useAlert';
```

### 2. Inicializar el hook en tu componente

```jsx
export default function MiComponente() {
  const { alert, confirm, success, error, warning, close } = useAlert();
  
  // ... resto del código
}
```

### 3. Agregar el componente AlertDialog en el JSX

```jsx
return (
  <div>
    {/* Tu contenido aquí */}
    
    <AlertDialog
      isOpen={alert.isOpen}
      onClose={close}
      onConfirm={alert.onConfirm}
      title={alert.title}
      message={alert.message}
      type={alert.type}
      confirmText={alert.confirmText}
      cancelText={alert.cancelText}
    />
  </div>
);
```

## Métodos disponibles

### `confirm(message, onConfirm, title)`

Muestra un diálogo de confirmación con botones "Confirmar" y "Cancelar".

```jsx
const handleDelete = () => {
  confirm(
    '¿Estás seguro de eliminar este elemento?',
    async () => {
      // Código a ejecutar si el usuario confirma
      await deleteItem();
      success('Elemento eliminado exitosamente');
    },
    'Confirmar eliminación'
  );
};
```

### `success(message, title)`

Muestra un mensaje de éxito con icono verde.

```jsx
success('¡Operación completada exitosamente!');
// o con título personalizado
success('Usuario creado correctamente', 'Éxito');
```

### `error(message, title)`

Muestra un mensaje de error con icono rojo.

```jsx
error('Ocurrió un error al procesar la solicitud');
// o con título personalizado
error('No se pudo conectar al servidor', 'Error de conexión');
```

### `warning(message, title)`

Muestra un mensaje de advertencia con icono amarillo.

```jsx
warning('Esta acción puede tener consecuencias');
// o con título personalizado
warning('Verifica los datos antes de continuar', 'Atención');
```

### `close()`

Cierra la alerta actual.

```jsx
close();
```

## Tipos de alertas

- **confirm**: Muestra botones de confirmar y cancelar (color azul)
- **success**: Muestra solo botón de aceptar (color verde)
- **error**: Muestra solo botón de aceptar (color rojo)
- **warning**: Muestra solo botón de aceptar (color amarillo)

## Ejemplo completo

```jsx
import { useState } from 'react';
import AlertDialog from '../../../shared/components/AlertDialog';
import { useAlert } from '../../../shared/hooks/useAlert';

export default function UserList() {
  const { alert, confirm, success, error, close } = useAlert();
  const [users, setUsers] = useState([]);

  const handleDeleteUser = (userId, userName) => {
    confirm(
      `¿Estás seguro de eliminar a ${userName}? Esta acción no se puede deshacer.`,
      async () => {
        try {
          await deleteUser(userId);
          success('Usuario eliminado exitosamente');
          loadUsers();
        } catch (err) {
          error('Error al eliminar el usuario');
        }
      },
      'Eliminar Usuario'
    );
  };

  return (
    <div>
      {/* Tu lista de usuarios */}
      
      <AlertDialog
        isOpen={alert.isOpen}
        onClose={close}
        onConfirm={alert.onConfirm}
        title={alert.title}
        message={alert.message}
        type={alert.type}
        confirmText={alert.confirmText}
        cancelText={alert.cancelText}
      />
    </div>
  );
}
```

## Ventajas sobre window.alert y window.confirm

1. **Consistencia visual**: Mantiene el diseño de la aplicación
2. **Personalizable**: Puedes ajustar colores, textos y comportamientos
3. **Accesible**: Mejor experiencia de usuario
4. **Asíncrono**: Funciona mejor con operaciones asíncronas
5. **Tipado**: Soporte para diferentes tipos de alertas
6. **Responsive**: Se adapta a diferentes tamaños de pantalla
