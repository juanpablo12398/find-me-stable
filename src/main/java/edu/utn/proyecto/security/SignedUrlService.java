package edu.utn.proyecto.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Genera URLs firmadas con expiración para acceso a archivos.
 * Flujo:
 *   1. Frontend pide foto → backend genera URL firmada con expiración
 *   2. Frontend usa esa URL → backend valida firma + expiración
 *   3. Si el link se comparte, expira en N segundos
 * URL formato: /api/v1/files/{fileId}?expires={timestamp}&sig={hmac}
 */
@Service
public class SignedUrlService {

    private static final String HMAC_ALGO = "HmacSHA256";

    @Value("${security.signed-url.secret:${jwt.secret:CAMBIA_ESTE_SECRETO}}")
    private String secret;

    @Value("${security.signed-url.ttl-seconds:120}")
    private long ttlSeconds;

    public String generateSignedPath(UUID fileId) {
        long expires = Instant.now().plusSeconds(ttlSeconds).getEpochSecond();
        String payload = fileId.toString() + ":" + expires;
        String signature = computeHmac(payload);

        return "/api/v1/files/" + fileId + "?expires=" + expires + "&sig=" + signature;
    }

    public boolean isValid(UUID fileId, long expires, String signature) {
        // Verificar expiración
        if (Instant.now().getEpochSecond() > expires) {
            return false;
        }

        // Verificar firma
        String payload = fileId.toString() + ":" + expires;
        String expected = computeHmac(payload);
        return constantTimeEquals(expected, signature);
    }

    private String computeHmac(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmac);
        } catch (Exception e) {
            throw new RuntimeException("Error generando HMAC", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}