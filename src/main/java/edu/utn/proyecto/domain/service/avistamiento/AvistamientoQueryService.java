package edu.utn.proyecto.domain.service.avistamiento;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.applicacion.mappers.AvistamientoMapper;
import edu.utn.proyecto.avistamiento.exception.AvistamientoError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.concreta.avistamiento.rules.WktPolygonFormatRule;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.service.abstraccion.IAvistamientoQueryService;
import edu.utn.proyecto.domain.service.helpers.ExposicionFilter;
import edu.utn.proyecto.domain.service.helpers.UrgenciaCalculator;
import edu.utn.proyecto.domain.service.helpers.VisibilidadResolver;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistamientos;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AvistamientoQueryService implements IAvistamientoQueryService {

    private final IRepoDeAvistamientos repoAvistamientos;
    private final IRepoDeAvistadores repoAvistadores;
    private final IRepoDeDesaparecidos repoDesaparecidos;
    private final AvistamientoMapper mapper;
    private final UrgenciaCalculator urgenciaCalculator;
    private final VisibilidadResolver visibilidadResolver;
    private final ExposicionFilter exposicionFilter;
    private final WktPolygonFormatRule wktPolygonFormatRule;

    public AvistamientoQueryService(
            IRepoDeAvistamientos repo,
            IRepoDeAvistadores repoAvistadores,
            IRepoDeDesaparecidos repoDesaparecidos,
            AvistamientoMapper mapper,
            UrgenciaCalculator urgenciaCalculator,
            VisibilidadResolver visibilidadResolver,
            ExposicionFilter exposicionFilter,
            WktPolygonFormatRule wktPolygonFormatRule) {
        this.repoAvistamientos = repo;
        this.repoAvistadores = repoAvistadores;
        this.repoDesaparecidos = repoDesaparecidos;
        this.mapper = mapper;
        this.urgenciaCalculator = urgenciaCalculator;
        this.visibilidadResolver = visibilidadResolver;
        this.exposicionFilter = exposicionFilter;
        this.wktPolygonFormatRule = wktPolygonFormatRule;
    }

    @Transactional(readOnly = true)
    public List<AvistamientoResponseDTO> obtenerAvistamientosPublicos() {
        return mapper.fromDomainListToResponseList(repoAvistamientos.findPublicos());
    }

    @Override
    @Transactional(readOnly = true)
    public edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO<AvistamientoResponseDTO> obtenerAvistamientosPublicosPaginado(int page, int size) {
        org.springframework.data.domain.Page<edu.utn.proyecto.domain.model.concreta.Avistamiento> reqPage = repoAvistamientos.findPublicosPaginado(
            org.springframework.data.domain.PageRequest.of(page, size)
        );
        List<AvistamientoResponseDTO> content = mapper.fromDomainListToResponseList(reqPage.getContent());
        return edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO.of(content, page, size, reqPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<AvistamientoResponseDTO> obtenerAvistamientosRecientes(int dias) {
        LocalDateTime desde = LocalDateTime.now().minusDays(dias);
        return mapper.fromDomainListToResponseList(repoAvistamientos.findRecientes(desde));
    }

    @Transactional(readOnly = true)
    public List<AvistamientoResponseDTO> obtenerPorDesaparecido(UUID desaparecidoId) {
        return mapper.fromDomainListToResponseList(repoAvistamientos.findByDesaparecidoId(desaparecidoId));
    }

    @Transactional(readOnly = true)
    public List<AvistamientoResponseDTO> obtenerEnArea(
            Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return mapper.fromDomainListToResponseList(repoAvistamientos.findInBounds(latMin, latMax, lngMin, lngMax));
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerParaMapa() {
        return pipeline(
                visibilidadResolver.resolver(
                        repoAvistamientos::findAll,
                        repoAvistamientos::findVisiblesParaUsuario,
                        repoAvistamientos::findPublicos
                )
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerPorDesaparecidoEnriquecido(UUID desaparecidoId) {
        return pipeline(
                visibilidadResolver.filtrarEnMemoria(
                        repoAvistamientos.findByDesaparecidoId(desaparecidoId)
                )
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerUltimaUbicacion(UUID desaparecidoId) {
        return pipeline(
                visibilidadResolver.filtrarEnMemoria(
                        repoAvistamientos.findUltimaUbicacion(desaparecidoId)
                )
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerEnAreaEnriquecido(
            Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return pipeline(
                visibilidadResolver.resolver(
                        () -> repoAvistamientos.findInBoundsAll(latMin, latMax, lngMin, lngMax),
                        uid -> repoAvistamientos.findInBoundsVisibles(latMin, latMax, lngMin, lngMax, uid),
                        () -> repoAvistamientos.findInBounds(latMin, latMax, lngMin, lngMax)
                )
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerRecientesEnriquecido(int dias) {
        LocalDateTime desde = LocalDateTime.now().minusDays(dias);
        return pipeline(
                visibilidadResolver.filtrarEnMemoria(repoAvistamientos.findRecientes(desde))
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerEnRadio(Double lat, Double lng, Double radioKm) {
        return pipeline(
                visibilidadResolver.resolver(
                        () -> repoAvistamientos.findWithinRadiusAll(lat, lng, radioKm),
                        uid -> repoAvistamientos.findWithinRadiusVisibles(lat, lng, radioKm, uid),
                        () -> repoAvistamientos.findWithinRadius(lat, lng, radioKm)
                )
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerEnPoligono(String polygonWKT) {
        wktPolygonFormatRule.check(polygonWKT);
        return pipeline(
                visibilidadResolver.resolver(
                        () -> repoAvistamientos.findInPolygonAll(polygonWKT),
                        uid -> repoAvistamientos.findInPolygonVisibles(polygonWKT, uid),
                        () -> repoAvistamientos.findInPolygon(polygonWKT)
                )
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerMasCercanos(Double lat, Double lng, Integer limite) {
        return pipeline(
                visibilidadResolver.filtrarEnMemoria(repoAvistamientos.findNearestN(lat, lng, limite))
        );
    }

    @Transactional(readOnly = true)
    public List<AvistamientoFrontDTO> obtenerPorDesaparecidoEnRadio(
            UUID desaparecidoId, Double lat, Double lng, Double radioKm) {
        return pipeline(
                visibilidadResolver.filtrarEnMemoria(
                        repoAvistamientos.findByDesaparecidoWithinRadius(desaparecidoId, lat, lng, radioKm)
                )
        );
    }

    @Transactional(readOnly = true)
    public Long contarEnArea(Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return repoAvistamientos.countInBounds(latMin, latMax, lngMin, lngMax);
    }

    private List<AvistamientoFrontDTO> pipeline(List<Avistamiento> avistamientos) {
        return exposicionFilter.filtrarAvistamientos(enrichAvistamientos(avistamientos));
    }

    private List<AvistamientoFrontDTO> enrichAvistamientos(List<Avistamiento> avistamientos) {
        Set<UUID> desaparecidoIds = avistamientos.stream()
                .map(Avistamiento::getDesaparecidoId)
                .collect(Collectors.toSet());
        Set<UUID> avistadorIds = avistamientos.stream()
                .map(Avistamiento::getAvistadorId)
                .collect(Collectors.toSet());
        Map<UUID, Desaparecido> desaparecidoMap = repoDesaparecidos.findByIds(desaparecidoIds)
                .stream()
                .collect(Collectors.toMap(Desaparecido::getId, Function.identity()));
        Map<UUID, Avistador> avistadorMap = repoAvistadores.findByIds(avistadorIds)
                .stream()
                .collect(Collectors.toMap(Avistador::getId, Function.identity()));
        return avistamientos.stream().map(a -> {
            var desaparecido = desaparecidoMap.get(a.getDesaparecidoId());
            if (desaparecido == null) {
                throw DomainException.of(
                        AvistamientoError.DESAPARECIDO_NOT_FOUND.key,
                        AvistamientoError.DESAPARECIDO_NOT_FOUND.status);
            }
            var avistador = avistadorMap.get(a.getAvistadorId());
            if (avistador == null) {
                throw DomainException.of(
                        AvistamientoError.AVISTADOR_NOT_FOUND.key,
                        AvistamientoError.AVISTADOR_NOT_FOUND.status);
            }
            AvistamientoFrontDTO front = mapper.fromDomainToFrontBasic(a);
            String nivelUrgencia = urgenciaCalculator.calcular(desaparecido.getFechaDesaparicion()).name();
            mapper.enrichFrontDTO(front, desaparecido, avistador, nivelUrgencia);
            return front;
        }).collect(Collectors.toList());
    }
}
