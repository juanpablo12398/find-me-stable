package edu.utn.proyecto.domain.model.concreta;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import java.time.LocalDateTime;
import java.util.UUID;

public class FileMetadata {
    private UUID id;
    private UUID personId;
    private EntityType entityType;
    private String storageKey;
    private String mimeType;
    private Long sizeBytes;
    private String sha256;
    private FileCategory category;
    private String originalName;
    private LocalDateTime createdAt;

    public FileMetadata() {
        this.createdAt = LocalDateTime.now();
        this.category = FileCategory.PHOTO;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPersonId() { return personId; }
    public void setPersonId(UUID personId) { this.personId = personId; }
    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getSha256() { return sha256; }
    public void setSha256(String sha256) { this.sha256 = sha256; }
    public FileCategory getCategory() { return category; }
    public void setCategory(FileCategory category) { this.category = category; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}