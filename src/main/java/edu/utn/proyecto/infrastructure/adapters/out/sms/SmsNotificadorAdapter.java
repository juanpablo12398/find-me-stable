package edu.utn.proyecto.infrastructure.adapters.out.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.NotificationLogEntity;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import edu.utn.proyecto.infrastructure.ports.out.jpa.NotificationLogJpaRepository;
import edu.utn.proyecto.security.encrypt.BlindIndexer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Adaptador de notificaciones por SMS usando Twilio API.
 * Envía un mensaje al contactoFamiliar (celular) del desaparecido cuando se registra
 * un nuevo avistamiento.
 *
 * Nota de migración futura: para activar WhatsApp Business API via Twilio,
 * reemplazar PhoneNumber(fromNumber) por PhoneNumber("whatsapp:" + fromNumber)
 * y el destinatario por PhoneNumber("whatsapp:" + telefono).
 * El cambio es mínimo — esta clase no requiere refactor adicional.
 */
@Component
public class SmsNotificadorAdapter {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificadorAdapter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IRepoDeDesaparecidos repoDesaparecidos;
    private final NotificationLogJpaRepository notificationLogRepo;
    private final BlindIndexer blindIndexer;

    @Value("${sms.enabled:false}")
    private boolean enabled;

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String fromNumber;

    public SmsNotificadorAdapter(IRepoDeDesaparecidos repoDesaparecidos,
                                  NotificationLogJpaRepository notificationLogRepo,
                                  BlindIndexer blindIndexer) {
        this.repoDesaparecidos = repoDesaparecidos;
        this.notificationLogRepo = notificationLogRepo;
        this.blindIndexer = blindIndexer;
    }

    @PostConstruct
    public void init() {
        if (enabled && !accountSid.isBlank() && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio SMS adapter inicializado");
        }
    }

    public void notificarNuevoAvistamiento(AvistamientoResponseDTO avistamiento) {
        if (!enabled) {
            return;
        }
        String telefono = null;
        Desaparecido desaparecido = null;
        try {
            Optional<Desaparecido> optDesaparecido = repoDesaparecidos.findById(avistamiento.getDesaparecidoId());
            if (optDesaparecido.isEmpty()) {
                log.warn("No se encontró desaparecido con id {} para notificar por SMS",
                        avistamiento.getDesaparecidoId());
                return;
            }
            desaparecido = optDesaparecido.get();
            telefono = desaparecido.getContactoFamiliar();
            if (telefono == null || telefono.isBlank()) {
                log.info("Desaparecido {} no tiene contactoFamiliar (celular), omitiendo SMS",
                        desaparecido.getId());
                return;
            }

            String fechaStr = avistamiento.getFechaHora() != null
                    ? avistamiento.getFechaHora().format(FORMATTER)
                    : "sin fecha";
            String ubicacion = avistamiento.getLatitud() != null && avistamiento.getLongitud() != null
                    ? avistamiento.getLatitud() + ", " + avistamiento.getLongitud()
                    : "ubicación no disponible";

            String cuerpo = String.format(
                    "FIND ME: Se reportó un avistamiento de %s %s el %s en %s. Ingresá al sistema para más información.",
                    desaparecido.getNombre(), desaparecido.getApellido(), fechaStr, ubicacion);

            // Para migrar a WhatsApp Business API via Twilio en el futuro, reemplazar:
            //   new PhoneNumber(fromNumber)  →  new PhoneNumber("whatsapp:" + fromNumber)
            //   new PhoneNumber(telefono)    →  new PhoneNumber("whatsapp:" + telefono)
            Message.creator(
                    new PhoneNumber(telefono),
                    new PhoneNumber(fromNumber),
                    cuerpo
            ).create();

            log.info("SMS enviado al contacto familiar de desaparecido {}", desaparecido.getId());
            persistirLog("SMS", telefono, desaparecido.getId(), avistamiento.getId(), "ENVIADO", null);
        } catch (Exception e) {
            log.error("Error al enviar SMS para avistamiento {}: {}", avistamiento.getId(), e.getMessage(), e);
            if (desaparecido != null && telefono != null) {
                persistirLog("SMS", telefono, desaparecido.getId(), avistamiento.getId(), "FALLIDO", e.getMessage());
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
            log.warn("No se pudo persistir el log de notificación SMS: {}", logEx.getMessage());
        }
    }
}
