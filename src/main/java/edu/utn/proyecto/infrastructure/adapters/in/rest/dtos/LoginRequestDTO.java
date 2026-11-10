package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequestDTO {
    @NotBlank
    @Pattern(regexp = "^[0-9]{7,10}$")
    private String dni;

    @NotBlank @Email
    private String email;

    private String resolvedNombre;

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResolvedNombre() { return resolvedNombre; }
    public void setResolvedNombre(String resolvedNombre) { this.resolvedNombre = resolvedNombre; }
}