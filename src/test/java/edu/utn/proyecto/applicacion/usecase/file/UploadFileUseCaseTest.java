package edu.utn.proyecto.applicacion.usecase.file;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.service.abstraccion.IFilestorageservice;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadFileUseCaseTest {

    @Mock
    private IFilestorageservice fileStorageService;

    @InjectMocks
    private UploadFileUseCase useCase;

    @Test
    void execute_retornaMetadataConCamposCorrectos() {
        FileUploadDTO dto = new FileUploadDTO();
        FileMetadata metadata = new FileMetadata();
        UUID fileId = UUID.randomUUID();
        metadata.setId(fileId);
        metadata.setMimeType("image/jpeg");
        metadata.setSizeBytes(204800L);
        when(fileStorageService.upload(dto)).thenReturn(metadata);

        FileMetadata result = useCase.execute(dto);

        assertThat(result.getId()).isEqualTo(fileId);
        assertThat(result.getMimeType()).isEqualTo("image/jpeg");
        assertThat(result.getSizeBytes()).isEqualTo(204800L);
    }

    @Test
    void execute_propagaDomainExceptionCuandoTipoDeArchivoEsInvalido() {
        FileUploadDTO dto = new FileUploadDTO();
        DomainException tipoInvalido = DomainException.of("file.tipo.invalido", HttpStatus.UNPROCESSABLE_ENTITY);
        when(fileStorageService.upload(dto)).thenThrow(tipoInvalido);

        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey())
                        .isEqualTo("file.tipo.invalido"));
    }

    @Test
    void execute_retornaMetadataConMimeTypePng() {
        FileUploadDTO dto = new FileUploadDTO();
        FileMetadata metadata = new FileMetadata();
        metadata.setId(UUID.randomUUID());
        metadata.setMimeType("image/png");
        metadata.setSizeBytes(512000L);
        when(fileStorageService.upload(dto)).thenReturn(metadata);

        FileMetadata result = useCase.execute(dto);

        assertThat(result.getMimeType()).isEqualTo("image/png");
        assertThat(result.getSizeBytes()).isEqualTo(512000L);
    }
}
