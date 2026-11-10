package edu.utn.proyecto.domain.model.concreta;
import java.time.LocalDateTime;
import java.util.UUID;

public class Avistamiento {
    private UUID id;
    private UUID avistadorId;
    private UUID desaparecidoId;

    private Double latitud;
    private Double longitud;

    private LocalDateTime fechaHora;
    private String descripcion;
    private UUID fotoFileId;
    private Boolean verificado;
    private Boolean publico;
    private LocalDateTime creadoEn;

    public Avistamiento() {
        this.fechaHora = LocalDateTime.now();
        this.creadoEn = LocalDateTime.now();
        this.verificado = false;
        this.publico = true;
    }

    public Avistamiento(UUID avistadorId, UUID desaparecidoId,
                        Double latitud, Double longitud, String descripcion,
                        LocalDateTime fechaAvistamiento) {
        this.avistadorId = avistadorId;
        this.desaparecidoId = desaparecidoId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
        this.fechaHora = (fechaAvistamiento != null) ? fechaAvistamiento : LocalDateTime.now();
        this.creadoEn = LocalDateTime.now();
        this.verificado = false;
        this.publico = true;
    }

    public Avistamiento(UUID avistadorId, UUID desaparecidoId,
                        Double latitud, Double longitud, String descripcion) {
        this(avistadorId, desaparecidoId, latitud, longitud, descripcion, null);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAvistadorId() { return avistadorId; }
    public void setAvistadorId(UUID avistadorId) { this.avistadorId = avistadorId; }
    public UUID getDesaparecidoId() { return desaparecidoId; }
    public void setDesaparecidoId(UUID desaparecidoId) { this.desaparecidoId = desaparecidoId; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
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