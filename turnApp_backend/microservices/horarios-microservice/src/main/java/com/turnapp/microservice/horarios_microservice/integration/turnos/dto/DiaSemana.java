package com.turnapp.microservice.horarios_microservice.integration.turnos.dto;

import java.time.DayOfWeek;

/**
 * Enum que representa los días de la semana en español.
 * Espejo del DiaSemana del microservicio de turnos.
 * 
 * @author TurnApp Team
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public int getValor() {
        return valor;
    }

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

    public static DiaSemana fromValor(int valor) {
        for (DiaSemana dia : values()) {
            if (dia.valor == valor) {
                return dia;
            }
        }
        
        throw new IllegalArgumentException("Valor de día no válido: " + valor);
    }

    public String getNombreCapitalizado() {
        String nombre = this.name();
        return nombre.charAt(0) + nombre.substring(1).toLowerCase();
    }

    @Override
    public String toString() {
        return getNombreCapitalizado();
    }
}
