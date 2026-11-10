package edu.utn.proyecto.security.encrypt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Genera blind indexes con HMAC-SHA256 para buscar campos cifrados
 * sin necesidad de descifrarlos.
 * Uso: WHERE dni_hmac = ? en vez de WHERE dni = ?
 * La clave HMAC es distinta a la clave de cifrado (defense in depth).
 * Si se compromete la DB, no se puede buscar sin la clave HMAC.
 */
@Component
public class BlindIndexer {

    private static final String HMAC_ALGO = "HmacSHA256";

    private final SecretKeySpec hmacKey;

    public BlindIndexer(
            @Value("${security.hmac.key}") String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        this.hmacKey = new SecretKeySpec(keyBytes, HMAC_ALGO);
    }

    public String index(String value) {
        if (value == null) return null;

        try {
            // Normalizar: lowercase + trim para consistencia
            String normalized = value.toLowerCase().trim();

            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(hmacKey);
            byte[] hmac = mac.doFinal(normalized.getBytes(StandardCharsets.UTF_8));

            // Hex encoding (64 chars para SHA-256)
            StringBuilder hex = new StringBuilder();
            for (byte b : hmac) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generando blind index", e);
        }
    }
}