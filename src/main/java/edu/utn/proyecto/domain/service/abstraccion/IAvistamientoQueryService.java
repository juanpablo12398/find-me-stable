package edu.utn.proyecto.domain.service.abstraccion;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;

import java.util.List;
import java.util.UUID;

public interface IAvistamientoQueryService {
    List<AvistamientoResponseDTO> obtenerAvistamientosPublicos();
    edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO<AvistamientoResponseDTO> obtenerAvistamientosPublicosPaginado(int page, int size);
    List<AvistamientoResponseDTO> obtenerAvistamientosRecientes(int dias);
    List<AvistamientoResponseDTO> obtenerPorDesaparecido(UUID desaparecidoId);
    List<AvistamientoResponseDTO> obtenerEnArea(Double latMin, Double latMax, Double lngMin, Double lngMax);
    List<AvistamientoFrontDTO> obtenerParaMapa();
    List<AvistamientoFrontDTO> obtenerPorDesaparecidoEnriquecido(UUID desaparecidoId);
    List<AvistamientoFrontDTO> obtenerUltimaUbicacion(UUID desaparecidoId);
    List<AvistamientoFrontDTO> obtenerEnAreaEnriquecido(Double latMin, Double latMax, Double lngMin, Double lngMax);
    List<AvistamientoFrontDTO> obtenerRecientesEnriquecido(int dias);
    List<AvistamientoFrontDTO> obtenerEnRadio(Double lat, Double lng, Double radioKm);
    List<AvistamientoFrontDTO> obtenerEnPoligono(String polygonWKT);
    List<AvistamientoFrontDTO> obtenerMasCercanos(Double lat, Double lng, Integer limite);
    List<AvistamientoFrontDTO> obtenerPorDesaparecidoEnRadio(UUID desaparecidoId, Double lat, Double lng, Double radioKm);
    Long contarEnArea(Double latMin, Double latMax, Double lngMin, Double lngMax);
}
