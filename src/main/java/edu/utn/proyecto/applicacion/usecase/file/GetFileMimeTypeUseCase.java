package edu.utn.proyecto.applicacion.usecase.file;
import edu.utn.proyecto.domain.service.abstraccion.IFilestorageservice;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GetFileMimeTypeUseCase {

    private final IFilestorageservice fileStorageService;

    public GetFileMimeTypeUseCase(IFilestorageservice fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String execute(UUID fileId) {
        return fileStorageService.getMimeType(fileId);
    }
}
