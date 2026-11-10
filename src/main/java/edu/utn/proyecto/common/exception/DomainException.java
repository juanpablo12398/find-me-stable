package edu.utn.proyecto.common.exception;
import org.springframework.http.HttpStatus;

public class DomainException extends RuntimeException {
    private final String key;
    private final HttpStatus status;

    private DomainException(String key, HttpStatus status, String message) {
        super(message);
        this.key = key;
        this.status = status;
    }

    public static DomainException of(String key, HttpStatus status, String message) {
        return new DomainException(key, status, message);
    }

    public static DomainException of(String key, HttpStatus status) {
        return new DomainException(key, status, key);  // Fallback = key
    }

    public String getKey() { return key; }
    public HttpStatus getStatus() { return status; }
}