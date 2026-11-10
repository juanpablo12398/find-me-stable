package edu.utn.proyecto.applicacion.usecase.desaparecido;
import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.common.audit.Auditable;
import edu.utn.proyecto.domain.service.desaparecido.DesaparecidoService;
import edu.utn.proyecto.domain.service.abstraccion.IDesaparecidoService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class CreateDesaparecidoUseCase {
    private final IDesaparecidoService desaparecidoService;

    public CreateDesaparecidoUseCase(DesaparecidoService desaparecidoService) {
        this.desaparecidoService = desaparecidoService;
    }

    @Auditable
    public DesaparecidoResponseDTO execute(DesaparecidoRequestDTO dto) {

        return desaparecidoService.crearDesaparecido(dto);
    }
}
