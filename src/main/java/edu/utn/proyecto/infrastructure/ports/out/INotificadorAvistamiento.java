package edu.utn.proyecto.infrastructure.ports.out;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;

public interface INotificadorAvistamiento {
    void notificarNuevoAvistamiento(AvistamientoResponseDTO avistamiento);
}
