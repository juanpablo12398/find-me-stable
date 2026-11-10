package edu.utn.proyecto.desaparecido.exception;
import org.springframework.http.HttpStatus;

public enum DesaparecidoError {
    RENAPER_NOT_FOUND("desaparecido.renaper.notfound", HttpStatus.NOT_FOUND),
    DNI_DUP("desaparecido.dni.duplicado", HttpStatus.CONFLICT),
    DESC_SHORT("desaparecido.descripcion.corta", HttpStatus.UNPROCESSABLE_ENTITY),
    CONTACTO_REQUERIDO("desaparecido.contacto.requerido", HttpStatus.UNPROCESSABLE_ENTITY),
    CONTACTO_EMAIL_INVALIDO("desaparecido.contacto.email.invalido", HttpStatus.UNPROCESSABLE_ENTITY),
    CONTACTO_TELEFONO_INVALIDO("desaparecido.contacto.telefono.invalido", HttpStatus.UNPROCESSABLE_ENTITY);

    public final String key;
    public final HttpStatus status;

    DesaparecidoError(String key, HttpStatus status) {
        this.key = key;
        this.status = status;
    }
}
