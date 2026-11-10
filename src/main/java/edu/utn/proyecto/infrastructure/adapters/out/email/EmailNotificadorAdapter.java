package edu.utn.proyecto.infrastructure.adapters.out.email;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.NotificationLogEntity;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import edu.utn.proyecto.infrastructure.ports.out.jpa.NotificationLogJpaRepository;
import edu.utn.proyecto.security.encrypt.BlindIndexer;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EmailNotificadorAdapter {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificadorAdapter.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final IRepoDeDesaparecidos repoDesaparecidos;
    private final NotificationLogJpaRepository notificationLogRepo;
    private final BlindIndexer blindIndexer;

    @Value("${mail.enabled:false}")
    private boolean enabled;

    @Value("${mail.from:noreply@findme.com}")
    private String from;

    public EmailNotificadorAdapter(JavaMailSender mailSender,
                                   TemplateEngine templateEngine,
                                   IRepoDeDesaparecidos repoDesaparecidos,
                                   NotificationLogJpaRepository notificationLogRepo,
                                   BlindIndexer blindIndexer) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.repoDesaparecidos = repoDesaparecidos;
        this.notificationLogRepo = notificationLogRepo;
        this.blindIndexer = blindIndexer;
    }

    public void notificarNuevoAvistamiento(AvistamientoResponseDTO avistamiento) {
        if (!enabled) {
            return;
        }
        String contacto = null;
        Desaparecido desaparecido = null;
        try {
            Optional<Desaparecido> optDesaparecido = repoDesaparecidos.findById(avistamiento.getDesaparecidoId());
            if (optDesaparecido.isEmpty()) {
                log.warn("No se encontró desaparecido con id {} para notificar por email",
                        avistamiento.getDesaparecidoId());
                return;
            }
            desaparecido = optDesaparecido.get();
            contacto = desaparecido.getEmailContactoFamiliar();
            if (contacto == null || contacto.isBlank()) {
                log.info("Desaparecido {} no tiene emailContactoFamiliar registrado, omitiendo notificación por email",
                        desaparecido.getId());
                return;
            }

            Context ctx = new Context();
            ctx.setVariable("desaparecido", desaparecido);
            ctx.setVariable("avistamiento", avistamiento);
            String html = templateEngine.process("notificacion-avistamiento", ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(contacto);
            helper.setSubject("Nuevo avistamiento de " + desaparecido.getNombre()
                    + " " + desaparecido.getApellido());
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Notificación por email enviada al contacto familiar de desaparecido {}",
                    desaparecido.getId());

            persistirLog("EMAIL", contacto, desaparecido.getId(), avistamiento.getId(), "ENVIADO", null);
        } catch (Exception e) {
            log.error("Error al enviar notificación por email para avistamiento {}: {}",
                    avistamiento.getId(), e.getMessage(), e);
            if (desaparecido != null && contacto != null) {
                persistirLog("EMAIL", contacto, desaparecido.getId(), avistamiento.getId(), "FALLIDO", e.getMessage());
            }
        }
    }

    private void persistirLog(String canal, String contacto, java.util.UUID desaparecidoId,
                               java.util.UUID avistamientoId, String estado, String mensajeError) {
        try {
            NotificationLogEntity entry = new NotificationLogEntity();
            entry.setCanal(canal);
            entry.setDestinatarioHmac(blindIndexer.index(contacto));
            entry.setDesaparecidoId(desaparecidoId);
            entry.setAvistamientoId(avistamientoId);
            entry.setEstado(estado);
            entry.setTimestamp(LocalDateTime.now());
            entry.setMensajeError(mensajeError);
            notificationLogRepo.save(entry);
        } catch (Exception logEx) {
            log.warn("No se pudo persistir el log de notificación email: {}", logEx.getMessage());
        }
    }
}
