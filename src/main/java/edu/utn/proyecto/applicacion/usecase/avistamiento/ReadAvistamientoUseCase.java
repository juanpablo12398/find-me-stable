package edu.utn.proyecto.applicacion.usecase.avistamiento;
import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.domain.service.avistamiento.AvistamientoQueryService;
import edu.utn.proyecto.domain.service.abstraccion.IAvistamientoQueryService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ReadAvistamientoUseCase {

    private final IAvistamientoQueryService service;

    public ReadAvistamientoUseCase(AvistamientoQueryService service) {
        this.service = service;
    }

    public List<AvistamientoResponseDTO> obtenerTodos() {
        return service.obtenerAvistamientosPublicos();
    }

    public edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO<AvistamientoResponseDTO> obtenerTodosPaginado(int page, int size) {
        return service.obtenerAvistamientosPublicosPaginado(page, size);
    }

    public List<AvistamientoResponseDTO> obtenerRecientes(int dias) {
        return service.obtenerAvistamientosRecientes(dias);
    }

    public List<AvistamientoResponseDTO> obtenerPorDesaparecido(UUID desaparecidoId) {
        return service.obtenerPorDesaparecido(desaparecidoId);
    }

    public List<AvistamientoResponseDTO> obtenerEnArea(
            Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return service.obtenerEnArea(latMin, latMax, lngMin, lngMax);
    }
}
