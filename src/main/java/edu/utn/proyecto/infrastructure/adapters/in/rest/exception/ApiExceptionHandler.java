package edu.utn.proyecto.infrastructure.adapters.in.rest.exception;
import edu.utn.proyecto.common.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final MessageSource messages;

    public ApiExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail handle(DomainException ex) {
        String message;
        try {
            message = messages.getMessage(
                    ex.getKey(), null, ex.getMessage(),
                    LocaleContextHolder.getLocale()
            );
        } catch (NoSuchMessageException e) {
            // Si falta la key i18n, NO rompas el handler (esto suele ser la causa del 500 fantasma)
            log.warn("Falta message key='{}' para DomainException. Fallback a ex.getMessage()", ex.getKey(), e);
            message = ex.getMessage();
        }

        log.warn("DomainException key={} status={} msg={}", ex.getKey(), ex.getStatus(), message);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatus(), message);
        pd.setProperty("key", ex.getKey());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handle(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("key", "validation.failed");
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handle(HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es válido o tiene formato incorrecto."
        );
        pd.setProperty("key", "request.body.invalid");
        return pd;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ProblemDetail handle(MissingServletRequestParameterException ex) {
        String detail = "Falta el parámetro requerido: " + ex.getParameterName();

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("key", "request.param.missing");
        return pd;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handle(MethodArgumentTypeMismatchException ex) {
        String detail = "El parámetro '" + ex.getName() + "' tiene un formato inválido.";

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("key", "request.param.type_mismatch");
        return pd;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail handle(MaxUploadSizeExceededException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "El archivo supera el tamaño máximo permitido."
        );
        pd.setProperty("key", "file.too_large");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleGeneric(Exception ex) {
        // ESTO es lo que hoy te falta: ver el stacktrace real
        log.error("Unhandled exception", ex);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor."
        );
        pd.setProperty("key", "server.internal_error");
        return pd;
    }
}