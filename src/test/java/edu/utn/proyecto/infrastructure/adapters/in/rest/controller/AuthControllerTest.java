package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.usecase.auth.LoginUseCase;
import edu.utn.proyecto.applicacion.usecase.auth.GetCurrentUserUseCase;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import edu.utn.proyecto.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doLogin_ok() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setDni("12345678");
        req.setEmail("test@test.com");

        SessionUserDTO sessionUserDTO = new SessionUserDTO(
                UUID.randomUUID(), "12345678", "test@test.com", "Test", Rol.USUARIO.name());

        when(loginUseCase.execute(req)).thenReturn(sessionUserDTO);
        when(tokenService.generate(any(), any(), any(), any(), any())).thenReturn("mocked_jwt");

        HttpServletResponse response = mock(HttpServletResponse.class);
        ResponseEntity<SessionUserDTO> res = controller.doLogin(req, response);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(sessionUserDTO, res.getBody());
        verify(tokenService).writeCookie(response, "mocked_jwt");
    }

    @Test
    void me_ok() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        SessionUserDTO sessionUserDTO = new SessionUserDTO(
                UUID.randomUUID(), "12345678", "test@test.com", "Test", Rol.USUARIO.name());

        when(getCurrentUserUseCase.execute(request)).thenReturn(sessionUserDTO);

        ResponseEntity<SessionUserDTO> res = controller.me(request);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(sessionUserDTO, res.getBody());
    }

    @Test
    void logout_ok() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        ResponseEntity<Void> res = controller.logout(response);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(tokenService).clearCookie(response);
    }
}
