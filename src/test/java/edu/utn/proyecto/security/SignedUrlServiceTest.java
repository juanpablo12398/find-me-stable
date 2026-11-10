package edu.utn.proyecto.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para SignedUrlService.
 * Cubre: generación de URL firmada, validación correcta, firma inválida,
 * URL expirada y path manipulado (distinto fileId).
 */
class SignedUrlServiceTest {

    private SignedUrlService service;
    private static final String SECRET = "este-es-un-secreto-de-test-32-bytes-largo";

    @BeforeEach
    void setUp() {
        service = new SignedUrlService();
        ReflectionTestUtils.setField(service, "secret", SECRET);
        ReflectionTestUtils.setField(service, "ttlSeconds", 120L);
    }

    @Test
    void generateSignedPath_generaUrlConFormatoEsperado() {
        UUID fileId = UUID.randomUUID();

        String path = service.generateSignedPath(fileId);

        assertThat(path)
                .startsWith("/api/v1/files/" + fileId)
                .contains("?expires=")
                .contains("&sig=");
    }

    @Test
    void isValid_urlFirmadaValida_retornaTrue() {
        UUID fileId = UUID.randomUUID();
        String path = service.generateSignedPath(fileId);

        // Extraer expires y sig del path generado
        long expires = extraerExpires(path);
        String sig = extraerSig(path);

        assertThat(service.isValid(fileId, expires, sig)).isTrue();
    }

    @Test
    void isValid_firmaInvalida_retornaFalse() {
        UUID fileId = UUID.randomUUID();
        String path = service.generateSignedPath(fileId);

        long expires = extraerExpires(path);
        String sigCorrupta = "firma-completamente-invalida";

        assertThat(service.isValid(fileId, expires, sigCorrupta)).isFalse();
    }

    @Test
    void isValid_urlExpirada_retornaFalse() {
        UUID fileId = UUID.randomUUID();
        // Expires en el pasado
        long expiresEnPasado = Instant.now().minusSeconds(10).getEpochSecond();

        // Construir manualmente la firma para la URL expirada
        // (el método generateSignedPath siempre pone expiracion futura)
        // Usamos isValid directamente con un expires pasado
        assertThat(service.isValid(fileId, expiresEnPasado, "cualquier-firma")).isFalse();
    }

    @Test
    void isValid_pathManipulado_distinto_fileId_retornaFalse() {
        UUID fileIdOriginal = UUID.randomUUID();
        UUID fileIdManipulado = UUID.randomUUID();

        String path = service.generateSignedPath(fileIdOriginal);
        long expires = extraerExpires(path);
        String sig = extraerSig(path);

        // La firma fue generada para fileIdOriginal, no debe ser válida para fileIdManipulado
        assertThat(service.isValid(fileIdManipulado, expires, sig)).isFalse();
    }

    @Test
    void isValid_firmaModificadaEnUnCaracter_retornaFalse() {
        UUID fileId = UUID.randomUUID();
        String path = service.generateSignedPath(fileId);

        long expires = extraerExpires(path);
        String sig = extraerSig(path);

        // Modificar el último carácter de la firma
        char lastChar = sig.charAt(sig.length() - 1);
        char replacement = (lastChar == 'a') ? 'b' : 'a';
        String sigModificada = sig.substring(0, sig.length() - 1) + replacement;

        assertThat(service.isValid(fileId, expires, sigModificada)).isFalse();
    }

    @Test
    void generateSignedPath_distintoFileId_distintoSig() {
        UUID fileId1 = UUID.randomUUID();
        UUID fileId2 = UUID.randomUUID();

        String path1 = service.generateSignedPath(fileId1);
        String path2 = service.generateSignedPath(fileId2);

        String sig1 = extraerSig(path1);
        String sig2 = extraerSig(path2);

        assertThat(sig1).isNotEqualTo(sig2);
    }

    // ── Helpers de extracción ──────────────────────────────────────────────

    private long extraerExpires(String path) {
        // path: /api/v1/files/{uuid}?expires={ts}&sig={sig}
        int start = path.indexOf("?expires=") + "?expires=".length();
        int end = path.indexOf("&sig=");
        return Long.parseLong(path.substring(start, end));
    }

    private String extraerSig(String path) {
        int start = path.indexOf("&sig=") + "&sig=".length();
        return path.substring(start);
    }
}
