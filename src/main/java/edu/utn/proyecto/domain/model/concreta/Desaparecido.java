package edu.utn.proyecto.domain.model.concreta;
import edu.utn.proyecto.domain.model.enums.EstadoDesaparecido;
import java.time.LocalDateTime;
import java.util.UUID;

public class Desaparecido {
    private UUID id;
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDateTime fechaDesaparicion;
    private String descripcion;
    private UUID fotoFileId;
    private EstadoDesaparecido estado;
    private String contactoFamiliar;
    private String emailContactoFamiliar;
    private String ultimaUbicacion;

    public Desaparecido(String nombre, String apellido, String dni, String descripcion,
                        UUID fotoFileId, LocalDateTime fechaDesaparicion, EstadoDesaparecido estado,
                        String contactoFamiliar, String ultimaUbicacion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.descripcion = descripcion;
        this.fotoFileId = fotoFileId;
        this.fechaDesaparicion = (fechaDesaparicion != null) ? fechaDesaparicion : LocalDateTime.now();
        this.estado = (estado != null) ? estado : EstadoDesaparecido.BUSCANDO;
        this.contactoFamiliar = contactoFamiliar;
        this.ultimaUbicacion = ultimaUbicacion;
    }

    public Desaparecido(String nombre, String apellido, String dni, String descripcion, UUID fotoFileId) {
        this(nombre, apellido, dni, descripcion, fotoFileId, null, null, null, null);
    }

    public Desaparecido() {}

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
    public EstadoDesaparecido getEstado() { return estado; }
    public void setEstado(EstadoDesaparecido estado) { this.estado = estado; }
    public String getContactoFamiliar() { return contactoFamiliar; }
    public void setContactoFamiliar(String contactoFamiliar) { this.contactoFamiliar = contactoFamiliar; }
    public String getEmailContactoFamiliar() { return emailContactoFamiliar; }
    public void setEmailContactoFamiliar(String emailContactoFamiliar) { this.emailContactoFamiliar = emailContactoFamiliar; }
    public String getUltimaUbicacion() { return ultimaUbicacion; }
    public void setUltimaUbicacion(String ultimaUbicacion) { this.ultimaUbicacion = ultimaUbicacion; }
}