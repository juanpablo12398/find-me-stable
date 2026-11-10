package edu.utn.proyecto.integration;

import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import edu.utn.proyecto.security.SecurityContextHelper;
import edu.utn.proyecto.security.SignedUrlService;
import edu.utn.proyecto.applicacion.usecase.file.UploadFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.DownloadFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.GetFileMimeTypeUseCase;
import edu.utn.proyecto.applicacion.usecase.file.DeleteFileUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Desactivar filtros de seguridad si aplica
@ActiveProfiles("test")
class FileIntegrationTest {

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
    void uploadFile_Authenticated_Return201() throws Exception {
        when(securityHelper.isAuthenticated()).thenReturn(true);

        UUID personId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image content".getBytes());

        FileMetadata metadata = new FileMetadata();
        metadata.setId(fileId);
        metadata.setMimeType("image/jpeg");
        metadata.setSizeBytes((long) file.getSize());

        when(uploadFileUseCase.execute(any(FileUploadDTO.class))).thenReturn(metadata);
        when(signedUrlService.generateSignedPath(fileId)).thenReturn("/api/files/" + fileId + "?expires=123&sig=abc");

        mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .param("personId", personId.toString())
                .param("entityType", EntityType.DESAPARECIDO.name())
                .param("category", FileCategory.PHOTO.name()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileId").value(fileId.toString()))
                .andExpect(jsonPath("$.downloadUrl").value("/api/files/" + fileId + "?expires=123&sig=abc"));
    }

    @Test
    void uploadFile_Unauthenticated_Return401() throws Exception {
        when(securityHelper.isAuthenticated()).thenReturn(false);

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image content".getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .param("personId", UUID.randomUUID().toString())
                .param("entityType", EntityType.AVISTAMIENTO.name())
                .param("category", FileCategory.DOC.name()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void downloadFile_ValidSignature_Return200() throws Exception {
        UUID fileId = UUID.randomUUID();
        Long expires = 123456L;
        String sig = "valid_sig";

        when(signedUrlService.isValid(fileId, expires, sig)).thenReturn(true);
        when(downloadFileUseCase.execute(fileId)).thenReturn(new ByteArrayResource("image data".getBytes()));
        when(getFileMimeTypeUseCase.execute(fileId)).thenReturn("image/jpeg");

        mockMvc.perform(get("/api/files/{fileId}", fileId)
                .param("expires", expires.toString())
                .param("sig", sig))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG));
    }

    @Test
    void deleteFile_AsAdmin_Return204() throws Exception {
        UUID fileId = UUID.randomUUID();
        when(securityHelper.isAdmin()).thenReturn(true);

        mockMvc.perform(delete("/api/files/{fileId}", fileId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteFile_AsNonAdmin_Return403() throws Exception {
        UUID fileId = UUID.randomUUID();
        when(securityHelper.isAdmin()).thenReturn(false);

        mockMvc.perform(delete("/api/files/{fileId}", fileId))
                .andExpect(status().isForbidden());
    }
}
