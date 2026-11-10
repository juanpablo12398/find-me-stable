package edu.utn.proyecto.integration;

import edu.utn.proyecto.applicacion.usecase.file.DeleteFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.DownloadFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.GetFileMimeTypeUseCase;
import edu.utn.proyecto.applicacion.usecase.file.UploadFileUseCase;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import edu.utn.proyecto.file.exception.FileError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import edu.utn.proyecto.security.SecurityContextHelper;
import edu.utn.proyecto.security.SignedUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FileValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadFileUseCase uploadFileUseCase;

    @MockBean
    private DownloadFileUseCase downloadFileUseCase;

    @MockBean
    private GetFileMimeTypeUseCase getFileMimeTypeUseCase;

    @MockBean
    private DeleteFileUseCase deleteFileUseCase;

    @MockBean
    private SecurityContextHelper securityHelper;

    @MockBean
    private SignedUrlService signedUrlService;

    @Test
    void uploadFile_tipoNoPermitido_retorna400ConKeyEsperada() throws Exception {
        when(securityHelper.isAuthenticated()).thenReturn(true);
        when(uploadFileUseCase.execute(any(FileUploadDTO.class)))
                .thenThrow(DomainException.of(FileError.TYPE_NOT_ALLOWED.key, FileError.TYPE_NOT_ALLOWED.status));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malware.exe",
                "application/octet-stream",
                "MZ-fake-binary".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(file)
                        .param("personId", UUID.randomUUID().toString())
                        .param("entityType", EntityType.AVISTAMIENTO.name())
                        .param("category", FileCategory.DOC.name()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").value("file.type_not_allowed"));
    }

    @Test
    void uploadFile_archivoVacio_retorna400ConKeyEsperada() throws Exception {
        when(securityHelper.isAuthenticated()).thenReturn(true);
        when(uploadFileUseCase.execute(any(FileUploadDTO.class)))
                .thenThrow(DomainException.of(FileError.EMPTY.key, FileError.EMPTY.status));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);

        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(file)
                        .param("personId", UUID.randomUUID().toString())
                        .param("entityType", EntityType.DESAPARECIDO.name())
                        .param("category", FileCategory.PHOTO.name()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").value("file.empty"));
    }

    @Test
    void uploadFile_contenidoMalicioso_retorna400ConKeyEsperada() throws Exception {
        when(securityHelper.isAuthenticated()).thenReturn(true);
        when(uploadFileUseCase.execute(any(FileUploadDTO.class)))
                .thenThrow(DomainException.of(FileError.MAGIC_BYTES_MISMATCH.key, FileError.MAGIC_BYTES_MISMATCH.status));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.jpg",
                "image/jpeg",
                "MZ-fake-binary".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(file)
                        .param("personId", UUID.randomUUID().toString())
                        .param("entityType", EntityType.AVISTAMIENTO.name())
                        .param("category", FileCategory.PHOTO.name()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").value("file.magic_bytes_mismatch"));
    }
}