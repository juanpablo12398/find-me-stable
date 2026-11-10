package edu.utn.proyecto.applicacion.usecase.avistador;

import edu.utn.proyecto.applicacion.dtos.AvistadorResponseDTO;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.service.avistador.AvistadorService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
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
class CreateAvistadorUseCaseTest {

    @Mock
    private AvistadorService service;

    @InjectMocks
    private CreateAvistadorUseCase useCase;

    @Test
    void execute_retornaResponseConTodosLosCamposDelAvistador() {
        AvistadorRequestDTO request = new AvistadorRequestDTO();
        AvistadorResponseDTO response = new AvistadorResponseDTO();
        UUID id = UUID.randomUUID();
        response.setId(id);
        response.setDni("12345678");
        response.setNombre("María");
        response.setApellido("González");
        response.setEmail("maria@test.com");
        response.setRol("USUARIO");
        when(service.registrar(request)).thenReturn(response);

        AvistadorResponseDTO result = useCase.execute(request);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getDni()).isEqualTo("12345678");
        assertThat(result.getNombre()).isEqualTo("María");
        assertThat(result.getApellido()).isEqualTo("González");
        assertThat(result.getEmail()).isEqualTo("maria@test.com");
        assertThat(result.getRol()).isEqualTo("USUARIO");
    }

    @Test
    void execute_propagaDomainExceptionCuandoDniEstaDuplicado() {
        AvistadorRequestDTO request = new AvistadorRequestDTO();
        DomainException dniDuplicado = DomainException.of("avistador.dni.duplicado", HttpStatus.CONFLICT);
        when(service.registrar(request)).thenThrow(dniDuplicado);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo("avistador.dni.duplicado"));
    }
}
