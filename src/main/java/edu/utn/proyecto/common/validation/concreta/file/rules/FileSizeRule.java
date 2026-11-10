package edu.utn.proyecto.common.validation.concreta.file.rules;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.file.exception.FileError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class FileSizeRule implements Rule<FileUploadDTO> {

    @Value("${findme.storage.max-size-mb:10}")
    private int maxSizeMb;

    @Override
    public void check(FileUploadDTO dto) {
        long maxBytes = (long) maxSizeMb * 1024 * 1024;
        if (dto.getFile().getSize() > maxBytes) {
            throw DomainException.of(
                    FileError.TOO_LARGE.key,
                    FileError.TOO_LARGE.status
            );
        }
    }
}