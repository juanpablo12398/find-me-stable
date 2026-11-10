package edu.utn.proyecto.common.validation.concreta.token.rules;
import edu.utn.proyecto.auth.exception.TokenError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
@Order(4)
public class TokenValidRule implements Rule<TokenRequestDTO> {

    @Value("${jwt.secret:CAMBIA_ESTE_SECRETO_LARGO_DEMO_32+_CHARS}")
    private String SECRET;

    @Override
    public void check(TokenRequestDTO dto) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(dto.getToken())
                    .getBody();
            dto.setClaims(claims);
        } catch (ExpiredJwtException e) {
            throw DomainException.of(
                    TokenError.EXPIRED.key,
                    TokenError.EXPIRED.status,
                    "Tu sesión ha expirado");
        } catch (SignatureException | MalformedJwtException e) {
            throw DomainException.of(
                    TokenError.INVALID.key,
                    TokenError.INVALID.status,
                    "Token inválido");
        } catch (Exception e) {
            throw DomainException.of(
                    TokenError.INVALID.key,
                    TokenError.INVALID.status,
                    "Error al validar token");
        }
    }
}