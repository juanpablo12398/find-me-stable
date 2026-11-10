package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public class FileUploadDTO {
    private MultipartFile file;
    private UUID personId;
    private EntityType entityType;
    private FileCategory category;

    public FileUploadDTO() {}

    public FileUploadDTO(MultipartFile file, UUID personId, EntityType entityType, FileCategory category) {
        this.file = file;
        this.personId = personId;
        this.entityType = entityType;
        this.category = category;
    }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
    public UUID getPersonId() { return personId; }
    public void setPersonId(UUID personId) { this.personId = personId; }
    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }
    public FileCategory getCategory() { return category; }
    public void setCategory(FileCategory category) { this.category = category; }
}