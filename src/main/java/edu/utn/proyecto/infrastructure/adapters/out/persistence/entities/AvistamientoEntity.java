package edu.utn.proyecto.infrastructure.adapters.out.persistence.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "avistamientos", schema = "public")
public class AvistamientoEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "avistador_id", nullable = false)
    private UUID avistadorId;

    @Column(name = "desaparecido_id", nullable = false)
    private UUID desaparecidoId;

    @Column(name = "ubicacion", columnDefinition = "geometry(Point, 4326)")
    private Point ubicacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "foto_file_id")
    private UUID fotoFileId;

    @Column(nullable = false)
    private Boolean verificado = false;

    @Column(nullable = false)
    private Boolean publico = true;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    public AvistamientoEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAvistadorId() { return avistadorId; }
    public void setAvistadorId(UUID avistadorId) { this.avistadorId = avistadorId; }
    public UUID getDesaparecidoId() { return desaparecidoId; }
    public void setDesaparecidoId(UUID desaparecidoId) { this.desaparecidoId = desaparecidoId; }
    public Point getUbicacion() { return ubicacion; }
    public void setUbicacion(Point ubicacion) { this.ubicacion = ubicacion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public UUID getFotoFileId() { return fotoFileId; }
    public void setFotoFileId(UUID fotoFileId) { this.fotoFileId = fotoFileId; }
    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean verificado) { this.verificado = verificado; }
    public Boolean getPublico() { return publico; }
    public void setPublico(Boolean publico) { this.publico = publico; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}