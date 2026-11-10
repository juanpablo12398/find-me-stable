package edu.utn.proyecto.infrastructure.adapters.out.sms;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import edu.utn.proyecto.infrastructure.ports.out.jpa.NotificationLogJpaRepository;
import edu.utn.proyecto.security.encrypt.BlindIndexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsNotificadorAdapterTest {

    @Mock
    private IRepoDeDesaparecidos repoDesaparecidos;

    @Mock
    private NotificationLogJpaRepository notificationLogRepo;

    @Mock
    private BlindIndexer blindIndexer;

    @InjectMocks
    private SmsNotificadorAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "enabled", false);
    }

    @Test
    void notificar_cuandoSmsDeshabilitado_noInteractuaConRepo() {
        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setDesaparecidoId(UUID.randomUUID());

        assertThatNoException().isThrownBy(() -> adapter.notificarNuevoAvistamiento(dto));

        verifyNoInteractions(repoDesaparecidos);
    }

    @Test
    void notificar_cuandoDesaparecidoNoEncontrado_noLanzaExcepcion() {
        ReflectionTestUtils.setField(adapter, "enabled", true);
        UUID desaparecidoId = UUID.randomUUID();
        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setDesaparecidoId(desaparecidoId);

        when(repoDesaparecidos.findById(desaparecidoId)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> adapter.notificarNuevoAvistamiento(dto));
    }

    @Test
    void notificar_cuandoContactoEsNull_noLanzaExcepcion() {
        ReflectionTestUtils.setField(adapter, "enabled", true);
        UUID desaparecidoId = UUID.randomUUID();

        Desaparecido desaparecido = new Desaparecido();
        desaparecido.setId(desaparecidoId);
        desaparecido.setNombre("Ana");
        desaparecido.setApellido("García");
        desaparecido.setContactoFamiliar(null);

        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setDesaparecidoId(desaparecidoId);

        when(repoDesaparecidos.findById(desaparecidoId)).thenReturn(Optional.of(desaparecido));

        // Aunque SMS está habilitado, no hay teléfono → no debe lanzar excepción
        assertThatNoException().isThrownBy(() -> adapter.notificarNuevoAvistamiento(dto));
    }
}
