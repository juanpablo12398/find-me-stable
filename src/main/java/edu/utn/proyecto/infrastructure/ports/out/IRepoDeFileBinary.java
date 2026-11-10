package edu.utn.proyecto.infrastructure.ports.out;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface IRepoDeFileBinary {
    byte[] readBytes(MultipartFile file);
    void saveAtomically(String storageKey, UUID fileId, byte[] bytes);
    Resource loadAsResource(String storageKey);
    void deleteIfExists(String storageKey);
}