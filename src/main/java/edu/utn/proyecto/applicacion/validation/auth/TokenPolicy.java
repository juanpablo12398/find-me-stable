package edu.utn.proyecto.applicacion.validation.auth;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TokenPolicy implements Validator<TokenRequestDTO> {

    private final List<Rule<TokenRequestDTO>> rules;

    public TokenPolicy(List<Rule<TokenRequestDTO>> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(TokenRequestDTO dto) {
        for (var rule : rules) {
            rule.check(dto);
        }
    }
}