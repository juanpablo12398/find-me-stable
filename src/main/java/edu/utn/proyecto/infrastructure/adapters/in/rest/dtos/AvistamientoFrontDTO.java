package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;

import java.util.UUID;

public class AvistamientoFrontDTO {

    private UUID id;
    private Double latitud;
    private Double longitud;
    private String fechaFormateada;
    private String descripcion;
    private String fotoUrl;
    private Boolean verificado;
    private Boolean fotoBloqueada;

    private UUID desaparecidoId;
    private String desaparecidoNombre;
    private String desaparecidoApellido;
    private String desaparecidoFoto;           // URL: "/api/files/{desaparecido.fotoFileId}"
    private Boolean desaparecidoFotoBloqueada;

    private String avistadorNombre;
    private String nivelUrgencia;

    public AvistamientoFrontDTO() {
        this.fotoBloqueada = false;
        this.desaparecidoFotoBloqueada = false;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public String getFechaFormateada() { return fechaFormateada; }
    public void setFechaFormateada(String fechaFormateada) { this.fechaFormateada = fechaFormateada; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean verificado) { this.verificado = verificado; }
    public Boolean getFotoBloqueada() { return fotoBloqueada; }
    public void setFotoBloqueada(Boolean fotoBloqueada) { this.fotoBloqueada = fotoBloqueada; }
    public UUID getDesaparecidoId() { return desaparecidoId; }
    public void setDesaparecidoId(UUID desaparecidoId) { this.desaparecidoId = desaparecidoId; }
    public String getDesaparecidoNombre() { return desaparecidoNombre; }
    public void setDesaparecidoNombre(String desaparecidoNombre) { this.desaparecidoNombre = desaparecidoNombre; }
    public String getDesaparecidoApellido() { return desaparecidoApellido; }
    public void setDesaparecidoApellido(String desaparecidoApellido) { this.desaparecidoApellido = desaparecidoApellido; }
    public String getDesaparecidoFoto() { return desaparecidoFoto; }
    public void setDesaparecidoFoto(String desaparecidoFoto) { this.desaparecidoFoto = desaparecidoFoto; }
    public Boolean getDesaparecidoFotoBloqueada() { return desaparecidoFotoBloqueada; }
    public void setDesaparecidoFotoBloqueada(Boolean desaparecidoFotoBloqueada) { this.desaparecidoFotoBloqueada = desaparecidoFotoBloqueada; }
    public String getAvistadorNombre() { return avistadorNombre; }
    public void setAvistadorNombre(String avistadorNombre) { this.avistadorNombre = avistadorNombre; }
    public String getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(String nivelUrgencia) { this.nivelUrgencia = nivelUrgencia; }
}