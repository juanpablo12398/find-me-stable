package edu.utn.proyecto.avistador.exception;
import org.springframework.http.HttpStatus;

public enum AvistadorError {
    PADRON_NOT_FOUND("avistador.renaper.notfound", HttpStatus.NOT_FOUND),
    PADRON_NO_MATCH("avistador.renaper.nomatch", HttpStatus.UNPROCESSABLE_ENTITY),
    DNI_DUP("avistador.dni.duplicado", HttpStatus.CONFLICT),
    UNDERAGE("avistador.edad.menor", HttpStatus.UNPROCESSABLE_ENTITY);

    public final String key;
    public final HttpStatus status;
    AvistadorError(String key, HttpStatus status) {
        this.key = key; this.status = status;
    }
}


