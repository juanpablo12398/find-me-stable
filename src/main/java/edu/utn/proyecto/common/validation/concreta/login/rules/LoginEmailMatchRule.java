package edu.utn.proyecto.common.validation.concreta.login.rules;
import edu.utn.proyecto.auth.exception.LoginError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class LoginEmailMatchRule implements Rule<LoginRequestDTO> {

    private final IRepoDeAvistadores repo;

    public LoginEmailMatchRule(IRepoDeAvistadores repo) {
        this.repo = repo;
    }

    @Override
    public void check(LoginRequestDTO dto) {
        Avistador avistador = repo.findByDni(dto.getDni()).orElse(null);

        if (!Objects.equals(dto.getEmail(), avistador.getEmail())) {
            throw DomainException.of(
                    LoginError.EMAIL_MISMATCH.key,
                    LoginError.EMAIL_MISMATCH.status
            );
        }
    }
}
