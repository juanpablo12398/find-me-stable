package edu.utn.proyecto.applicacion.usecase.avistamiento;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.service.avistamiento.AvistamientoQueryService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadAvistamientoGeoUseCaseTest {

    @Mock
    private AvistamientoQueryService service;

    @InjectMocks
    private ReadAvistamientoGeoUseCase useCase;

    @Test
    void paraMapa_retornaListaConDatosEnriquecidos() {
        AvistamientoFrontDTO dto = new AvistamientoFrontDTO();
        dto.setId(UUID.randomUUID());
        dto.setLatitud(-34.603684);
        dto.setLongitud(-58.381555);
        dto.setDesaparecidoNombre("Juan");
        dto.setNivelUrgencia("CRITICO");
        when(service.obtenerParaMapa()).thenReturn(List.of(dto));

        List<AvistamientoFrontDTO> result = useCase.paraMapa();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLatitud()).isEqualTo(-34.603684);
        assertThat(result.get(0).getLongitud()).isEqualTo(-58.381555);
        assertThat(result.get(0).getDesaparecidoNombre()).isEqualTo("Juan");
        assertThat(result.get(0).getNivelUrgencia()).isEqualTo("CRITICO");
    }

    @Test
    void paraMapa_listaVaciaRetornaListaVacia() {
        when(service.obtenerParaMapa()).thenReturn(List.of());

        List<AvistamientoFrontDTO> result = useCase.paraMapa();

        assertThat(result).isEmpty();
    }

    @Test
    void enRadio_pasaParametrosCorrectosAlServicio() {
        double lat = -34.603684;
        double lng = -58.381555;
        double radioKm = 2.5;
        AvistamientoFrontDTO dto = new AvistamientoFrontDTO();
        dto.setId(UUID.randomUUID());
        dto.setLatitud(lat - 0.001);
        dto.setLongitud(lng + 0.001);
        when(service.obtenerEnRadio(lat, lng, radioKm)).thenReturn(List.of(dto));

        List<AvistamientoFrontDTO> result = useCase.enRadio(lat, lng, radioKm);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLatitud()).isEqualTo(lat - 0.001);
    }

    @Test
    void porDesaparecido_retornaSoloAvistamientosDeEseDesaparecido() {
        UUID desaparecidoId = UUID.randomUUID();
        AvistamientoFrontDTO dto = new AvistamientoFrontDTO();
        dto.setDesaparecidoId(desaparecidoId);
        dto.setDesaparecidoNombre("Carlos");
        dto.setDesaparecidoApellido("López");
        when(service.obtenerPorDesaparecidoEnriquecido(desaparecidoId)).thenReturn(List.of(dto));

        List<AvistamientoFrontDTO> result = useCase.porDesaparecido(desaparecidoId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDesaparecidoId()).isEqualTo(desaparecidoId);
        assertThat(result.get(0).getDesaparecidoNombre()).isEqualTo("Carlos");
    }

    @Test
    void enArea_pasaExactamenteLasCoordenadaDelBoundingBox() {
        AvistamientoFrontDTO dto = new AvistamientoFrontDTO();
        dto.setLatitud(-34.60);
        dto.setLongitud(-58.38);
        when(service.obtenerEnAreaEnriquecido(-35.0, -34.0, -59.0, -58.0)).thenReturn(List.of(dto));

        List<AvistamientoFrontDTO> result = useCase.enArea(-35.0, -34.0, -59.0, -58.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLatitud()).isEqualTo(-34.60);
    }

    @Test
    void enPoligono_wktInvalidoPropagaExcepcion() {
        when(service.obtenerEnPoligono("INVALID_WKT"))
                .thenThrow(DomainException.of("avistamiento.poligono.wkt.invalid", HttpStatus.UNPROCESSABLE_ENTITY));

        assertThatThrownBy(() -> useCase.enPoligono("INVALID_WKT"))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo("avistamiento.poligono.wkt.invalid"));
    }
}
