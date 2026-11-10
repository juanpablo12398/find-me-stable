package edu.utn.proyecto.common.validation.concreta.file.rules;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.file.exception.FileError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class FileNotEmptyRule implements Rule<FileUploadDTO> {

    @Override
    public void check(FileUploadDTO dto) {
        if (dto.getFile() == null || dto.getFile().isEmpty()) {
            throw DomainException.of(
                    FileError.EMPTY.key,
                    FileError.EMPTY.status
            );
        }
    }
}