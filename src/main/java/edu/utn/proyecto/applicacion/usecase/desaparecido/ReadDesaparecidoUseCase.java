package edu.utn.proyecto.applicacion.usecase.desaparecido;

import edu.utn.proyecto.domain.service.abstraccion.IDesaparecidoService;
import edu.utn.proyecto.domain.service.desaparecido.DesaparecidoService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadDesaparecidoUseCase {
    private final IDesaparecidoService desaparecidoService;

    public ReadDesaparecidoUseCase(DesaparecidoService desaparecidoService) {
        this.desaparecidoService = desaparecidoService;
    }

    public List<DesaparecidoFrontDTO> execute() {
        return desaparecidoService.buscarParaFront(null, null, "urgencia");
    }

    public edu.utn.proyecto.applicacion.dtos.DesaparecidoEstadisticasDTO getEstadisticas() {
        return desaparecidoService.getEstadisticas();
    }

    public List<DesaparecidoFrontDTO> execute(String search, String estado, String sort) {
        return desaparecidoService.buscarParaFront(search, estado, sort);
    }

    public PagedResponseDTO<DesaparecidoFrontDTO> execute(String search, String estado, String sort, int page, int size) {
        return desaparecidoService.buscarParaFrontPaginado(search, estado, sort, page, size);
    }
}
