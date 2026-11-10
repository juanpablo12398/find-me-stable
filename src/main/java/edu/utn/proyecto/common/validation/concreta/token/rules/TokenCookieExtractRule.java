package edu.utn.proyecto.common.validation.concreta.token.rules;
import edu.utn.proyecto.auth.exception.TokenError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import edu.utn.proyecto.security.TokenService;
import jakarta.servlet.http.Cookie;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
@Order(2)
public class TokenCookieExtractRule implements Rule<TokenRequestDTO> {
    @Override
    public void check(TokenRequestDTO dto) {
        Cookie[] cookies = dto.getRequest().getCookies();
        String token = Arrays.stream(cookies)
                .filter(c -> TokenService.COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> DomainException.of(
                        TokenError.NOT_FOUND.key,
                        TokenError.NOT_FOUND.status));
        dto.setToken(token);
    }
}
