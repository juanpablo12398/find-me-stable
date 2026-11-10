package edu.utn.proyecto.applicacion.mappers;

import edu.utn.proyecto.applicacion.usecase.file.commands.FileUploadCommand;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeFileBinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileMapperTest {

    @Mock
    private IRepoDeFileBinary binaryStorage;

    @InjectMocks
    private FileMapper fileMapper;

    @Test
    void toCommand_mapsCorrectly() {
        FileUploadDTO dto = new FileUploadDTO();
        UUID personId = UUID.randomUUID();
        dto.setPersonId(personId);
        dto.setEntityType(EntityType.DESAPARECIDO);
        dto.setCategory(FileCategory.PHOTO);

        MultipartFile file = mock(MultipartFile.class);
        dto.setFile(file);

        byte[] expectedBytes = "test data".getBytes();
        when(binaryStorage.readBytes(file)).thenReturn(expectedBytes);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(9L);

        FileUploadCommand cmd = fileMapper.toCommand(dto);

        assertThat(cmd.getBytes()).isEqualTo(expectedBytes);
        assertThat(cmd.getOriginalName()).isEqualTo("test.jpg");
        assertThat(cmd.getMimeType()).isEqualTo("image/jpeg");
        assertThat(cmd.getSizeBytes()).isEqualTo(9L);
        assertThat(cmd.getPersonId()).isEqualTo(personId);
        assertThat(cmd.getEntityType()).isEqualTo(EntityType.DESAPARECIDO);
        assertThat(cmd.getCategory()).isEqualTo(FileCategory.PHOTO);
    }

    @Test
    void buildMetadata_mapsCorrectly() {
        UUID fileId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();
        
        FileUploadCommand cmd = new FileUploadCommand(
                new byte[0],
                "photo.png",
                "image/png",
                1024L,
                personId,
                EntityType.AVISTAMIENTO,
                FileCategory.DOC
        );
        String storageKey = "path/to/storage.png";
        String sha256 = "hash123";

        FileMetadata metadata = fileMapper.buildMetadata(fileId, cmd, storageKey, sha256);

        assertThat(metadata.getId()).isEqualTo(fileId);
        assertThat(metadata.getPersonId()).isEqualTo(personId);
        assertThat(metadata.getEntityType()).isEqualTo(EntityType.AVISTAMIENTO);
        assertThat(metadata.getCategory()).isEqualTo(FileCategory.DOC);
        assertThat(metadata.getStorageKey()).isEqualTo(storageKey);
        assertThat(metadata.getSha256()).isEqualTo(sha256);
        assertThat(metadata.getOriginalName()).isEqualTo("photo.png");
        assertThat(metadata.getMimeType()).isEqualTo("image/png");
        assertThat(metadata.getSizeBytes()).isEqualTo(1024L);
    }
}
