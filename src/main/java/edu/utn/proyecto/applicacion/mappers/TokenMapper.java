package edu.utn.proyecto.applicacion.mappers;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TokenMapper {

    public SessionUserDTO toSessionUser(Claims claims) {
        String dni    = claims.getSubject();
        String idStr  = claims.get("id", String.class);
        String email  = claims.get("email", String.class) != null ? claims.get("email", String.class) : "";
        String nombre = claims.get("name", String.class)  != null ? claims.get("name", String.class)  : "";
        String rol    = claims.get("rol", String.class)   != null ? claims.get("rol", String.class)   : "USUARIO";

        return new SessionUserDTO(
                UUID.fromString(idStr),
                dni,
                email,
                nombre,
                rol
        );
    }
}
