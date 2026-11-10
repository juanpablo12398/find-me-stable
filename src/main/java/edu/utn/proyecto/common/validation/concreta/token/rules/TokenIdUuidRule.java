package edu.utn.proyecto.common.validation.concreta.token.rules;
import edu.utn.proyecto.auth.exception.TokenError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Order(8)
public class TokenIdUuidRule implements Rule<TokenRequestDTO> {
    @Override
    public void check(TokenRequestDTO dto) {
        String idStr = dto.getClaims().get("id", String.class);
        try {
            UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            throw DomainException.of(
                    TokenError.MALFORMED.key,
                    TokenError.MALFORMED.status,
                    "ID de usuario inválido en el token");
        }
    }
}