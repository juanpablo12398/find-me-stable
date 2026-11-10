package edu.utn.proyecto.infrastructure.ports.out;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import java.util.Optional;
import java.util.UUID;

public interface IRepoDeFile {
    FileMetadata save(FileMetadata metadata);
    Optional<FileMetadata> findById(UUID id);
    void deleteById(UUID id);
}