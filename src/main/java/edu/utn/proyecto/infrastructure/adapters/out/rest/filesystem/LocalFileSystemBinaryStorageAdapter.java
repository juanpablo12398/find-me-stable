package edu.utn.proyecto.infrastructure.adapters.out.rest.filesystem;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.file.exception.FileError;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeFileBinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Component
public class LocalFileSystemBinaryStorageAdapter implements IRepoDeFileBinary {

    private final Path rootPath;

    public LocalFileSystemBinaryStorageAdapter(
            @Value("${findme.storage.root:./storage}") String storageRoot) {
        this.rootPath = Paths.get(storageRoot).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw DomainException.of(
                    FileError.STORAGE_ERROR.key,
                    FileError.STORAGE_ERROR.status);
        }
    }

    @Override
    public byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw DomainException.of(
                    FileError.STORAGE_ERROR.key,
                    FileError.STORAGE_ERROR.status);
        }
    }

    @Override
    public void saveAtomically(String storageKey, UUID fileId, byte[] bytes) {
        Path finalPath = safeResolve(storageKey);

        try {
            Files.createDirectories(finalPath.getParent());

            Path tmpPath = safeResolve("tmp/" + fileId + ".tmp");
            Files.createDirectories(tmpPath.getParent());
            Files.write(tmpPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            try {
                Files.move(tmpPath, finalPath, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmpPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw DomainException.of(
                    FileError.STORAGE_ERROR.key,
                    FileError.STORAGE_ERROR.status);
        }
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        Path path = safeResolve(storageKey);
        if (!Files.exists(path)) {
            throw DomainException.of(
                    FileError.NOT_FOUND.key,
                    FileError.NOT_FOUND.status);
        }
        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw DomainException.of(
                    FileError.STORAGE_ERROR.key,
                    FileError.STORAGE_ERROR.status);
        }
    }

    @Override
    public void deleteIfExists(String storageKey) {
        Path path = safeResolve(storageKey);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw DomainException.of(
                    FileError.STORAGE_ERROR.key,
                    FileError.STORAGE_ERROR.status);
        }
    }

    private Path safeResolve(String storageKey) {
        Path resolved = rootPath.resolve(storageKey).normalize();
        if (!resolved.startsWith(rootPath)) {
            throw DomainException.of(
                    FileError.PATH_TRAVERSAL.key,
                    FileError.PATH_TRAVERSAL.status);
        }
        // Verificación adicional con toRealPath() para detectar symlinks
        // que podrían apuntar fuera del directorio base.
        // Solo si el archivo ya existe (toRealPath() falla si no existe).
        if (Files.exists(resolved)) {
            try {
                Path realResolved = resolved.toRealPath();
                Path realRoot = rootPath.toRealPath();
                if (!realResolved.startsWith(realRoot)) {
                    throw DomainException.of(
                            FileError.PATH_TRAVERSAL.key,
                            FileError.PATH_TRAVERSAL.status);
                }
            } catch (IOException e) {
                throw DomainException.of(
                        FileError.PATH_TRAVERSAL.key,
                        FileError.PATH_TRAVERSAL.status);
            }
        }
        return resolved;
    }
}
