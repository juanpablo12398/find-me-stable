package edu.utn.proyecto.infrastructure.adapters.out.persistence.postgresql;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers.FilePersistenceMapper;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeFile;
import edu.utn.proyecto.infrastructure.ports.out.jpa.FileJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class FileRepository implements IRepoDeFile {

    private final FileJpaRepository jpa;
    private final FilePersistenceMapper mapper;

    public FileRepository(FileJpaRepository jpa, FilePersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public FileMetadata save(FileMetadata metadata) {
        var saved = jpa.save(mapper.toEntity(metadata));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<FileMetadata> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
