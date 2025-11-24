import { useState } from 'react';
import { FiClock, FiAlertCircle } from 'react-icons/fi';
import { disponibilidadService } from '../../../../services/disponibilidad.service';
import TablaAsignaciones from './TablaAsignaciones';
import ModalSeleccionEmpleado from './ModalSeleccionEmpleado';
import { useFechas } from '../../hooks/useFechas';

export default function Paso2AsignarTurnos({ 
  fechaInicio, 
  fechaFin, 
  turnos, 
  asignaciones, 
  onAgregarAsignaciones,
  onEliminarAsignacion,
  loadingUsuarios,
  setLoadingUsuarios
}) {
  const [fechaSeleccionada, setFechaSeleccionada] = useState('');
  const [turnoSeleccionado, setTurnoSeleccionado] = useState(null);
  const [usuariosDisponibles, setUsuariosDisponibles] = useState([]);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [modoAsignacion, setModoAsignacion] = useState('individual');
  const [diasSeleccionados, setDiasSeleccionados] = useState([]);

  const fechas = useFechas(fechaInicio, fechaFin);

  // Cargar usuarios disponibles para una fecha y turno específicos
  const cargarUsuariosDisponibles = async (fecha, turno) => {
    setLoadingUsuarios(true);
    try {
      const usuarios = await disponibilidadService.obtenerUsuariosDisponibles(
        fecha,
        turno.horaInicio,
        turno.horaFin
      );
      setUsuariosDisponibles(usuarios);
    } catch (error) {
      console.error('Error al cargar usuarios disponibles:', error);
      setUsuariosDisponibles([]);
    } finally {
      setLoadingUsuarios(false);
    }
  };

  // Abrir modal para asignar turno
  const abrirModalAsignacion = async (fecha, turno) => {
    setFechaSeleccionada(fecha);
    setTurnoSeleccionado(turno);
    setMostrarModal(true);
    setModoAsignacion('individual');
    setDiasSeleccionados([]);
    await cargarUsuariosDisponibles(fecha, turno);
  };

  // Obtener fechas según el modo de asignación
  const obtenerFechasParaAsignar = () => {
    const fechasParaAsignar = [];

    switch (modoAsignacion) {
      case 'individual':
        fechasParaAsignar.push(fechaSeleccionada);
        break;

      case 'periodo':
        fechasParaAsignar.push(...fechas);
        break;

      case 'semana':
        fechas.forEach(fecha => {
          const dia = new Date(fecha + 'T00:00:00').getDay();
          if (dia >= 1 && dia <= 5) fechasParaAsignar.push(fecha);
        });
        break;

      case 'finSemana':
        fechas.forEach(fecha => {
          const dia = new Date(fecha + 'T00:00:00').getDay();
          if (dia === 0 || dia === 6) fechasParaAsignar.push(fecha);
        });
        break;

      case 'personalizado':
        fechas.forEach(fecha => {
          const dia = new Date(fecha + 'T00:00:00').getDay();
          const diaAjustado = dia === 0 ? 6 : dia - 1;
          if (diasSeleccionados.includes(diaAjustado)) fechasParaAsignar.push(fecha);
        });
        break;

      default:
        fechasParaAsignar.push(fechaSeleccionada);
    }

    return fechasParaAsignar;
  };

  // Asignar usuario a un turno
  const asignarUsuario = (usuario) => {
    const fechasParaAsignar = obtenerFechasParaAsignar();
    const nuevasAsignaciones = [];

    fechasParaAsignar.forEach(fecha => {
      const yaExiste = asignaciones.some(
        a => a.fecha === fecha && a.turnoId === turnoSeleccionado.id && a.usuarioId === usuario.keycloakId
      );

      if (!yaExiste) {
        nuevasAsignaciones.push({
          id: `${fecha}-${turnoSeleccionado.id}-${usuario.keycloakId}`,
          usuarioId: usuario.keycloakId,
          nombreEmpleado: usuario.nombreCompleto,
          codigoEmpleado: usuario.codigoEmpleado,
          fecha: fecha,
          turnoId: turnoSeleccionado.id,
          nombreTurno: turnoSeleccionado.nombre,
          horaInicio: turnoSeleccionado.horaInicio,
          horaFin: turnoSeleccionado.horaFin,
          cumplePreferencias: usuario.cumplePreferencias,
          tienePreferencias: usuario.tienePreferencias,
          observaciones: usuario.tienePreferencias
            ? (usuario.cumplePreferencias 
                ? 'Dentro de preferencia horaria' 
                : '⚠️ Fuera de preferencia horaria')
            : 'Sin preferencias configuradas'
        });
      }
    });

    onAgregarAsignaciones(nuevasAsignaciones);
    setMostrarModal(false);
  };

  return (
    <div className="space-y-4">
      <div className="text-center mb-6">
        <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
          <FiClock className="w-8 h-8 text-purple-600" />
        </div>
        <h3 className="text-xl font-bold text-gray-900">Asignar Turnos</h3>
        <p className="text-sm text-gray-500 mt-1">
          Selecciona turnos y asigna empleados para cada fecha
        </p>
      </div>

      {fechas.length === 0 ? (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 text-center">
          <FiAlertCircle className="w-8 h-8 text-yellow-600 mx-auto mb-2" />
          <p className="text-sm text-yellow-800">
            Primero define las fechas del horario en el paso anterior
          </p>
        </div>
      ) : (
        <>
          {/* Resumen de asignaciones */}
          <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-semibold text-purple-900">Asignaciones creadas</p>
                <p className="text-sm text-purple-700">
                  {asignaciones.length} turnos asignados en {fechas.length} días
                </p>
              </div>
              <div className="text-3xl font-bold text-purple-600">
                {asignaciones.length}
              </div>
            </div>
          </div>

          <TablaAsignaciones
            fechas={fechas}
            turnos={turnos}
            asignaciones={asignaciones}
            onAbrirModal={abrirModalAsignacion}
            onEliminarAsignacion={onEliminarAsignacion}
          />
        </>
      )}

      <ModalSeleccionEmpleado
        mostrar={mostrarModal}
        onCerrar={() => setMostrarModal(false)}
        fechaSeleccionada={fechaSeleccionada}
        turnoSeleccionado={turnoSeleccionado}
        usuariosDisponibles={usuariosDisponibles}
        loadingUsuarios={loadingUsuarios}
        onAsignarUsuario={asignarUsuario}
        modoAsignacion={modoAsignacion}
        setModoAsignacion={setModoAsignacion}
        fechas={fechas}
        diasSeleccionados={diasSeleccionados}
        setDiasSeleccionados={setDiasSeleccionados}
      />
    </div>
  );
}
