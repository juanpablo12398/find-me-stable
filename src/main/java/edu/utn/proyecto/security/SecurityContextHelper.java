package edu.utn.proyecto.security;
import edu.utn.proyecto.domain.model.enums.Rol;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;


@Component
public class SecurityContextHelper {

    public Optional<String> getCurrentDni() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();

        Object principal = auth.getPrincipal();
        if (principal instanceof String dni && !"anonymousUser".equals(dni)) {
            return Optional.of(dni);
        }
        return Optional.empty();
    }

    public Optional<UUID> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();

        Object details = auth.getDetails();
        if (details instanceof UUID uuid) {
            return Optional.of(uuid);
        }
        return Optional.empty();
    }

    public Rol getCurrentRol() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Rol.NO_REGISTRADO;

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5)) // quita "ROLE_"
                .findFirst()
                .map(rolStr -> {
                    try {
                        return Rol.valueOf(rolStr);
                    } catch (IllegalArgumentException e) {
                        return Rol.NO_REGISTRADO;
                    }
                })
                .orElse(Rol.NO_REGISTRADO);
    }

    public boolean isAuthenticated() {
        return getCurrentDni().isPresent();
    }

    public boolean isAdmin() {
        return getCurrentRol() == Rol.ADMINISTRADOR;
    }
}