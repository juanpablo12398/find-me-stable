package edu.utn.proyecto.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class JwtVerifier {

    private final byte[] key;

    public JwtVerifier(@Value("${jwt.secret:CAMBIA_ESTE_SECRETO_LARGO_DEMO_32+_CHARS}") String secret) {
        this.key = secret.getBytes(StandardCharsets.UTF_8);
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}