package edu.utn.proyecto.common.validation.concreta.token.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import edu.utn.proyecto.security.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TokenCookieExtractRuleTest {

    private TokenCookieExtractRule rule;

    @BeforeEach
    void setUp() {
        rule = new TokenCookieExtractRule();
    }

    @Test
    void check_cookieFound_ok() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie(TokenService.COOKIE_NAME, "mock_token_value") };
        when(request.getCookies()).thenReturn(cookies);

        TokenRequestDTO dto = new TokenRequestDTO();
        dto.setRequest(request);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
        assertEquals("mock_token_value", dto.getToken());
    }

    @Test
    void check_cookieNotFound_lanzaExcepcion() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("other_cookie", "value") };
        when(request.getCookies()).thenReturn(cookies);

        TokenRequestDTO dto = new TokenRequestDTO();
        dto.setRequest(request);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("auth.token.notfound");
    }
}
