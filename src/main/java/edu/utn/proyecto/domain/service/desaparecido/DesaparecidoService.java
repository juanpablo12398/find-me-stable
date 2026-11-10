package edu.utn.proyecto.domain.service.desaparecido;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.applicacion.mappers.DesaparecidoMapper;
import edu.utn.proyecto.applicacion.validation.desaparecido.DesaparecidoCreatePolicy;
import edu.utn.proyecto.common.normalize.SortNormalizer;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.service.abstraccion.IDesaparecidoService;
import edu.utn.proyecto.domain.service.helpers.DesaparecidoSort;
import edu.utn.proyecto.domain.service.helpers.ExposicionFilter;
import edu.utn.proyecto.domain.service.helpers.UrgenciaCalculator;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DesaparecidoService implements IDesaparecidoService {

    private final IRepoDeDesaparecidos repo;
    private final DesaparecidoMapper mapper;
    private final Validator<DesaparecidoRequestDTO> createPolicy;
    private final UrgenciaCalculator urgenciaCalculator;
    private final TextNormalizer txtNorm;
    private final SortNormalizer sortNorm;
    private final ExposicionFilter exposicionFilter;
    private final DesaparecidoSort sortHelper;

    public DesaparecidoService(IRepoDeDesaparecidos repo,
                               DesaparecidoMapper mapper,
                               DesaparecidoCreatePolicy createPolicy,
                               UrgenciaCalculator urgenciaCalculator,
                               TextNormalizer txtNorm, SortNormalizer sortNorm,
                               ExposicionFilter exposicionFilter, DesaparecidoSort sortHelper) {
        this.repo = repo;
        this.mapper = mapper;
        this.createPolicy = createPolicy;
        this.urgenciaCalculator = urgenciaCalculator;
        this.txtNorm = txtNorm;
        this.sortNorm = sortNorm;
        this.exposicionFilter = exposicionFilter;
        this.sortHelper = sortHelper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "desaparecidosRaw", allEntries = true)
    public DesaparecidoResponseDTO crearDesaparecido(DesaparecidoRequestDTO dto) {
        mapper.normalizeRequestInPlace(dto);
        createPolicy.validate(dto);
        var domain = mapper.fromRequestToDomain(dto);
        var saved = repo.save(domain);
        return mapper.fromDomainToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesaparecidoResponseDTO> obtenerDesaparecidos() {
        return repo.getDesaparecidos().stream()
                .map(mapper::fromDomainToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "desaparecidosRaw", key = "(#search == null ? '' : #search) + '|' + (#estado == null ? '' : #estado)")
    public List<DesaparecidoResponseDTO> buscar(String search, String estado) {
        String searchParam = txtNorm.normalize(search);
        String estadoParam = txtNorm.normalizeUpperOrNull(estado);

        if (isDniExactSearch(searchParam)) {
            return repo.buscarPorDniExacto(searchParam, estadoParam).stream()
                    .map(mapper::fromDomainToResponse)
                    .toList();
        }
        return repo.buscar(searchParam, estadoParam).stream()
                .map(mapper::fromDomainToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public edu.utn.proyecto.applicacion.dtos.DesaparecidoEstadisticasDTO getEstadisticas() {
        long total = repo.contar("", null);
        long buscando = repo.contar("", "BUSCANDO");
        long encontrado = repo.contar("", "ENCONTRADO");
        return new edu.utn.proyecto.applicacion.dtos.DesaparecidoEstadisticasDTO(total, buscando, encontrado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesaparecidoFrontDTO> buscarParaFront(String search, String estado, String sort) {
        List<DesaparecidoResponseDTO> lista = buscar(search, estado);
        List<DesaparecidoFrontDTO> resultado = mapper.fromResponseListToFrontList(
                lista, dto -> urgenciaCalculator.calcular(dto.getFechaDesaparicion()));
        resultado.sort(sortHelper.getComparator(sort));
        return exposicionFilter.filtrarDesaparecidos(resultado);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<DesaparecidoFrontDTO> buscarParaFrontPaginado(
        String search, String estado, String sort, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = clampSize(size);
        String searchParam = txtNorm.normalize(search);
        String estadoParam = txtNorm.normalizeUpperOrNull(estado);
        String sortKey = sortNorm.normalizeSort(sort);
        if (isDniExactSearch(searchParam)) {
            return paginarEnMemoria(buscarParaFront(searchParam, estadoParam, sortKey), safePage, safeSize);
        }
        int offset = safePage * safeSize;
        List<DesaparecidoFrontDTO> content = toFrontFiltered(
            repo.buscarPaginado(searchParam, estadoParam, sortKey, safeSize, offset));
        long totalElements = repo.contar(searchParam, estadoParam);
        return PagedResponseDTO.of(content, safePage, safeSize, totalElements);
    }

    @Override
    public UrgenciaCalculator getUrgenciaCalculator() {
        return urgenciaCalculator;
    }


    private List<DesaparecidoFrontDTO> toFrontFiltered(List<Desaparecido> dominios) {
        List<DesaparecidoFrontDTO> frontList = dominios.stream()
            .map(mapper::fromDomainToResponse)
            .map(dto -> mapper.fromResponseToFront(dto, urgenciaCalculator.calcular(dto.getFechaDesaparicion())))
            .toList();
        return exposicionFilter.filtrarDesaparecidos(frontList);
    }

    private int clampSize(int size) {
        return Math.max(1, Math.min(size, 100));
    }

    private PagedResponseDTO<DesaparecidoFrontDTO> paginarEnMemoria(
        List<DesaparecidoFrontDTO> all, int page, int size) {
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return PagedResponseDTO.of(all.subList(from, to), page, size, all.size());
    }

    private boolean isDniExactSearch(String searchParam) {
        return searchParam.matches("\\d{7,8}");
    }

}
