package edu.utn.proyecto.common.validation.concreta.token.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TokenValidRuleTest {

    private TokenValidRule rule;
    private final String SECRET = "CAMBIA_ESTE_SECRETO_LARGO_DEMO_32+_CHARS";

    @BeforeEach
    void setUp() {
        rule = new TokenValidRule();
        ReflectionTestUtils.setField(rule, "SECRET", SECRET);
    }

    @Test
    void check_validToken_ok() {
        String token = Jwts.builder()
                .setSubject("123")
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        TokenRequestDTO dto = new TokenRequestDTO();
        dto.setToken(token);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
        assertNotNull(dto.getClaims());
    }

    @Test
    void check_invalidToken_lanzaExcepcion() {
        TokenRequestDTO dto = new TokenRequestDTO();
        dto.setToken("invalid.token.here");

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Token inválido");
    }

    @Test
    void check_expiredToken_lanzaExcepcion() {
        String token = Jwts.builder()
                .setSubject("123")
                .setExpiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        TokenRequestDTO dto = new TokenRequestDTO();
        dto.setToken(token);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Tu sesión ha expirado");
    }
}
