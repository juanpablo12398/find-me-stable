package edu.utn.proyecto.infrastructure.adapters.out.persistence.entities;
import edu.utn.proyecto.security.encrypt.EncryptedFieldConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "desaparecidos")
public class DesaparecidoEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String nombre;
    private String apellido;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "dni_enc", columnDefinition = "TEXT")
    private String dni;

    @Column(name = "dni_hmac", length = 64)
    private String dniHmac;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "contacto_familiar_enc", columnDefinition = "TEXT")
    private String contactoFamiliar;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "email_contacto_familiar_enc", columnDefinition = "TEXT")
    private String emailContactoFamiliar;

    @Column(name = "email_contacto_familiar_hmac", length = 64)
    private String emailContactoFamiliarHmac;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "foto_file_id")
    private UUID fotoFileId;

    @Column(name = "fecha_desaparicion")
    private LocalDateTime fechaDesaparicion;

    @Column(name = "estado")
    private String estado;

    @Column(name = "ultima_ubicacion")
    private String ultimaUbicacion;

    public DesaparecidoEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getDniHmac() { return dniHmac; }
    public void setDniHmac(String dniHmac) { this.dniHmac = dniHmac; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public UUID getFotoFileId() { return fotoFileId; }
    public void setFotoFileId(UUID fotoFileId) { this.fotoFileId = fotoFileId; }
    public LocalDateTime getFechaDesaparicion() { return fechaDesaparicion; }
    public void setFechaDesaparicion(LocalDateTime fechaDesaparicion) { this.fechaDesaparicion = fechaDesaparicion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getContactoFamiliar() { return contactoFamiliar; }
    public void setContactoFamiliar(String contactoFamiliar) { this.contactoFamiliar = contactoFamiliar; }
    public String getEmailContactoFamiliar() { return emailContactoFamiliar; }
    public void setEmailContactoFamiliar(String emailContactoFamiliar) { this.emailContactoFamiliar = emailContactoFamiliar; }
    public String getEmailContactoFamiliarHmac() { return emailContactoFamiliarHmac; }
    public void setEmailContactoFamiliarHmac(String emailContactoFamiliarHmac) { this.emailContactoFamiliarHmac = emailContactoFamiliarHmac; }
    public String getUltimaUbicacion() { return ultimaUbicacion; }
    public void setUltimaUbicacion(String ultimaUbicacion) { this.ultimaUbicacion = ultimaUbicacion; }
}