package edu.utn.proyecto.applicacion.usecase.file;
import edu.utn.proyecto.domain.service.abstraccion.IFilestorageservice;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteFileUseCase {

    private final IFilestorageservice fileStorageService;

    public DeleteFileUseCase(IFilestorageservice fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public void execute(UUID fileId) {
        fileStorageService.delete(fileId);
    }
}