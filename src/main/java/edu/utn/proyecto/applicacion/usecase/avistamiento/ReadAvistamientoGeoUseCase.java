package edu.utn.proyecto.applicacion.usecase.avistamiento;
import edu.utn.proyecto.domain.service.avistamiento.AvistamientoQueryService;
import edu.utn.proyecto.domain.service.abstraccion.IAvistamientoQueryService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ReadAvistamientoGeoUseCase {

    private final IAvistamientoQueryService service;

    public ReadAvistamientoGeoUseCase(AvistamientoQueryService service) {
        this.service = service;
    }

    public List<AvistamientoFrontDTO> paraMapa() {
        return service.obtenerParaMapa();
    }

    public List<AvistamientoFrontDTO> porDesaparecido(UUID desaparecidoId) {
        return service.obtenerPorDesaparecidoEnriquecido(desaparecidoId);
    }

    public List<AvistamientoFrontDTO> ultimaUbicacion(UUID desaparecidoId) {
        return service.obtenerUltimaUbicacion(desaparecidoId);
    }

    public List<AvistamientoFrontDTO> enArea(Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return service.obtenerEnAreaEnriquecido(latMin, latMax, lngMin, lngMax);
    }

    public List<AvistamientoFrontDTO> recientes(int dias) {
        return service.obtenerRecientesEnriquecido(dias);
    }

    public List<AvistamientoFrontDTO> enRadio(Double lat, Double lng, Double radioKm) {
        return service.obtenerEnRadio(lat, lng, radioKm);
    }

    public List<AvistamientoFrontDTO> enPoligono(String polygonWKT) {
        return service.obtenerEnPoligono(polygonWKT);
    }

    public List<AvistamientoFrontDTO> masCercanos(Double lat, Double lng, Integer limite) {
        return service.obtenerMasCercanos(lat, lng, limite);
    }

    public List<AvistamientoFrontDTO> porDesaparecidoEnRadio(UUID desaparecidoId, Double lat, Double lng, Double radioKm) {
        return service.obtenerPorDesaparecidoEnRadio(desaparecidoId, lat, lng, radioKm);
    }

    public Long contarEnArea(Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return service.contarEnArea(latMin, latMax, lngMin, lngMax);
    }
}
