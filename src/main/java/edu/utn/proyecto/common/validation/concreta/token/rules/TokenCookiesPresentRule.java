package edu.utn.proyecto.common.validation.concreta.token.rules;
import edu.utn.proyecto.auth.exception.TokenError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import jakarta.servlet.http.Cookie;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TokenCookiesPresentRule implements Rule<TokenRequestDTO> {
    @Override
    public void check(TokenRequestDTO dto) {
        Cookie[] cookies = dto.getRequest().getCookies();
        if (cookies == null) {
            throw DomainException.of(
                    TokenError.NOT_FOUND.key,
                    TokenError.NOT_FOUND.status);
        }
    }
}
