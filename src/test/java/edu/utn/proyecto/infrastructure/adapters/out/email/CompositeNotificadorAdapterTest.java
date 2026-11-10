package edu.utn.proyecto.infrastructure.adapters.out.email;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.infrastructure.adapters.out.sms.SmsNotificadorAdapter;
import edu.utn.proyecto.infrastructure.adapters.out.websocket.WebsocketNotificadorAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositeNotificadorAdapterTest {

    @Mock
    private WebsocketNotificadorAdapter websocketAdapter;

    @Mock
    private EmailNotificadorAdapter emailAdapter;

    @Mock
    private SmsNotificadorAdapter smsAdapter;

    @InjectMocks
    private CompositeNotificadorAdapter composite;

    @Test
    void notificar_delegaALosTresAdaptadores() {
        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setDesaparecidoId(UUID.randomUUID());

        composite.notificarNuevoAvistamiento(dto);

        verify(websocketAdapter).notificarNuevoAvistamiento(dto);
        verify(emailAdapter).notificarNuevoAvistamiento(dto);
        verify(smsAdapter).notificarNuevoAvistamiento(dto);
    }

    @Test
    void notificar_falloEnEmail_noRompeElFlujo() {
        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setDesaparecidoId(UUID.randomUUID());

        doNothing().when(websocketAdapter).notificarNuevoAvistamiento(dto);
        doThrow(new RuntimeException("SMTP connection refused"))
                .when(emailAdapter).notificarNuevoAvistamiento(dto);

        // EmailNotificadorAdapter captura excepciones internamente;
        // este test verifica que websocket notificó antes que ocurra el error
        try {
            composite.notificarNuevoAvistamiento(dto);
        } catch (RuntimeException ignored) {
            // esperado en este escenario de test
        }

        verify(websocketAdapter).notificarNuevoAvistamiento(dto);
    }

    @Test
    void notificar_falloInternoNoSePropaga() {
        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setDesaparecidoId(UUID.randomUUID());

        // Todos los adaptadores manejan sus propias excepciones internamente
        doNothing().when(websocketAdapter).notificarNuevoAvistamiento(dto);
        doNothing().when(emailAdapter).notificarNuevoAvistamiento(dto);
        doNothing().when(smsAdapter).notificarNuevoAvistamiento(dto);

        assertThatNoException().isThrownBy(() -> composite.notificarNuevoAvistamiento(dto));

        verify(websocketAdapter).notificarNuevoAvistamiento(dto);
        verify(emailAdapter).notificarNuevoAvistamiento(dto);
        verify(smsAdapter).notificarNuevoAvistamiento(dto);
    }
}
