package edu.utn.proyecto.applicacion.usecase.file;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.service.abstraccion.IFilestorageservice;
import edu.utn.proyecto.file.exception.FileError;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GetFileMetadataUseCase {

    private final IFilestorageservice fileStorageService;

    public GetFileMetadataUseCase(IFilestorageservice fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public FileMetadata execute(UUID fileId) {
        return fileStorageService.getMetadata(fileId)
                .orElseThrow(() -> DomainException.of(FileError.NOT_FOUND.key, FileError.NOT_FOUND.status));
    }
}
