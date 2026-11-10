package edu.utn.proyecto.common.validation.concreta.file.rules;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.file.exception.FileError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@Order(3)
public class FileExtensionRule implements Rule<FileUploadDTO> {

    private static final Map<String, List<String>> ALLOWED_TYPES = Map.of(
            "jpg",  List.of("image/jpeg"),
            "jpeg", List.of("image/jpeg"),
            "png",  List.of("image/png"),
            "webp", List.of("image/webp"),
            "pdf",  List.of("application/pdf")
    );

    @Override
    public void check(FileUploadDTO dto) {
        String originalName = dto.getFile().getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
        String contentType = dto.getFile().getContentType();

        if (dto.getFile().getOriginalFilename() == null || !originalName.contains(".")) {
            throw DomainException.of(
                    FileError.NO_EXTENSION.key,
                    FileError.NO_EXTENSION.status);
        }

        if (!ALLOWED_TYPES.containsKey(extension)) {
            throw DomainException.of(
                    FileError.TYPE_NOT_ALLOWED.key,
                    FileError.TYPE_NOT_ALLOWED.status);
        }

        if (contentType == null || !ALLOWED_TYPES.get(extension).contains(contentType)) {
            throw DomainException.of(
                    FileError.MIME_MISMATCH.key,
                    FileError.MIME_MISMATCH.status);
        }
    }
}