package edu.utn.proyecto.applicacion.usecase.desaparecido;

import edu.utn.proyecto.domain.service.desaparecido.DesaparecidoService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadDesaparecidoUseCaseTest {

    @Mock
    private DesaparecidoService desaparecidoService;

    @InjectMocks
    private ReadDesaparecidoUseCase useCase;

    @Test
    void debeDevolverListaConParametrosPorDefecto() {
        // Arrange
        DesaparecidoFrontDTO dto = new DesaparecidoFrontDTO(UUID.randomUUID(), "Juan", "Perez", null, "Desc", null,
                "Hoy", "BUSCANDO", null, null, "ALTA", 1);
        when(desaparecidoService.buscarParaFront(null, null, "urgencia")).thenReturn(List.of(dto));

        // Act
        List<DesaparecidoFrontDTO> result = useCase.execute();

        // Assert
        assertThat(result).isNotEmpty().hasSize(1);
        verify(desaparecidoService).buscarParaFront(null, null, "urgencia");
    }

    @Test
    void debeDevolverListaConParametrosEspecificados() {
        // Arrange
        DesaparecidoFrontDTO dto = new DesaparecidoFrontDTO(UUID.randomUUID(), "Pedro", "Gomez", null, "Desc2", null,
                "Ayer", "ENCONTRADO", null, null, "BAJA", 3);
        when(desaparecidoService.buscarParaFront("Pedro", "ENCONTRADO", "nombre")).thenReturn(List.of(dto));

        // Act
        List<DesaparecidoFrontDTO> result = useCase.execute("Pedro", "ENCONTRADO", "nombre");

        // Assert
        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Pedro");
        verify(desaparecidoService).buscarParaFront("Pedro", "ENCONTRADO", "nombre");
    }
}
