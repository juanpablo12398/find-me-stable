package edu.utn.proyecto.domain.service.abstraccion;
import edu.utn.proyecto.applicacion.dtos.AvistadorResponseDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;

public interface IAvistadorService {
    AvistadorResponseDTO registrar(AvistadorRequestDTO dto);
}
