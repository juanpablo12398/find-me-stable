package edu.utn.proyecto.applicacion.usecase.avistador;
import edu.utn.proyecto.applicacion.dtos.AvistadorResponseDTO;
import edu.utn.proyecto.domain.service.avistador.AvistadorService;
import edu.utn.proyecto.domain.service.abstraccion.IAvistadorService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class CreateAvistadorUseCase {
    private final IAvistadorService service;

    public CreateAvistadorUseCase(AvistadorService service) {
        this.service = service;
    }

    public AvistadorResponseDTO execute(AvistadorRequestDTO dto) {
        return service.registrar(dto);
    }
}