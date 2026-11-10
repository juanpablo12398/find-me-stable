package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;

import edu.utn.proyecto.avistamiento.exception.AvistamientoError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class WktPolygonFormatRule implements Rule<String> {

    private static final int MAX_WKT_LENGTH = 10_000;

    @Override
    public void check(String wkt) {
        if (wkt == null || wkt.isBlank()) {
            throw DomainException.of(
                    AvistamientoError.WKT_REQUIRED.key,
                    AvistamientoError.WKT_REQUIRED.status);
        }

        String normalized = wkt.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("POLYGON((") || !normalized.endsWith("))")) {
            throw DomainException.of(
                    AvistamientoError.WKT_INVALID.key,
                    AvistamientoError.WKT_INVALID.status);
        }

        if (wkt.length() > MAX_WKT_LENGTH) {
            throw DomainException.of(
                    AvistamientoError.WKT_TOO_LARGE.key,
                    AvistamientoError.WKT_TOO_LARGE.status);
        }
    }
}
