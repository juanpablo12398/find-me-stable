package edu.utn.proyecto.auth.exception;
import org.springframework.http.HttpStatus;

public enum AuthError {
    UNAUTHORIZED("auth.unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("auth.forbidden", HttpStatus.FORBIDDEN);

    public final String key;
    public final HttpStatus status;

    AuthError(String key, HttpStatus status) {
        this.key = key;
        this.status = status;
    }
}
