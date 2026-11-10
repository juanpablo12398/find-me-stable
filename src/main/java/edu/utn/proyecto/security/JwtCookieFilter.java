package edu.utn.proyecto.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JwtCookieFilter extends OncePerRequestFilter {

    private final JwtVerifier jwtVerifier;

    public JwtCookieFilter(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String path = req.getRequestURI();
        if (path.startsWith("/js/") || path.startsWith("/css/") ||
                path.startsWith("/images/") || path.startsWith("/assets/") ||
                path.equals("/") || path.equals("/index.html") ||
                path.endsWith(".js") || path.endsWith(".css") ||
                path.endsWith(".html") || path.endsWith(".png") ||
                path.endsWith(".jpg") || path.endsWith(".ico")) {
            chain.doFilter(req, res);
            return;
        }

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (TokenService.COOKIE_NAME.equals(c.getName())) {
                    try {
                        var claims = jwtVerifier.parse(c.getValue());
                        String dni = claims.getSubject();
                        if (dni != null) {
                            String rolStr = claims.get("rol", String.class);
                            if (rolStr == null || rolStr.isBlank()) {
                                rolStr = "USUARIO";
                            }
                            String authority = "ROLE_" + rolStr;
                            String idStr = claims.get("id", String.class);
                            UUID userId = null;
                            if (idStr != null && !idStr.isBlank()) {
                                try {
                                    userId = UUID.fromString(idStr);
                                } catch (IllegalArgumentException ignored) {}
                            }
                            final UUID finalUserId = userId;
                            var auth = new AbstractAuthenticationToken(
                                    List.of(new SimpleGrantedAuthority(authority))) {
                                @Override public Object getCredentials() { return c.getValue(); }
                                @Override public Object getPrincipal() { return dni; }
                                @Override public Object getDetails() { return finalUserId; }
                            };
                            auth.setAuthenticated(true);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    } catch (Exception ignored) {}
                    break;
                }
            }
        }
        chain.doFilter(req, res);
    }
}