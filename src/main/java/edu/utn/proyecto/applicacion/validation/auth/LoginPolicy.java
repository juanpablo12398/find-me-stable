package edu.utn.proyecto.applicacion.validation.auth;
import edu.utn.proyecto.auth.exception.AuthError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LoginPolicy implements Validator<LoginRequestDTO> {

    private final IRepoDeRenaper renaper;
    private final IRepoDeAvistadores repo;
    private final List<Rule<LoginRequestDTO>> rules;

    public LoginPolicy(IRepoDeRenaper renaper,
                       IRepoDeAvistadores repo,
                       List<Rule<LoginRequestDTO>> rules) {
        this.renaper = renaper;
        this.repo = repo;
        this.rules = rules;
    }

    @Override
    public void validate(LoginRequestDTO dto) {
        for (var rule : rules) {
            rule.check(dto);
        }
    }
}