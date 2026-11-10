package edu.utn.proyecto.domain.service.desaparecido;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.applicacion.mappers.DesaparecidoMapper;
import edu.utn.proyecto.applicacion.validation.desaparecido.DesaparecidoCreatePolicy;
import edu.utn.proyecto.common.normalize.SortNormalizer;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import edu.utn.proyecto.domain.service.helpers.DesaparecidoSort;
import edu.utn.proyecto.domain.service.helpers.ExposicionFilter;
import edu.utn.proyecto.domain.service.helpers.UrgenciaCalculator;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesaparecidoServiceTest {

    @Mock
    private IRepoDeDesaparecidos repo;
    @Mock
    private DesaparecidoMapper mapper;
    @Mock
    private DesaparecidoCreatePolicy createPolicy;
    @Mock
    private UrgenciaCalculator urgenciaCalculator;
    @Mock
    private TextNormalizer txtNorm;
    @Mock
    private SortNormalizer sortNorm;
    @Mock
    private ExposicionFilter exposicionFilter;
    @Mock
    private DesaparecidoSort sortHelper;

    @InjectMocks
    private DesaparecidoService service;

    @Test
    void buscar_debeUsarBusquedaExactaPorDni() {
        Desaparecido domain = new Desaparecido();
        domain.setId(UUID.randomUUID());
        DesaparecidoResponseDTO response = new DesaparecidoResponseDTO();
        response.setId(domain.getId());

        when(txtNorm.normalize("12345678")).thenReturn("12345678");
        when(txtNorm.normalizeUpperOrNull(null)).thenReturn(null);
        when(repo.buscarPorDniExacto("12345678", null)).thenReturn(List.of(domain));
        when(mapper.fromDomainToResponse(domain)).thenReturn(response);

        List<DesaparecidoResponseDTO> result = service.buscar("12345678", null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(domain.getId());
        verify(repo).buscarPorDniExacto("12345678", null);
        verify(repo, never()).buscar(any(), any());
    }

    @Test
    void buscarParaFrontPaginado_conDniPaginaEnMemoria() {
        Desaparecido domainA = new Desaparecido();
        Desaparecido domainB = new Desaparecido();
        DesaparecidoResponseDTO responseA = new DesaparecidoResponseDTO();
        DesaparecidoResponseDTO responseB = new DesaparecidoResponseDTO();

        DesaparecidoFrontDTO frontZoe = new DesaparecidoFrontDTO(
                UUID.randomUUID(), "Zoe", "Zulu", null, null, null,
                "10/01/2026 10:00", "buscando", null, null, "BAJO", 3);
        DesaparecidoFrontDTO frontAna = new DesaparecidoFrontDTO(
                UUID.randomUUID(), "Ana", "Alfa", null, null, null,
                "11/01/2026 10:00", "buscando", null, null, "CRITICO", 0);

        when(txtNorm.normalize("12345678")).thenReturn("12345678");
        when(txtNorm.normalizeUpperOrNull(null)).thenReturn(null);
        when(sortNorm.normalizeSort("urgencia")).thenReturn("urgencia");
        when(repo.buscarPorDniExacto("12345678", null)).thenReturn(List.of(domainA, domainB));
        when(mapper.fromDomainToResponse(domainA)).thenReturn(responseA);
        when(mapper.fromDomainToResponse(domainB)).thenReturn(responseB);
        when(mapper.fromResponseListToFrontList(anyList(), any())).thenReturn(new ArrayList<>(List.of(frontZoe, frontAna)));
        when(sortHelper.getComparator("urgencia")).thenReturn(Comparator.comparing(DesaparecidoFrontDTO::getNombre));
        when(exposicionFilter.filtrarDesaparecidos(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var paged = service.buscarParaFrontPaginado("12345678", null, "urgencia", 0, 1);

        assertThat(paged.getTotalElements()).isEqualTo(2);
        assertThat(paged.getTotalPages()).isEqualTo(2);
        assertThat(paged.getContent()).hasSize(1);
        assertThat(paged.getContent().get(0).getNombre()).isEqualTo("Ana");
        verify(repo, never()).buscarPaginado(any(), any(), any(), anyInt(), anyInt());
        verify(repo, never()).contar(any(), any());
    }

    @Test
    void buscarParaFrontPaginado_sinDniDebeUsarPaginacionSql() {
        Desaparecido domain = new Desaparecido();
        domain.setId(UUID.randomUUID());
        domain.setFechaDesaparicion(LocalDateTime.now().minusDays(2));
        DesaparecidoResponseDTO response = new DesaparecidoResponseDTO();
        response.setId(domain.getId());
        response.setFechaDesaparicion(domain.getFechaDesaparicion());
        DesaparecidoFrontDTO front = new DesaparecidoFrontDTO(
                domain.getId(), "Carlos", "Perez", null, null, null,
                "08/03/2026 10:00", "buscando", null, null, "ALTO", 1);

        when(txtNorm.normalize("carlos")).thenReturn("carlos");
        when(txtNorm.normalizeUpperOrNull("buscando")).thenReturn("BUSCANDO");
        when(sortNorm.normalizeSort("recent")).thenReturn("reciente");
        when(repo.buscarPaginado("carlos", "BUSCANDO", "reciente", 12, 12)).thenReturn(List.of(domain));
        when(mapper.fromDomainToResponse(domain)).thenReturn(response);
        when(urgenciaCalculator.calcular(response.getFechaDesaparicion())).thenReturn(NivelUrgencia.ALTO);
        when(mapper.fromResponseToFront(response, NivelUrgencia.ALTO)).thenReturn(front);
        when(exposicionFilter.filtrarDesaparecidos(anyList())).thenReturn(List.of(front));
        when(repo.contar("carlos", "BUSCANDO")).thenReturn(25L);

        var paged = service.buscarParaFrontPaginado("carlos", "buscando", "recent", 1, 12);

        assertThat(paged.getContent()).hasSize(1);
        assertThat(paged.getTotalElements()).isEqualTo(25);
        assertThat(paged.getTotalPages()).isEqualTo(3);
        assertThat(paged.isLast()).isFalse();
        verify(repo).buscarPaginado("carlos", "BUSCANDO", "reciente", 12, 12);
        verify(repo).contar("carlos", "BUSCANDO");
    }

    @Test
    void buscarParaFrontPaginado_debeAplicarClampDeSize() {
        when(txtNorm.normalize("abc")).thenReturn("abc");
        when(txtNorm.normalizeUpperOrNull(null)).thenReturn(null);
        when(sortNorm.normalizeSort(null)).thenReturn("urgencia");
        when(repo.buscarPaginado("abc", null, "urgencia", 100, 0)).thenReturn(List.of());
        when(exposicionFilter.filtrarDesaparecidos(anyList())).thenReturn(List.of());
        when(repo.contar("abc", null)).thenReturn(0L);

        var paged = service.buscarParaFrontPaginado("abc", null, null, 0, 9999);

        assertThat(paged.getSize()).isEqualTo(100);
        verify(repo).buscarPaginado("abc", null, "urgencia", 100, 0);
    }
}
