package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

public class AvistadorRequestDTO {
    @NotBlank @Pattern(regexp="\\d{7,10}") private String dni;
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    @NotNull
    @Min(0) @Max(120) private Integer edad;
    @NotBlank
    private String direccion;
    @Email
    @Nullable
    private String email;
    @Nullable private String telefono;

    public AvistadorRequestDTO() {}

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}