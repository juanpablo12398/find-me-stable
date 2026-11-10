package edu.utn.proyecto.applicacion.usecase.avistamiento;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.service.avistamiento.AvistamientoCommandService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAvistamientoUseCaseTest {

    @Mock
    private AvistamientoCommandService service;

    @InjectMocks
    private CreateAvistamientoUseCase useCase;

    @Test
    void execute_retornaResponseConCoordenadasYDesaparecidoId() {
        UUID avistadorId = UUID.randomUUID();
        UUID desaparecidoId = UUID.randomUUID();
        UUID avistamientoId = UUID.randomUUID();

        AvistamientoRequestDTO request = new AvistamientoRequestDTO();
        request.setAvistadorId(avistadorId.toString());
        request.setDesaparecidoId(desaparecidoId.toString());
        request.setLatitud(-34.603684);
        request.setLongitud(-58.381555);

        AvistamientoResponseDTO response = new AvistamientoResponseDTO();
        response.setId(avistamientoId);
        response.setAvistadorId(avistadorId);
        response.setDesaparecidoId(desaparecidoId);
        response.setLatitud(-34.603684);
        response.setLongitud(-58.381555);
        response.setPublico(true);
        when(service.crearAvistamiento(request)).thenReturn(response);

        AvistamientoResponseDTO result = useCase.execute(request);

        assertThat(result.getId()).isEqualTo(avistamientoId);
        assertThat(result.getAvistadorId()).isEqualTo(avistadorId);
        assertThat(result.getDesaparecidoId()).isEqualTo(desaparecidoId);
        assertThat(result.getLatitud()).isEqualTo(-34.603684);
        assertThat(result.getLongitud()).isEqualTo(-58.381555);
        assertThat(result.getPublico()).isTrue();
    }

    @Test
    void execute_propagaDomainExceptionCuandoDesaparecidoNoExiste() {
        AvistamientoRequestDTO request = new AvistamientoRequestDTO();
        DomainException noExiste = DomainException.of("desaparecido.not.found", HttpStatus.NOT_FOUND);
        when(service.crearAvistamiento(request)).thenThrow(noExiste);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo("desaparecido.not.found"));
    }
}
