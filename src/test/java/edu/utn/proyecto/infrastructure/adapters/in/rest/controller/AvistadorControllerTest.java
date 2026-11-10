package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.dtos.AvistadorResponseDTO;
import edu.utn.proyecto.applicacion.usecase.avistador.CreateAvistadorUseCase;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.security.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AvistadorControllerTest {

    @Mock
    private CreateAvistadorUseCase createAvistadorUseCase;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AvistadorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set mock request for ServletUriComponentsBuilder
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void registrar_ok_createsAvistadorAndSetsCookie() {
        AvistadorRequestDTO requestDTO = new AvistadorRequestDTO();
        AvistadorResponseDTO responseDTO = new AvistadorResponseDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setDni("123");
        responseDTO.setEmail("test@test.com");
        responseDTO.setNombre("Name");
        responseDTO.setRol(Rol.USUARIO.name());

        when(createAvistadorUseCase.execute(requestDTO)).thenReturn(responseDTO);
        when(tokenService.generate(any(), any(), any(), any(), any())).thenReturn("mocked_jwt");

        HttpServletResponse response = mock(HttpServletResponse.class);

        ResponseEntity<AvistadorResponseDTO> result = controller.registrar(requestDTO, response);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
        verify(tokenService).writeCookie(response, "mocked_jwt");
        assertNotNull(result.getHeaders().getLocation());
    }
}
