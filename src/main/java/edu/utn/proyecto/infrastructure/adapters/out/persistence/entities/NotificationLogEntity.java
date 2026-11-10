package edu.utn.proyecto.infrastructure.adapters.out.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para registrar intentos de notificación (email/SMS).
 * El campo destinatario_hmac almacena el HMAC del contacto real,
 * nunca el valor en claro (protección en reposo).
 */
@Entity
@Table(name = "notification_log")
public class NotificationLogEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "canal", nullable = false, length = 10)
    private String canal;

    @Column(name = "destinatario_hmac", nullable = false, length = 64)
    private String destinatarioHmac;

    @Column(name = "desaparecido_id")
    private UUID desaparecidoId;

    @Column(name = "avistamiento_id")
    private UUID avistamientoId;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    public NotificationLogEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public String getDestinatarioHmac() { return destinatarioHmac; }
    public void setDestinatarioHmac(String destinatarioHmac) { this.destinatarioHmac = destinatarioHmac; }

    public UUID getDesaparecidoId() { return desaparecidoId; }
    public void setDesaparecidoId(UUID desaparecidoId) { this.desaparecidoId = desaparecidoId; }

    public UUID getAvistamientoId() { return avistamientoId; }
    public void setAvistamientoId(UUID avistamientoId) { this.avistamientoId = avistamientoId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getMensajeError() { return mensajeError; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }
}
