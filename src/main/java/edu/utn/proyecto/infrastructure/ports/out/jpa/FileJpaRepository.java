package edu.utn.proyecto.infrastructure.ports.out.jpa;

import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileJpaRepository extends JpaRepository<FileEntity, UUID> {

    List<FileEntity> findByPersonIdAndEntityType(UUID personId, EntityType entityType);

    boolean existsBySha256(String sha256);
}