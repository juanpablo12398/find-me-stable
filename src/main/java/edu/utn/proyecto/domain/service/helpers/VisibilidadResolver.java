package edu.utn.proyecto.domain.service.helpers;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.security.SecurityContextHelper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class VisibilidadResolver {

    private final SecurityContextHelper securityHelper;

    public VisibilidadResolver(SecurityContextHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    /**
     * Resuelve la query correcta según el rol.
     * @param queryAdmin   query para ADMINISTRADOR (ve completo)
     * @param queryUsuario query para USUARIO (recibe su userId)
     * @param queryPublico query para NO_REGISTRADO (solo públicos)
     */
    public List<Avistamiento> resolver(
            Supplier<List<Avistamiento>> queryAdmin,
            Function<UUID, List<Avistamiento>> queryUsuario,
            Supplier<List<Avistamiento>> queryPublico) {

        Rol rol = securityHelper.getCurrentRol();

        if (rol == Rol.ADMINISTRADOR) {
            return queryAdmin.get();
        }

        UUID userId = securityHelper.getCurrentUserId().orElse(null);
        if (rol == Rol.USUARIO && userId != null) {
            return queryUsuario.apply(userId);
        }

        return queryPublico.get();
    }

    public List<Avistamiento> filtrarEnMemoria(List<Avistamiento> avistamientos) {
        Rol rol = securityHelper.getCurrentRol();

        if (rol == Rol.ADMINISTRADOR) {
            return avistamientos;
        }

        UUID userId = securityHelper.getCurrentUserId().orElse(null);

        return avistamientos.stream()
                .filter(a -> {
                    if (Boolean.TRUE.equals(a.getPublico())) return true;
                    return userId != null && userId.equals(a.getAvistadorId());
                })
                .collect(Collectors.toList());
    }
}