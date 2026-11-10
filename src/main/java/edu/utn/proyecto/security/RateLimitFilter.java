package edu.utn.proyecto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro de rate limiting por IP con límites diferenciados por ruta:
 *  - Login:    5 intentos/minuto   (POST /api/auth/login)
 *  - Registro: 3 intentos/hora     (POST /api/avistadores)
 *
 * En caso de superar el límite responde 429 Too Many Requests con
 * el header Retry-After indicando cuántos segundos debe esperar el cliente.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${security.rate-limit.login.max-requests:5}")
    private int loginMaxRequests;

    @Value("${security.rate-limit.login.window-seconds:60}")
    private int loginWindowSeconds;

    @Value("${security.rate-limit.registro.max-requests:3}")
    private int registroMaxRequests;

    @Value("${security.rate-limit.registro.window-seconds:3600}")
    private int registroWindowSeconds;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean isLoginPath = "POST".equals(method) &&
                (path.equals("/api/auth/login") || path.equals("/api/v1/auth/login"));
        boolean isRegistroPath = "POST".equals(method) &&
                (path.equals("/api/avistadores") || path.equals("/api/v1/avistadores"));

        return !(isLoginPath || isRegistroPath);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String clientIp = resolveClientIp(request);
        String path = request.getRequestURI();

        boolean isRegistro = path.contains("avistadores");
        String scope = isRegistro ? "registro" : "login";
        int maxReqs = isRegistro ? registroMaxRequests : loginMaxRequests;
        int windowSecs = isRegistro ? registroWindowSeconds : loginWindowSeconds;
        String key = clientIp + ":" + scope;

        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());

        if (!bucket.tryConsume(maxReqs, windowSecs)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(windowSecs));
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"key\":\"rate.limit.exceeded\",\"detail\":\"Demasiadas solicitudes. Intenta de nuevo en "
                            + windowSecs + " segundos.\"}"
            );
            return;
        }

        response.setHeader("X-RateLimit-Remaining",
                String.valueOf(Math.max(0, maxReqs - bucket.getCount())));

        chain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class Bucket {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        boolean tryConsume(int maxRequests, int windowSeconds) {
            long now = System.currentTimeMillis();
            long windowMs = windowSeconds * 1000L;

            if (now - windowStart > windowMs) {
                synchronized (this) {
                    if (now - windowStart > windowMs) {
                        count.set(0);
                        windowStart = now;
                    }
                }
            }

            return count.incrementAndGet() <= maxRequests;
        }

        int getCount() {
            return count.get();
        }
    }
}
