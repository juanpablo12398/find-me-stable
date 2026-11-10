package edu.utn.proyecto.file.exception;
import org.springframework.http.HttpStatus;

public enum FileError {
    EMPTY("file.empty", HttpStatus.BAD_REQUEST),
    TOO_LARGE("file.too_large", HttpStatus.BAD_REQUEST),
    NO_EXTENSION("file.no_extension", HttpStatus.BAD_REQUEST),
    TYPE_NOT_ALLOWED("file.type_not_allowed", HttpStatus.BAD_REQUEST),
    MIME_MISMATCH("file.mime_mismatch", HttpStatus.BAD_REQUEST),
    MAGIC_BYTES_MISMATCH("file.magic_bytes_mismatch", HttpStatus.BAD_REQUEST),
    FILE_CORRUPTED("file.corrupted", HttpStatus.BAD_REQUEST),
    NOT_FOUND("file.not_found", HttpStatus.NOT_FOUND),
    STORAGE_ERROR("file.storage_error", HttpStatus.INTERNAL_SERVER_ERROR),
    PATH_TRAVERSAL("file.path_traversal", HttpStatus.FORBIDDEN);

    public final String key;
    public final HttpStatus status;

    FileError(String key, HttpStatus status) {
        this.key = key;
        this.status = status;
    }
}