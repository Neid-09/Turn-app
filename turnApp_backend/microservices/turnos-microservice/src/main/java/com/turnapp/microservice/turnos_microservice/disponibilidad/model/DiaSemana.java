package com.turnapp.microservice.turnos_microservice.disponibilidad.model;

import java.time.DayOfWeek;

/**
 * Enum que representa los días de la semana en español.
 * Proporciona mapeo bidireccional con java.time.DayOfWeek para compatibilidad.
 */
public enum DiaSemana {
    LUNES(DayOfWeek.MONDAY, 1),
    MARTES(DayOfWeek.TUESDAY, 2),
    MIERCOLES(DayOfWeek.WEDNESDAY, 3),
    JUEVES(DayOfWeek.THURSDAY, 4),
    VIERNES(DayOfWeek.FRIDAY, 5),
    SABADO(DayOfWeek.SATURDAY, 6),
    DOMINGO(DayOfWeek.SUNDAY, 7);

    private final DayOfWeek dayOfWeek;
    private final int valor;

    DiaSemana(DayOfWeek dayOfWeek, int valor) {
        this.dayOfWeek = dayOfWeek;
        this.valor = valor;
    }

    /**
     * Obtiene el DayOfWeek de Java correspondiente.
     * @return DayOfWeek equivalente
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Obtiene el valor numérico del día (1 = Lunes, 7 = Domingo).
     * @return valor numérico del día
     */
    public int getValor() {
        return valor;
    }

    /**
     * Convierte un DayOfWeek de Java a DiaSemana en español.
     * @param dayOfWeek el día de la semana de Java
     * @return DiaSemana equivalente
     * @throws IllegalArgumentException si dayOfWeek es null
     */
    public static DiaSemana fromDayOfWeek(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("DayOfWeek no puede ser null");
        }
        
        for (DiaSemana dia : values()) {
            if (dia.dayOfWeek == dayOfWeek) {
                return dia;
            }
        }
        
        throw new IllegalArgumentException("DayOfWeek no válido: " + dayOfWeek);
    }

    /**
     * Convierte un valor numérico (1-7) a DiaSemana.
     * @param valor valor numérico (1 = Lunes, 7 = Domingo)
     * @return DiaSemana correspondiente
     * @throws IllegalArgumentException si el valor no está entre 1 y 7
     */
    public static DiaSemana fromValor(int valor) {
        for (DiaSemana dia : values()) {
            if (dia.valor == valor) {
                return dia;
            }
        }
        
        throw new IllegalArgumentException("Valor de día no válido: " + valor + ". Debe estar entre 1 (Lunes) y 7 (Domingo)");
    }

    /**
     * Obtiene el nombre del día en formato capitalizado (ej: "Lunes").
     * @return nombre del día capitalizado
     */
    public String getNombreCapitalizado() {
        String nombre = this.name();
        return nombre.charAt(0) + nombre.substring(1).toLowerCase();
    }

    @Override
    public String toString() {
        return getNombreCapitalizado();
    }
}
