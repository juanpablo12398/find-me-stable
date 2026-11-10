package edu.utn.proyecto.applicacion.usecase.avistamiento;
import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.common.audit.Auditable;
import edu.utn.proyecto.domain.service.avistamiento.AvistamientoCommandService;
import edu.utn.proyecto.domain.service.abstraccion.IAvistamientoCommandService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class CreateAvistamientoUseCase {
    private final IAvistamientoCommandService service;

    public CreateAvistamientoUseCase(AvistamientoCommandService service) {
        this.service = service;
    }

    @Auditable
    public AvistamientoResponseDTO execute(AvistamientoRequestDTO dto) {
        return service.crearAvistamiento(dto);
    }
}
