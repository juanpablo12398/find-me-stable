package edu.utn.proyecto.common.validation.concreta.token.rules;
import edu.utn.proyecto.auth.exception.TokenError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(7)
public class TokenIdPresentRule implements Rule<TokenRequestDTO> {
    @Override
    public void check(TokenRequestDTO dto) {
        String idStr = dto.getClaims().get("id", String.class);
        if (idStr == null || idStr.isBlank()) {
            throw DomainException.of(
                    TokenError.DATA_MISSING.key,
                    TokenError.DATA_MISSING.status);
        }
    }
}
