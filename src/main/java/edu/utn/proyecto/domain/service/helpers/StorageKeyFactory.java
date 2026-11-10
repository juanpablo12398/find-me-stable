package edu.utn.proyecto.domain.service.helpers;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import edu.utn.proyecto.file.exception.FileError;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class StorageKeyFactory {

    public String build(EntityType entityType,
                        UUID personId,
                        FileCategory category,
                        UUID fileId,
                        String originalName) {

        String ext = extractExtensionOrFail(originalName);

        return String.join("/",
                entityType.name().toLowerCase(),
                personId.toString(),
                category.name().toLowerCase(),
                fileId + "." + ext
        );
    }

    private String extractExtensionOrFail(String filename) {
        if (filename == null) {
            throw DomainException.of(
                    FileError.NO_EXTENSION.key,
                    FileError.NO_EXTENSION.status);
        }
        int dot = filename.lastIndexOf('.');
        if (dot <= 0 || dot == filename.length() - 1) {
            throw DomainException.of(
                    FileError.NO_EXTENSION.key,
                    FileError.NO_EXTENSION.status);
        }
        return filename.substring(dot + 1).toLowerCase();
    }
}