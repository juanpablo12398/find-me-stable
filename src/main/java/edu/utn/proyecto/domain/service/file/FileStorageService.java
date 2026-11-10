package edu.utn.proyecto.domain.service.file;
import edu.utn.proyecto.applicacion.usecase.file.commands.FileUploadCommand;
import edu.utn.proyecto.applicacion.mappers.FileMapper;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.service.abstraccion.IFilestorageservice;
import edu.utn.proyecto.domain.service.helpers.ExifStripper;
import edu.utn.proyecto.domain.service.helpers.StorageKeyFactory;
import edu.utn.proyecto.domain.service.helpers.abstraccion.FileHasher;
import edu.utn.proyecto.file.exception.FileError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeFile;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeFileBinary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService implements IFilestorageservice {

    private final IRepoDeFile fileRepo;
    private final IRepoDeFileBinary binaryStorage;
    private final StorageKeyFactory keyFactory;
    private final FileHasher hasher;
    private final FileMapper fileMapper;
    private final ExifStripper exifStripper;
    private final Validator<FileUploadDTO> uploadPolicy;

    public FileStorageService(IRepoDeFile fileRepo,
                              IRepoDeFileBinary binaryStorage,
                              StorageKeyFactory keyFactory,
                              FileHasher hasher,
                              FileMapper fileMapper,
                              ExifStripper exifStripper,
                              Validator<FileUploadDTO> uploadPolicy) {
        this.fileRepo = fileRepo;
        this.binaryStorage = binaryStorage;
        this.keyFactory = keyFactory;
        this.hasher = hasher;
        this.fileMapper = fileMapper;
        this.exifStripper = exifStripper;
        this.uploadPolicy = uploadPolicy;
    }

    @Transactional
    public FileMetadata upload(FileUploadDTO dto) {
        uploadPolicy.validate(dto);
        FileUploadCommand cmd = fileMapper.toCommand(dto);
        UUID fileId = UUID.randomUUID();
        byte[] cleanBytes = exifStripper.strip(cmd.getBytes(), cmd.getMimeType());
        String sha256 = hasher.sha256(cleanBytes);
        String storageKey = keyFactory.build(
                cmd.getEntityType(), cmd.getPersonId(),
                cmd.getCategory(), fileId, cmd.getOriginalName()
        );
        binaryStorage.saveAtomically(storageKey, fileId, cleanBytes);
        FileMetadata metadata = fileMapper.buildMetadata(fileId, cmd, storageKey, sha256);
        metadata.setSizeBytes((long) cleanBytes.length);
        return fileRepo.save(metadata);
    }

    public Resource download(UUID fileId) {
        FileMetadata meta = findOrFail(fileId);
        return binaryStorage.loadAsResource(meta.getStorageKey());
    }

    public Optional<FileMetadata> getMetadata(UUID fileId) {
        return fileRepo.findById(fileId);
    }

    public String getMimeType(UUID fileId) {
        return fileRepo.findById(fileId)
                .map(FileMetadata::getMimeType)
                .orElse("application/octet-stream");
    }

    @Transactional
    public void delete(UUID fileId) {
        FileMetadata meta = findOrFail(fileId);
        binaryStorage.deleteIfExists(meta.getStorageKey());
        fileRepo.deleteById(fileId);
    }

    private FileMetadata findOrFail(UUID fileId) {
        return fileRepo.findById(fileId)
                .orElseThrow(() -> DomainException.of(
                        FileError.NOT_FOUND.key,
                        FileError.NOT_FOUND.status));
    }
}