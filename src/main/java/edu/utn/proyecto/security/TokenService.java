package edu.utn.proyecto.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {

    public static final String COOKIE_NAME = "FM_TOKEN";

    @Value("${jwt.secret:CAMBIA_ESTE_SECRETO_LARGO_DEMO_32+_CHARS}")
    private String SECRET;

    @Value("${security.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${security.cookie.max-age-seconds:604800}")
    private int cookieMaxAge; // default 7 días

    public String generate(String avistadorId, String dni, String email, String nombre, String rol) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(cookieMaxAge);

        return Jwts.builder()
                .setSubject(dni)
                .claim("id", avistadorId)
                .claim("email", email)
                .claim("name", nombre)
                .claim("rol", rol)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public void writeCookie(HttpServletResponse resp, String jwt) {
        Cookie c = new Cookie(COOKIE_NAME, jwt);
        c.setHttpOnly(true);          // No accesible desde JS (previene XSS)
        c.setPath("/");
        c.setMaxAge(cookieMaxAge);
        c.setSecure(cookieSecure);     // true en prod (solo HTTPS)
        c.setAttribute("SameSite", "Lax"); // Protección CSRF
        resp.addCookie(c);
    }

    public void clearCookie(HttpServletResponse resp) {
        Cookie c = new Cookie(COOKIE_NAME, "");
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(0);
        c.setSecure(cookieSecure);
        c.setAttribute("SameSite", "Lax");
        resp.addCookie(c);
    }

    public String secret() {
        return SECRET;
    }
}