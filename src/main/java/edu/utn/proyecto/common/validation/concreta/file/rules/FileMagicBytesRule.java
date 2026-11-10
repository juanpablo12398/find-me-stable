package edu.utn.proyecto.common.validation.concreta.file.rules;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.file.exception.FileError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
@Order(4)
public class FileMagicBytesRule implements Rule<FileUploadDTO> {

    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
            "image/jpeg",       new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/png",        new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},
            "image/webp",       new byte[]{0x52, 0x49, 0x46, 0x46},
            "application/pdf",  new byte[]{0x25, 0x50, 0x44, 0x46}
    );

    @Override
    public void check(FileUploadDTO dto) {
        String contentType = dto.getFile().getContentType();
        if (contentType == null) return;

        byte[] expected = MAGIC_BYTES.get(contentType);
        if (expected == null) return;

        try {
            byte[] fileBytes = dto.getFile().getBytes();

            if (fileBytes.length < expected.length) {
                throw DomainException.of(
                        FileError.FILE_CORRUPTED.key,
                        FileError.FILE_CORRUPTED.status);
            }

            for (int i = 0; i < expected.length; i++) {
                if (fileBytes[i] != expected[i]) {
                    throw DomainException.of(
                            FileError.MAGIC_BYTES_MISMATCH.key,
                            FileError.MAGIC_BYTES_MISMATCH.status);
                }
            }
        } catch (IOException e) {
            throw DomainException.of(
                    FileError.STORAGE_ERROR.key,
                    FileError.STORAGE_ERROR.status);
        }
    }
}