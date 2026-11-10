package edu.utn.proyecto.common.validation.concreta.login.rules;
import edu.utn.proyecto.auth.exception.LoginError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class LoginAvistadorExistsRule implements Rule<LoginRequestDTO> {

    private final IRepoDeAvistadores repo;

    public LoginAvistadorExistsRule(IRepoDeAvistadores repo) {
        this.repo = repo;
    }

    @Override
    public void check(LoginRequestDTO dto) {
        repo.findByDni(dto.getDni())
                .orElseThrow(() -> DomainException.of(
                        LoginError.USER_NOT_FOUND.key,
                        LoginError.USER_NOT_FOUND.status
                ));
    }
}