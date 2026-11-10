package edu.utn.proyecto.applicacion.usecase.auth;
import edu.utn.proyecto.domain.service.auth.AuthService;
import edu.utn.proyecto.domain.service.abstraccion.IAuthService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {
    private final IAuthService authService;

    public LoginUseCase(AuthService authService) {
        this.authService = authService;
    }

    public SessionUserDTO execute(LoginRequestDTO dto) {
        return authService.login(dto);
    }
}
