package edu.utn.proyecto.applicacion.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class AvistamientoResponseDTO {
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

    public AvistamientoResponseDTO() {}

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