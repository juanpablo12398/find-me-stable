package edu.utn.proyecto.applicacion.mappers;
import edu.utn.proyecto.applicacion.usecase.file.commands.FileUploadCommand;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeFileBinary;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class FileMapper {

    private final IRepoDeFileBinary binaryStorage;

    public FileMapper(IRepoDeFileBinary binaryStorage) {
        this.binaryStorage = binaryStorage;
    }

    public FileUploadCommand toCommand(FileUploadDTO dto) {
        var file = dto.getFile();
        byte[] bytes = binaryStorage.readBytes(file);

        return new FileUploadCommand(
                bytes,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                dto.getPersonId(),
                dto.getEntityType(),
                dto.getCategory()
        );
    }

    public FileMetadata buildMetadata(UUID fileId, FileUploadCommand cmd, String storageKey, String sha256) {
        FileMetadata metadata = new FileMetadata();
        metadata.setId(fileId);
        metadata.setPersonId(cmd.getPersonId());
        metadata.setEntityType(cmd.getEntityType());
        metadata.setStorageKey(storageKey);
        metadata.setMimeType(cmd.getMimeType());
        metadata.setSizeBytes(cmd.getSizeBytes());
        metadata.setSha256(sha256);
        metadata.setCategory(cmd.getCategory());
        metadata.setOriginalName(cmd.getOriginalName());
        return metadata;
    }
}