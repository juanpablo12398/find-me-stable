package edu.utn.proyecto.applicacion.mappers;
import edu.utn.proyecto.common.normalize.DniNormalizer;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class LoginMapper {


    private final DniNormalizer dniNorm;
    private final TextNormalizer txtNorm;

    public LoginMapper(DniNormalizer dniNorm, TextNormalizer txtNorm) {
        this.dniNorm = dniNorm;
        this.txtNorm = txtNorm;
    }

    public void normalizeRequestInPlace(LoginRequestDTO dto) {
        dto.setDni(dniNorm.normalize(dto.getDni()));
        dto.setEmail(dto.getEmail() == null ? "" : txtNorm.normalize(dto.getEmail()));
    }

    public SessionUserDTO fromLoginRequestToSession(LoginRequestDTO dto, UUID avistadorId, String resolvedNombre, String rol) {
        return new SessionUserDTO(
                avistadorId,
                dto.getDni(),
                dto.getEmail(),
                resolvedNombre,
                rol
        );
    }
}