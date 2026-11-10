package edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers;

import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.FileEntity;
import org.springframework.stereotype.Component;

@Component
public class FilePersistenceMapper {

    public FileEntity toEntity(FileMetadata d) {
        FileEntity e = new FileEntity();
        e.setId(d.getId());
        e.setPersonId(d.getPersonId());
        e.setEntityType(d.getEntityType());
        e.setStorageKey(d.getStorageKey());
        e.setMimeType(d.getMimeType());
        e.setSizeBytes(d.getSizeBytes());
        e.setSha256(d.getSha256());
        e.setCategory(d.getCategory());
        e.setOriginalName(d.getOriginalName());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }

    public FileMetadata toDomain(FileEntity e) {
        FileMetadata d = new FileMetadata();
        d.setId(e.getId());
        d.setPersonId(e.getPersonId());
        d.setEntityType(e.getEntityType());
        d.setStorageKey(e.getStorageKey());
        d.setMimeType(e.getMimeType());
        d.setSizeBytes(e.getSizeBytes());
        d.setSha256(e.getSha256());
        d.setCategory(e.getCategory());
        d.setOriginalName(e.getOriginalName());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }
}