package edu.utn.proyecto.common.validation.concreta.token.rules;
import edu.utn.proyecto.auth.exception.TokenError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class TokenSubjectPresentRule implements Rule<TokenRequestDTO> {
    @Override
    public void check(TokenRequestDTO dto) {
        String dni = dto.getClaims().getSubject();
        if (dni == null || dni.isBlank()) {
            throw DomainException.of(
                    TokenError.DATA_MISSING.key,
                    TokenError.DATA_MISSING.status);
        }
    }
}
