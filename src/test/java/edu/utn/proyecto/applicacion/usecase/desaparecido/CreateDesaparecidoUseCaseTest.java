package edu.utn.proyecto.applicacion.usecase.desaparecido;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.service.desaparecido.DesaparecidoService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateDesaparecidoUseCaseTest {

    @Mock
    private DesaparecidoService desaparecidoService;

    @InjectMocks
    private CreateDesaparecidoUseCase useCase;

    @Test
    void execute_retornaResponseConTodosLosCampos() {
        UUID id = UUID.randomUUID();
        LocalDateTime fechaDesaparicion = LocalDateTime.of(2025, 1, 10, 8, 0);
        DesaparecidoRequestDTO request = new DesaparecidoRequestDTO(
                "Juan", "Pérez", 35, "12345678", null, "Última vez visto en el parque",
                fechaDesaparicion, "contacto@familiar.com", "Parque Rivadavia");

        DesaparecidoResponseDTO response = new DesaparecidoResponseDTO(
                id, "Juan", "Pérez", "12345678", fechaDesaparicion,
                "Última vez visto en el parque", null, "BUSCANDO",
                "contacto@familiar.com", "Parque Rivadavia");
        when(desaparecidoService.crearDesaparecido(request)).thenReturn(response);

        DesaparecidoResponseDTO result = useCase.execute(request);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNombre()).isEqualTo("Juan");
        assertThat(result.getApellido()).isEqualTo("Pérez");
        assertThat(result.getDni()).isEqualTo("12345678");
        assertThat(result.getEstado()).isEqualTo("BUSCANDO");
        assertThat(result.getContactoFamiliar()).isEqualTo("contacto@familiar.com");
        assertThat(result.getUltimaUbicacion()).isEqualTo("Parque Rivadavia");
    }

    @Test
    void execute_propagaDomainExceptionCuandoDniYaExisteEnElSistema() {
        DesaparecidoRequestDTO request = new DesaparecidoRequestDTO(
                "Juan", "Pérez", 35, "12345678", null, "desc",
                LocalDateTime.now(), null, null);
        DomainException dniDuplicado = DomainException.of("desaparecido.dni.duplicado", HttpStatus.CONFLICT);
        when(desaparecidoService.crearDesaparecido(request)).thenThrow(dniDuplicado);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo("desaparecido.dni.duplicado"));
    }
}
