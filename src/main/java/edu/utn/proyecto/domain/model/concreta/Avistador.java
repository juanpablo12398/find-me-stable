package edu.utn.proyecto.domain.model.concreta;
import edu.utn.proyecto.domain.model.enums.Rol;
import java.time.LocalDateTime;
import java.util.UUID;

public class Avistador {
    private UUID id;
    private String dni;
    private String nombre;
    private String apellido;
    private Integer edad;
    private String direccion;
    private String email;
    private String telefono;
    private Rol rol;
    private LocalDateTime creadoEn;

    public Avistador() {
        this.rol = Rol.USUARIO;
    }

    public Avistador(UUID id, String dni, String nombre, String apellido,
                     Integer edad, String direccion, String email, String telefono,
                     Rol rol, LocalDateTime creadoEn) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
        this.rol = (rol != null) ? rol : Rol.USUARIO;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}