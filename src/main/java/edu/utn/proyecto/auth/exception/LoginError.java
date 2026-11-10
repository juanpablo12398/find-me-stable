package edu.utn.proyecto.auth.exception;
import org.springframework.http.HttpStatus;

public enum LoginError {
    RENAPER_NOT_FOUND("auth.login.renaper.notfound", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("auth.login.user.notfound", HttpStatus.NOT_FOUND),
    EMAIL_MISMATCH("auth.login.email.mismatch", HttpStatus.UNPROCESSABLE_ENTITY);

    public final String key;
    public final HttpStatus status;

    LoginError(String key, HttpStatus status) {
        this.key = key;
        this.status = status;
    }
}
