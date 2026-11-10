package edu.utn.proyecto.applicacion.usecase.file;
import edu.utn.proyecto.domain.service.file.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class DownloadFileUseCase {

    private final FileStorageService fileStorageService;

    public DownloadFileUseCase(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public Resource execute(UUID fileId) {
        return fileStorageService.download(fileId);
    }
}