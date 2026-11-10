package edu.utn.proyecto.auth.exception;
import org.springframework.http.HttpStatus;

public enum TokenError {
    NOT_FOUND("auth.token.notfound", HttpStatus.UNAUTHORIZED),
    EXPIRED("auth.token.expired", HttpStatus.UNAUTHORIZED),
    INVALID("auth.token.invalid", HttpStatus.UNAUTHORIZED),
    MALFORMED("auth.token.malformed", HttpStatus.UNAUTHORIZED),
    DATA_MISSING("auth.token.data.missing", HttpStatus.UNAUTHORIZED);

    public final String key;
    public final HttpStatus status;

    TokenError(String key, HttpStatus status) {
        this.key = key;
        this.status = status;
    }
}
