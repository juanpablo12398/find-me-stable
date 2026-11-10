package edu.utn.proyecto.applicacion.usecase.auth;
import edu.utn.proyecto.domain.service.abstraccion.IAuthService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserUseCase {

    private final IAuthService authService;

    public GetCurrentUserUseCase(IAuthService authService) {
        this.authService = authService;
    }

    public SessionUserDTO execute(HttpServletRequest request) {
        return authService.currentUser(new TokenRequestDTO(request));
    }
}
