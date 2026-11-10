package edu.utn.proyecto.infrastructure.adapters.out.email;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.infrastructure.adapters.out.sms.SmsNotificadorAdapter;
import edu.utn.proyecto.infrastructure.adapters.out.websocket.WebsocketNotificadorAdapter;
import edu.utn.proyecto.infrastructure.ports.out.INotificadorAvistamiento;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Adaptador compuesto (Composite pattern) que agrega todos los canales de notificación.
 * Es el bean @Primary para INotificadorAvistamiento: el servicio de aplicación lo inyecta
 * sin saber qué canales concretos se usan.
 *
 * Canales actuales:
 *  - WebSocket: notificación en tiempo real a clientes conectados.
 *  - Email: se envía si el desaparecido tiene emailContactoFamiliar (mail.enabled=true).
 *  - SMS: se envía si el desaparecido tiene contactoFamiliar/celular (sms.enabled=true).
 *
 * Nota: para activar WhatsApp Business API via Twilio en el futuro, es suficiente con
 * reemplazar SmsNotificadorAdapter por WhatsAppNotificadorAdapter con un cambio mínimo
 * de configuración. Esta clase no requiere modificación.
 */
@Primary
@Service
public class CompositeNotificadorAdapter implements INotificadorAvistamiento {

    private final WebsocketNotificadorAdapter websocketAdapter;
    private final EmailNotificadorAdapter emailAdapter;
    private final SmsNotificadorAdapter smsAdapter;

    public CompositeNotificadorAdapter(WebsocketNotificadorAdapter websocketAdapter,
                                       EmailNotificadorAdapter emailAdapter,
                                       SmsNotificadorAdapter smsAdapter) {
        this.websocketAdapter = websocketAdapter;
        this.emailAdapter = emailAdapter;
        this.smsAdapter = smsAdapter;
    }

    @Override
    public void notificarNuevoAvistamiento(AvistamientoResponseDTO avistamiento) {
        // WebSocket: notificación en tiempo real a clientes conectados
        websocketAdapter.notificarNuevoAvistamiento(avistamiento);
        // Email: destinatario = emailContactoFamiliar del desaparecido (si existe y mail.enabled=true)
        emailAdapter.notificarNuevoAvistamiento(avistamiento);
        // SMS: destinatario = contactoFamiliar (celular) del desaparecido (si existe y sms.enabled=true)
        smsAdapter.notificarNuevoAvistamiento(avistamiento);
    }
}
