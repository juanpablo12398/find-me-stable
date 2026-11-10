package edu.utn.proyecto.domain.service.avistamiento;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.applicacion.mappers.AvistamientoMapper;
import edu.utn.proyecto.avistamiento.exception.AvistamientoError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.concreta.avistamiento.rules.WktPolygonFormatRule;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import edu.utn.proyecto.domain.service.helpers.ExposicionFilter;
import edu.utn.proyecto.domain.service.helpers.UrgenciaCalculator;
import edu.utn.proyecto.domain.service.helpers.VisibilidadResolver;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistamientos;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvistamientoQueryServiceTest {

    @Mock
    private IRepoDeAvistamientos repoAvistamientos;
    @Mock
    private IRepoDeAvistadores repoAvistadores;
    @Mock
    private IRepoDeDesaparecidos repoDesaparecidos;
    @Mock
    private AvistamientoMapper mapper;
    @Mock
    private UrgenciaCalculator urgenciaCalculator;
    @Mock
    private VisibilidadResolver visibilidadResolver;
    @Mock
    private ExposicionFilter exposicionFilter;
    @Mock
    private WktPolygonFormatRule wktPolygonFormatRule;

    @InjectMocks
    private AvistamientoQueryService service;

    @Test
    void obtenerEnPoligono_debeFallarConWktInvalido() {
        doThrow(DomainException.of("avistamiento.poligono.wkt.invalid", HttpStatus.UNPROCESSABLE_ENTITY))
                .when(wktPolygonFormatRule).check("POINT(1 1)");

        assertThatThrownBy(() -> service.obtenerEnPoligono("POINT(1 1)"))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo("avistamiento.poligono.wkt.invalid"));

        verify(wktPolygonFormatRule).check("POINT(1 1)");
        verifyNoInteractions(visibilidadResolver);
    }

    @Test
    void obtenerRecientesEnriquecido_debeResolverConBatchYSinNMasUno() {
        UUID avistadorId1 = UUID.randomUUID();
        UUID avistadorId2 = UUID.randomUUID();
        UUID desaparecidoId1 = UUID.randomUUID();
        UUID desaparecidoId2 = UUID.randomUUID();

        Avistamiento a1 = new Avistamiento();
        a1.setId(UUID.randomUUID());
        a1.setAvistadorId(avistadorId1);
        a1.setDesaparecidoId(desaparecidoId1);
        Avistamiento a2 = new Avistamiento();
        a2.setId(UUID.randomUUID());
        a2.setAvistadorId(avistadorId2);
        a2.setDesaparecidoId(desaparecidoId2);

        Desaparecido d1 = new Desaparecido();
        d1.setId(desaparecidoId1);
        d1.setFechaDesaparicion(LocalDateTime.now().minusHours(6));
        Desaparecido d2 = new Desaparecido();
        d2.setId(desaparecidoId2);
        d2.setFechaDesaparicion(LocalDateTime.now().minusHours(12));

        Avistador av1 = new Avistador();
        av1.setId(avistadorId1);
        Avistador av2 = new Avistador();
        av2.setId(avistadorId2);

        AvistamientoFrontDTO f1 = new AvistamientoFrontDTO();
        AvistamientoFrontDTO f2 = new AvistamientoFrontDTO();

        when(repoAvistamientos.findRecientes(any(LocalDateTime.class))).thenReturn(List.of(a1, a2));
        when(visibilidadResolver.filtrarEnMemoria(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repoDesaparecidos.findByIds(any())).thenReturn(List.of(d1, d2));
        when(repoAvistadores.findByIds(any())).thenReturn(List.of(av1, av2));
        when(mapper.fromDomainToFrontBasic(a1)).thenReturn(f1);
        when(mapper.fromDomainToFrontBasic(a2)).thenReturn(f2);
        when(urgenciaCalculator.calcular(any(LocalDateTime.class))).thenReturn(NivelUrgencia.ALTO);
        when(exposicionFilter.filtrarAvistamientos(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<AvistamientoFrontDTO> result = service.obtenerRecientesEnriquecido(7);

        assertThat(result).hasSize(2);
        verify(repoDesaparecidos).findByIds(any());
        verify(repoAvistadores).findByIds(any());
        verify(repoDesaparecidos, never()).findById(any());
        verify(repoAvistadores, never()).findById(any());
        verify(mapper).enrichFrontDTO(f1, d1, av1, "ALTO");
        verify(mapper).enrichFrontDTO(f2, d2, av2, "ALTO");
    }

    @Test
    void obtenerRecientesEnriquecido_debeFallarSiFaltaDesaparecido() {
        UUID avistadorId = UUID.randomUUID();
        UUID desaparecidoId = UUID.randomUUID();

        Avistamiento a1 = new Avistamiento();
        a1.setAvistadorId(avistadorId);
        a1.setDesaparecidoId(desaparecidoId);

        Avistador av1 = new Avistador();
        av1.setId(avistadorId);

        when(repoAvistamientos.findRecientes(any(LocalDateTime.class))).thenReturn(List.of(a1));
        when(visibilidadResolver.filtrarEnMemoria(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repoDesaparecidos.findByIds(any())).thenReturn(List.of());
        when(repoAvistadores.findByIds(any())).thenReturn(List.of(av1));

        assertThatThrownBy(() -> service.obtenerRecientesEnriquecido(7))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo(AvistamientoError.DESAPARECIDO_NOT_FOUND.key));
    }

    @Test
    void obtenerRecientesEnriquecido_debeFallarSiFaltaAvistador() {
        UUID avistadorId = UUID.randomUUID();
        UUID desaparecidoId = UUID.randomUUID();

        Avistamiento a1 = new Avistamiento();
        a1.setAvistadorId(avistadorId);
        a1.setDesaparecidoId(desaparecidoId);

        Desaparecido d1 = new Desaparecido();
        d1.setId(desaparecidoId);
        d1.setFechaDesaparicion(LocalDateTime.now().minusHours(8));

        when(repoAvistamientos.findRecientes(any(LocalDateTime.class))).thenReturn(List.of(a1));
        when(visibilidadResolver.filtrarEnMemoria(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repoDesaparecidos.findByIds(any())).thenReturn(List.of(d1));
        when(repoAvistadores.findByIds(any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.obtenerRecientesEnriquecido(7))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo(AvistamientoError.AVISTADOR_NOT_FOUND.key));
    }
}
