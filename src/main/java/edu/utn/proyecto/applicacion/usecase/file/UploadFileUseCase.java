package edu.utn.proyecto.applicacion.usecase.file;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.service.abstraccion.IFilestorageservice;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.springframework.stereotype.Service;

@Service
public class UploadFileUseCase {

    private final IFilestorageservice fileStorageService;

    public UploadFileUseCase(IFilestorageservice fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public FileMetadata execute(FileUploadDTO dto) {
        return fileStorageService.upload(dto);
    }
}