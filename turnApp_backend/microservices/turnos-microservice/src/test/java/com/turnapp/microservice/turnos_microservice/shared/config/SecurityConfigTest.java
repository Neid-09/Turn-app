package com.turnapp.microservice.turnos_microservice.shared.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para la configuración de seguridad.
 * 
 * Verifica que:
 * - Los endpoints requieren autenticación
 * - Los roles EMPLEADO y ADMIN tienen los permisos correctos
 * - Las restricciones de acceso se aplican correctamente
 * 
 * @author TurnApp Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de Seguridad - Control de Acceso por Roles")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ========== TESTS SIN AUTENTICACIÓN ==========

    @Test
    @DisplayName("Sin token - Debe denegar acceso (401)")
    void testSinToken_DebeDenegarAcceso() throws Exception {
        mockMvc.perform(get("/api/turnos"))
                .andExpect(status().isUnauthorized());
    }

    // ========== TESTS CON ROL EMPLEADO ==========

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - Puede consultar turnos (GET)")
    void testEmpleado_PuedeConsultarTurnos() throws Exception {
        mockMvc.perform(get("/api/turnos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - Puede consultar turno específico (GET)")
    void testEmpleado_PuedeConsultarTurnoEspecifico() throws Exception {
        mockMvc.perform(get("/api/turnos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - NO puede crear turno (POST)")
    void testEmpleado_NoPuedeCrearTurno() throws Exception {
        mockMvc.perform(post("/api/turnos")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Turno Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - NO puede actualizar turno (PUT)")
    void testEmpleado_NoPuedeActualizarTurno() throws Exception {
        mockMvc.perform(put("/api/turnos/1")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Turno Actualizado\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - NO puede eliminar turno (DELETE)")
    void testEmpleado_NoPuedeEliminarTurno() throws Exception {
        mockMvc.perform(delete("/api/turnos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - Puede consultar asignaciones (GET)")
    void testEmpleado_PuedeConsultarAsignaciones() throws Exception {
        mockMvc.perform(get("/api/asignaciones/usuario/keycloak-id-123"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - NO puede crear asignación (POST)")
    void testEmpleado_NoPuedeCrearAsignacion() throws Exception {
        mockMvc.perform(post("/api/asignaciones")
                        .contentType("application/json")
                        .content("{\"usuarioId\":\"user123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - Puede consultar disponibilidades (GET)")
    void testEmpleado_PuedeConsultarDisponibilidades() throws Exception {
        mockMvc.perform(get("/api/disponibilidades/usuario/keycloak-id-123"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - NO puede crear disponibilidad (POST)")
    void testEmpleado_NoPuedeCrearDisponibilidad() throws Exception {
        mockMvc.perform(post("/api/disponibilidades")
                        .contentType("application/json")
                        .content("{\"usuarioId\":\"user123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - Puede consultar descansos (GET)")
    void testEmpleado_PuedeConsultarDescansos() throws Exception {
        mockMvc.perform(get("/api/descansos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - NO puede crear descanso (POST)")
    void testEmpleado_NoPuedeCrearDescanso() throws Exception {
        mockMvc.perform(post("/api/descansos")
                        .contentType("application/json")
                        .content("{\"asignacionId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - Puede consultar reemplazos (GET)")
    void testEmpleado_PuedeConsultarReemplazos() throws Exception {
        mockMvc.perform(get("/api/reemplazos/pendientes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    @DisplayName("EMPLEADO - NO puede crear reemplazo (POST)")
    void testEmpleado_NoPuedeCrearReemplazo() throws Exception {
        mockMvc.perform(post("/api/reemplazos")
                        .contentType("application/json")
                        .content("{\"asignacionId\":1}"))
                .andExpect(status().isForbidden());
    }

    // ========== TESTS CON ROL ADMIN ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede consultar turnos (GET)")
    void testAdmin_PuedeConsultarTurnos() throws Exception {
        mockMvc.perform(get("/api/turnos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede crear turno (POST)")
    void testAdmin_PuedeCrearTurno() throws Exception {
        mockMvc.perform(post("/api/turnos")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Turno Test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede actualizar turno (PUT)")
    void testAdmin_PuedeActualizarTurno() throws Exception {
        mockMvc.perform(put("/api/turnos/1")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Turno Actualizado\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede eliminar turno (DELETE)")
    void testAdmin_PuedeEliminarTurno() throws Exception {
        mockMvc.perform(delete("/api/turnos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede crear asignación (POST)")
    void testAdmin_PuedeCrearAsignacion() throws Exception {
        mockMvc.perform(post("/api/asignaciones")
                        .contentType("application/json")
                        .content("{\"usuarioId\":\"user123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede cancelar asignación (PATCH)")
    void testAdmin_PuedeCancelarAsignacion() throws Exception {
        mockMvc.perform(patch("/api/asignaciones/1/cancelar"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede crear disponibilidad (POST)")
    void testAdmin_PuedeCrearDisponibilidad() throws Exception {
        mockMvc.perform(post("/api/disponibilidades")
                        .contentType("application/json")
                        .content("{\"usuarioId\":\"user123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede actualizar disponibilidad (PUT)")
    void testAdmin_PuedeActualizarDisponibilidad() throws Exception {
        mockMvc.perform(put("/api/disponibilidades/1")
                        .contentType("application/json")
                        .content("{\"usuarioId\":\"user123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede eliminar disponibilidad (DELETE)")
    void testAdmin_PuedeEliminarDisponibilidad() throws Exception {
        mockMvc.perform(delete("/api/disponibilidades/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede crear descanso (POST)")
    void testAdmin_PuedeCrearDescanso() throws Exception {
        mockMvc.perform(post("/api/descansos")
                        .contentType("application/json")
                        .content("{\"asignacionId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede eliminar descanso (DELETE)")
    void testAdmin_PuedeEliminarDescanso() throws Exception {
        mockMvc.perform(delete("/api/descansos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede crear reemplazo (POST)")
    void testAdmin_PuedeCrearReemplazo() throws Exception {
        mockMvc.perform(post("/api/reemplazos")
                        .contentType("application/json")
                        .content("{\"asignacionId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede aprobar reemplazo (PATCH)")
    void testAdmin_PuedeAprobarReemplazo() throws Exception {
        mockMvc.perform(patch("/api/reemplazos/1/aprobar"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - Puede rechazar reemplazo (PATCH)")
    void testAdmin_PuedeRechazarReemplazo() throws Exception {
        mockMvc.perform(patch("/api/reemplazos/1/rechazar"))
                .andExpect(status().isOk());
    }
}
