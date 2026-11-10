package edu.utn.proyecto.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtVerifierTest {

    private JwtVerifier verifier;
    private final String SECRET = "ESTE_ES_UN_SECRETO_DE_PRUEBA_MUY_LARGO_Y_SEGURO_32+CHARS";
    private final byte[] key = SECRET.getBytes(StandardCharsets.UTF_8);

    @BeforeEach
    void setUp() {
        verifier = new JwtVerifier(SECRET);
    }

    private String generateValidToken(String subject, long expirationSeconds) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);
        return Jwts.builder()
                .setSubject(subject)
                .claim("rol", "ADMIN")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(key))
                .compact();
    }

    @Test
    void testParseValidToken() {
        String token = generateValidToken("12345678", 3600);

        Claims claims = verifier.parse(token);

        assertNotNull(claims);
        assertEquals("12345678", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol"));
    }

    @Test
    void testParseInvalidSignature() {
        String token = generateValidToken("12345678", 3600);
        // Modificar el token para invalidar la firma
        String invalidToken = token.substring(0, token.length() - 2) + "xx";

        assertThrows(Exception.class, () -> verifier.parse(invalidToken),
                "Debe tirar excepción si la firma es inválida");
    }

    @Test
    void testParseExpiredToken() {
        // Token que expiró hace 1 segundo
        String expiredToken = generateValidToken("12345678", -1);

        assertThrows(Exception.class, () -> verifier.parse(expiredToken),
                "Debe tirar excepción si el token está expirado");
    }
}
