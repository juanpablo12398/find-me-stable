package edu.utn.proyecto.domain.service.abstraccion;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;

public interface IAvistamientoCommandService {
    AvistamientoResponseDTO crearAvistamiento(AvistamientoRequestDTO dto);
}
