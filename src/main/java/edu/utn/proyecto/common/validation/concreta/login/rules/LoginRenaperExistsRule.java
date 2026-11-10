package edu.utn.proyecto.common.validation.concreta.login.rules;
import edu.utn.proyecto.auth.exception.LoginError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class LoginRenaperExistsRule implements Rule<LoginRequestDTO> {

    private final IRepoDeRenaper renaper;

    public LoginRenaperExistsRule(IRepoDeRenaper renaper) {
        this.renaper = renaper;
    }

    @Override
    public void check(LoginRequestDTO dto) {
        renaper.findByDni(dto.getDni())
                .orElseThrow(() -> DomainException.of(
                        LoginError.RENAPER_NOT_FOUND.key,
                        LoginError.RENAPER_NOT_FOUND.status
                ));
    }
}

