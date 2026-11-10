package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;
import java.util.UUID;

public class SessionUserDTO {
    private UUID id;
    private String dni;
    private String email;
    private String nombre;
    private String rol;

    public SessionUserDTO(UUID id, String dni, String email, String nombre, String rol) {
        this.id = id;
        this.dni = dni;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    public UUID getId() { return id; }
    public String getDni() { return dni; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
}