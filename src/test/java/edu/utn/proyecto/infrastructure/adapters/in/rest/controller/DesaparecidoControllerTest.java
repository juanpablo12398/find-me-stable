package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.applicacion.usecase.desaparecido.CreateDesaparecidoUseCase;
import edu.utn.proyecto.applicacion.usecase.desaparecido.ReadDesaparecidoUseCase;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DesaparecidoControllerTest {

    @Mock
    private CreateDesaparecidoUseCase crearDesaparecidoUseCase;

    @Mock
    private ReadDesaparecidoUseCase readDesaparecidoUseCase;

    @InjectMocks
    private DesaparecidoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_ok() {
        DesaparecidoRequestDTO req = new DesaparecidoRequestDTO();
        DesaparecidoResponseDTO dto = new DesaparecidoResponseDTO();
        dto.setId(UUID.randomUUID());

        when(crearDesaparecidoUseCase.execute(req)).thenReturn(dto);

        ResponseEntity<DesaparecidoResponseDTO> res = controller.crear(req);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals(dto, res.getBody());
    }

    @Test
    void getAll_ok() {
        edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO<DesaparecidoFrontDTO> paged = 
            new edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO<>(List.of(org.mockito.Mockito.mock(DesaparecidoFrontDTO.class)), 0, 20, 1, 1, true);
        when(readDesaparecidoUseCase.execute("Perez", "ACTIVO", "asc", 0, 20)).thenReturn(paged);

        ResponseEntity<edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO<DesaparecidoFrontDTO>> res = controller.getAll("Perez", "ACTIVO", "asc", 0, 20);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(paged, res.getBody());
    }
}

