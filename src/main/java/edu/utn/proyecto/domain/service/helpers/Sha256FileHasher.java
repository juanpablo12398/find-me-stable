package edu.utn.proyecto.domain.service.helpers;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.service.helpers.abstraccion.FileHasher;
import edu.utn.proyecto.file.exception.FileError;
import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class Sha256FileHasher implements FileHasher {

    @Override
    public String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw DomainException.of(FileError.STORAGE_ERROR.key, FileError.STORAGE_ERROR.status);
        }
    }
}