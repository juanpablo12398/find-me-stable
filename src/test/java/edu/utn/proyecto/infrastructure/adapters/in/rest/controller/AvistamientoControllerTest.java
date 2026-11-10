package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.applicacion.usecase.avistamiento.CreateAvistamientoUseCase;
import edu.utn.proyecto.applicacion.usecase.avistamiento.ReadAvistamientoGeoUseCase;
import edu.utn.proyecto.applicacion.usecase.avistamiento.ReadAvistamientoUseCase;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AvistamientoControllerTest {

    @Mock
    private CreateAvistamientoUseCase createUseCase;
    @Mock
    private ReadAvistamientoUseCase readUseCase;
    @Mock
    private ReadAvistamientoGeoUseCase readGeoUseCase;

    @InjectMocks
    private AvistamientoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void crear_ok() {
        AvistamientoRequestDTO req = new AvistamientoRequestDTO();
        AvistamientoResponseDTO resDto = new AvistamientoResponseDTO();
        resDto.setId(UUID.randomUUID());
        when(createUseCase.execute(req)).thenReturn(resDto);

        ResponseEntity<AvistamientoResponseDTO> res = controller.crear(req);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals(resDto, res.getBody());
    }

    @Test
    void obtenerParaMapa_ok() {
        List<AvistamientoFrontDTO> list = List.of(org.mockito.Mockito.mock(AvistamientoFrontDTO.class));
        when(readGeoUseCase.paraMapa()).thenReturn(list);

        ResponseEntity<List<AvistamientoFrontDTO>> res = controller.obtenerParaMapa();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(list, res.getBody());
    }

    @Test
    void obtenerEnArea_ok() {
        List<AvistamientoFrontDTO> list = List.of(org.mockito.Mockito.mock(AvistamientoFrontDTO.class));
        when(readGeoUseCase.enArea(1.0, 2.0, 1.0, 2.0)).thenReturn(list);

        ResponseEntity<List<AvistamientoFrontDTO>> res = controller.obtenerEnArea(1.0, 2.0, 1.0, 2.0);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(list, res.getBody());
    }
}
