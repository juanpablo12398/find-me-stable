package edu.utn.proyecto.applicacion.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class DesaparecidoResponseDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDateTime fechaDesaparicion;
    private String descripcion;
    private UUID fotoFileId;
    private String estado;
    private String contactoFamiliar;
    private String emailContactoFamiliar;
    private String ultimaUbicacion;

    public DesaparecidoResponseDTO() {}

    public DesaparecidoResponseDTO(UUID id, String nombre, String apellido, String dni,
                                   LocalDateTime fechaDesaparicion, String descripcion,
                                   UUID fotoFileId, String estado, String contactoFamiliar,
                                   String ultimaUbicacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaDesaparicion = fechaDesaparicion;
        this.descripcion = descripcion;
        this.fotoFileId = fotoFileId;
        this.estado = estado;
        this.contactoFamiliar = contactoFamiliar;
        this.ultimaUbicacion = ultimaUbicacion;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public LocalDateTime getFechaDesaparicion() { return fechaDesaparicion; }
    public void setFechaDesaparicion(LocalDateTime fechaDesaparicion) { this.fechaDesaparicion = fechaDesaparicion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public UUID getFotoFileId() { return fotoFileId; }
    public void setFotoFileId(UUID fotoFileId) { this.fotoFileId = fotoFileId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getContactoFamiliar() { return contactoFamiliar; }
    public void setContactoFamiliar(String contactoFamiliar) { this.contactoFamiliar = contactoFamiliar; }
    public String getEmailContactoFamiliar() { return emailContactoFamiliar; }
    public void setEmailContactoFamiliar(String emailContactoFamiliar) { this.emailContactoFamiliar = emailContactoFamiliar; }
    public String getUltimaUbicacion() { return ultimaUbicacion; }
    public void setUltimaUbicacion(String ultimaUbicacion) { this.ultimaUbicacion = ultimaUbicacion; }
}