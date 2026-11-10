package edu.utn.proyecto.domain.service.abstraccion;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.springframework.core.io.Resource;
import java.util.Optional;
import java.util.UUID;

public interface IFilestorageservice {

    FileMetadata upload(FileUploadDTO dto);
    Resource download(UUID fileId);
    Optional<FileMetadata> getMetadata(UUID fileId);
    String getMimeType(UUID fileId);
    void delete(UUID fileId);
}