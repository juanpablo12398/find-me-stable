package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;

import java.util.UUID;

public class DesaparecidoFrontDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private String dni;
    private String descripcion;
    private String fotoUrl;
    private String fechaFormateada;
    private String estado;
    private String contactoFamiliar;
    private String ultimaUbicacion;
    private String nivelUrgencia;
    private int prioridadUrgencia;
    private Boolean fotoBloqueada;

    public DesaparecidoFrontDTO(UUID id, String nombre, String apellido, String dni,
                                String descripcion, String fotoUrl, String fechaFormateada,
                                String estado, String contactoFamiliar, String ultimaUbicacion,
                                String nivelUrgencia, int prioridadUrgencia) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.descripcion = descripcion;
        this.fotoUrl = fotoUrl;
        this.fechaFormateada = fechaFormateada;
        this.estado = estado;
        this.contactoFamiliar = contactoFamiliar;
        this.ultimaUbicacion = ultimaUbicacion;
        this.nivelUrgencia = nivelUrgencia;
        this.prioridadUrgencia = prioridadUrgencia;
        this.fotoBloqueada = false;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public String getFechaFormateada() { return fechaFormateada; }
    public void setFechaFormateada(String fechaFormateada) { this.fechaFormateada = fechaFormateada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getContactoFamiliar() { return contactoFamiliar; }
    public void setContactoFamiliar(String contactoFamiliar) { this.contactoFamiliar = contactoFamiliar; }
    public String getUltimaUbicacion() { return ultimaUbicacion; }
    public void setUltimaUbicacion(String ultimaUbicacion) { this.ultimaUbicacion = ultimaUbicacion; }
    public String getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(String nivelUrgencia) { this.nivelUrgencia = nivelUrgencia; }
    public int getPrioridadUrgencia() { return prioridadUrgencia; }
    public void setPrioridadUrgencia(int prioridadUrgencia) { this.prioridadUrgencia = prioridadUrgencia; }
    public Boolean getFotoBloqueada() { return fotoBloqueada; }
    public void setFotoBloqueada(Boolean fotoBloqueada) { this.fotoBloqueada = fotoBloqueada; }
}