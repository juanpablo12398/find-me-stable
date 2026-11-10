package edu.utn.proyecto.common.validation.concreta.token.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenClaimsPresentRuleTest {

    private TokenClaimsPresentRule rule;

    @BeforeEach
    void setUp() {
        rule = new TokenClaimsPresentRule();
    }

    @Test
    void check_claimsPresent_ok() {
        TokenRequestDTO dto = new TokenRequestDTO();
        Claims claims = Jwts.claims();
        dto.setClaims(claims);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_claimsMissing_lanzaExcepcion() {
        TokenRequestDTO dto = new TokenRequestDTO();
        dto.setClaims(null);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("auth.token.data.missing");
    }
}
