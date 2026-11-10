package edu.utn.proyecto.domain.service.abstraccion;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.domain.service.helpers.UrgenciaCalculator;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO;

import java.util.List;

public interface IDesaparecidoService {
    DesaparecidoResponseDTO crearDesaparecido(DesaparecidoRequestDTO dto);
    List<DesaparecidoResponseDTO> obtenerDesaparecidos();
    List<DesaparecidoResponseDTO> buscar(String search, String estado);
    edu.utn.proyecto.applicacion.dtos.DesaparecidoEstadisticasDTO getEstadisticas();
    List<DesaparecidoFrontDTO> buscarParaFront(String search, String estado, String sort);
    PagedResponseDTO<DesaparecidoFrontDTO> buscarParaFrontPaginado(String search, String estado, String sort, int page, int size);
    UrgenciaCalculator getUrgenciaCalculator();
}
