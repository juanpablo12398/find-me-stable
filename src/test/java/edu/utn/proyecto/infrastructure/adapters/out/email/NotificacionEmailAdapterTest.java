package edu.utn.proyecto.infrastructure.adapters.out.email;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import edu.utn.proyecto.infrastructure.ports.out.jpa.NotificationLogJpaRepository;
import edu.utn.proyecto.security.encrypt.BlindIndexer;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionEmailAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private IRepoDeDesaparecidos repoDesaparecidos;

    @Mock
    private NotificationLogJpaRepository notificationLogRepo;

    @Mock
    private BlindIndexer blindIndexer;

    @InjectMocks
    private EmailNotificadorAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "enabled", true);
        ReflectionTestUtils.setField(adapter, "from", "noreply@findme.com");
    }

    @Test
    void notificar_cuandoHayContacto_enviaEmail() throws Exception {
        UUID desaparecidoId = UUID.randomUUID();
        UUID avistamientoId = UUID.randomUUID();

        Desaparecido desaparecido = new Desaparecido();
        desaparecido.setId(desaparecidoId);
        desaparecido.setNombre("Juan");
        desaparecido.setApellido("Pérez");
        desaparecido.setEmailContactoFamiliar("familiar@example.com");

        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setId(avistamientoId);
        dto.setDesaparecidoId(desaparecidoId);
        dto.setLatitud(-34.60);
        dto.setLongitud(-58.38);
        dto.setFechaHora(LocalDateTime.of(2026, 3, 19, 10, 0));

        // MimeMessage real (sin Session) para que MimeMessageHelper funcione correctamente
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(repoDesaparecidos.findById(desaparecidoId)).thenReturn(Optional.of(desaparecido));
        when(templateEngine.process(eq("notificacion-avistamiento"), any(Context.class)))
                .thenReturn("<html>contenido</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(blindIndexer.index(any())).thenReturn("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        adapter.notificarNuevoAvistamiento(dto);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void notificar_cuandoMailDeshabilitado_noEnviaEmail() {
        ReflectionTestUtils.setField(adapter, "enabled", false);

        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setDesaparecidoId(UUID.randomUUID());

        adapter.notificarNuevoAvistamiento(dto);

        verifyNoInteractions(mailSender, templateEngine, repoDesaparecidos);
    }

    @Test
    void notificar_cuandoDesaparecidoNoEncontrado_noEnviaEmail() {
        UUID desaparecidoId = UUID.randomUUID();
        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setDesaparecidoId(desaparecidoId);

        when(repoDesaparecidos.findById(desaparecidoId)).thenReturn(Optional.empty());

        adapter.notificarNuevoAvistamiento(dto);

        verifyNoInteractions(mailSender, templateEngine);
    }

    @Test
    void notificar_cuandoContactoEsNull_noEnviaEmail() {
        UUID desaparecidoId = UUID.randomUUID();

        Desaparecido desaparecido = new Desaparecido();
        desaparecido.setId(desaparecidoId);
        desaparecido.setNombre("Ana");
        desaparecido.setApellido("García");
        desaparecido.setContactoFamiliar(null);

        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setDesaparecidoId(desaparecidoId);

        when(repoDesaparecidos.findById(desaparecidoId)).thenReturn(Optional.of(desaparecido));

        adapter.notificarNuevoAvistamiento(dto);

        verifyNoInteractions(mailSender, templateEngine);
    }
}
