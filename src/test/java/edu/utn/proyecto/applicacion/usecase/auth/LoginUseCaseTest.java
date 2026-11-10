package edu.utn.proyecto.applicacion.usecase.auth;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.service.auth.AuthService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
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
class LoginUseCaseTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private LoginUseCase useCase;

    @Test
    void execute_retornaSessionConTodosLosCampos() {
        UUID userId = UUID.randomUUID();
        LoginRequestDTO request = new LoginRequestDTO();
        request.setDni("12345678");
        request.setEmail("juan@test.com");
        SessionUserDTO expected = new SessionUserDTO(userId, "12345678", "juan@test.com", "Juan Pérez", "USUARIO");
        when(authService.login(request)).thenReturn(expected);

        SessionUserDTO result = useCase.execute(request);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getDni()).isEqualTo("12345678");
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
        assertThat(result.getNombre()).isEqualTo("Juan Pérez");
        assertThat(result.getRol()).isEqualTo("USUARIO");
    }

    @Test
    void execute_propagaDomainExceptionDelServicio() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setDni("00000000");
        request.setEmail("fake@test.com");
        DomainException authError = DomainException.of("auth.credenciales.invalidas", HttpStatus.UNAUTHORIZED);
        when(authService.login(request)).thenThrow(authError);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo("auth.credenciales.invalidas"));
    }

    @Test
    void execute_conRolAdministrador_retornaRolCorrecto() {
        LoginRequestDTO request = new LoginRequestDTO();
        SessionUserDTO adminSession = new SessionUserDTO(
                UUID.randomUUID(), "99999999", "admin@test.com", "Admin Sistema", "ADMINISTRADOR");
        when(authService.login(request)).thenReturn(adminSession);

        SessionUserDTO result = useCase.execute(request);

        assertThat(result.getRol()).isEqualTo("ADMINISTRADOR");
        assertThat(result.getEmail()).isEqualTo("admin@test.com");
        assertThat(result.getNombre()).isEqualTo("Admin Sistema");
    }
}
