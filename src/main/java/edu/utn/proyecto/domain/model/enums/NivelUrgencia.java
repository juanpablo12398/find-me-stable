package edu.utn.proyecto.domain.model.enums;

public enum NivelUrgencia {
    CRITICO(0),    // < 48 horas
    ALTO(1),       // 48hs - 7 días
    MEDIO(2),      // 7 días - 1 mes
    BAJO(3);       // > 1 mes

    private final int prioridad;

    NivelUrgencia(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getPrioridad() {
        return prioridad;
    }
}