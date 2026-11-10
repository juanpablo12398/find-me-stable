package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class DesaparecidoRequestDTO {
    public String nombre;
    public String apellido;
    public int edad;
    public String dni;
    public UUID fotoFileId;         // Referencia al archivo subido previamente via /api/files/upload
    public String descripcion;
    public LocalDateTime fechaDesaparicion;
    public String contactoFamiliar;
    public String emailContactoFamiliar;
    public String ultimaUbicacion;

    public DesaparecidoRequestDTO() {}

    public DesaparecidoRequestDTO(String nombre, String apellido, int edad, String dni,
                                  UUID fotoFileId, String descripcion, LocalDateTime fechaDesaparicion,
                                  String contactoFamiliar, String ultimaUbicacion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.dni = dni;
        this.fotoFileId = fotoFileId;
        this.descripcion = descripcion;
        this.fechaDesaparicion = fechaDesaparicion;
        this.contactoFamiliar = contactoFamiliar;
        this.ultimaUbicacion = ultimaUbicacion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public UUID getFotoFileId() { return fotoFileId; }
    public void setFotoFileId(UUID fotoFileId) { this.fotoFileId = fotoFileId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaDesaparicion() { return fechaDesaparicion; }
    public void setFechaDesaparicion(LocalDateTime fechaDesaparicion) { this.fechaDesaparicion = fechaDesaparicion; }
    public String getContactoFamiliar() { return contactoFamiliar; }
    public void setContactoFamiliar(String contactoFamiliar) { this.contactoFamiliar = contactoFamiliar; }
    public String getEmailContactoFamiliar() { return emailContactoFamiliar; }
    public void setEmailContactoFamiliar(String emailContactoFamiliar) { this.emailContactoFamiliar = emailContactoFamiliar; }
    public String getUltimaUbicacion() { return ultimaUbicacion; }
    public void setUltimaUbicacion(String ultimaUbicacion) { this.ultimaUbicacion = ultimaUbicacion; }
}