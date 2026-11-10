package edu.utn.proyecto.applicacion.usecase.file.commands;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import java.util.UUID;

public class FileUploadCommand {

    private final byte[] bytes;
    private final String originalName;
    private final String mimeType;
    private final long sizeBytes;
    private final UUID personId;
    private final EntityType entityType;
    private final FileCategory category;

    public FileUploadCommand(byte[] bytes,
                             String originalName,
                             String mimeType,
                             long sizeBytes,
                             UUID personId,
                             EntityType entityType,
                             FileCategory category) {
        this.bytes = bytes;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.personId = personId;
        this.entityType = entityType;
        this.category = category;
    }

    public byte[] getBytes() { return bytes; }
    public String getOriginalName() { return originalName; }
    public String getMimeType() { return mimeType; }
    public long getSizeBytes() { return sizeBytes; }
    public UUID getPersonId() { return personId; }
    public EntityType getEntityType() { return entityType; }
    public FileCategory getCategory() { return category; }
}
