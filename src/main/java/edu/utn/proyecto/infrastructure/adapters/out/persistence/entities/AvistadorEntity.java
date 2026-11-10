package edu.utn.proyecto.infrastructure.adapters.out.persistence.entities;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.security.encrypt.EncryptedFieldConverter;
import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "avistadores", schema = "public",
        uniqueConstraints = @UniqueConstraint(name = "uq_avistadores_dni_hmac", columnNames = "dni_hmac"))
public class AvistadorEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "dni_enc", columnDefinition = "TEXT", nullable = false)
    private String dni;

    @Column(name = "dni_hmac", nullable = false, length = 64)
    private String dniHmac;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "email_enc", columnDefinition = "TEXT")
    private String email;

    @Column(name = "email_hmac", length = 64)
    private String emailHmac;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "telefono_enc", columnDefinition = "TEXT")
    private String telefono;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "direccion_enc", columnDefinition = "TEXT")
    private String direccion;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private Integer edad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.USUARIO;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public AvistadorEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getDniHmac() { return dniHmac; }
    public void setDniHmac(String dniHmac) { this.dniHmac = dniHmac; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailHmac() { return emailHmac; }
    public void setEmailHmac(String emailHmac) { this.emailHmac = emailHmac; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}