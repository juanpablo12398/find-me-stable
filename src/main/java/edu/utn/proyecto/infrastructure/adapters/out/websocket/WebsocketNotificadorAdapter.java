package edu.utn.proyecto.infrastructure.adapters.out.websocket;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.infrastructure.ports.out.INotificadorAvistamiento;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebsocketNotificadorAdapter implements INotificadorAvistamiento {

    private final SimpMessagingTemplate messagingTemplate;

    public WebsocketNotificadorAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notificarNuevoAvistamiento(AvistamientoResponseDTO avistamiento) {
        // Envia el avistamiento a todos los clientes suscritos al canal del desaparecido
        String destination = "/topic/avistamientos/" + avistamiento.getDesaparecidoId();
        messagingTemplate.convertAndSend(destination, avistamiento);
        
        // Enviar también a canal global de avistamientos para mapas que no filtran
        messagingTemplate.convertAndSend("/topic/avistamientos", avistamiento);
    }
}
