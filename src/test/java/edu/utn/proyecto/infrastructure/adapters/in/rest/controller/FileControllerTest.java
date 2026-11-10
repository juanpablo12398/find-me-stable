package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.usecase.file.DeleteFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.DownloadFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.GetFileMimeTypeUseCase;
import edu.utn.proyecto.applicacion.usecase.file.UploadFileUseCase;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import edu.utn.proyecto.security.SecurityContextHelper;
import edu.utn.proyecto.security.SignedUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileControllerTest {

    @Mock
    private UploadFileUseCase uploadFileUseCase;
    @Mock
    private DownloadFileUseCase downloadFileUseCase;
    @Mock
    private GetFileMimeTypeUseCase getFileMimeTypeUseCase;
    @Mock
    private DeleteFileUseCase deleteFileUseCase;
    @Mock
    private SecurityContextHelper securityHelper;
    @Mock
    private SignedUrlService signedUrlService;

    @InjectMocks
    private FileController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void upload_ok() {
        MultipartFile file = mock(MultipartFile.class);
        UUID personId = UUID.randomUUID();
        EntityType entityType = EntityType.DESAPARECIDO;
        FileCategory category = FileCategory.PHOTO;

        when(securityHelper.isAuthenticated()).thenReturn(true);
        FileMetadata metadata = new FileMetadata();
        metadata.setId(UUID.randomUUID());
        metadata.setMimeType("image/jpeg");
        metadata.setSizeBytes(100L);

        when(uploadFileUseCase.execute(any(FileUploadDTO.class))).thenReturn(metadata);
        when(signedUrlService.generateSignedPath(metadata.getId())).thenReturn("/signed/url");

        ResponseEntity<Map<String, Object>> res = controller.upload(file, personId, entityType, category);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("/signed/url", res.getBody().get("downloadUrl"));
    }

    @Test
    void upload_unauthenticated_returns401() {
        MultipartFile file = mock(MultipartFile.class);
        when(securityHelper.isAuthenticated()).thenReturn(false);

        ResponseEntity<Map<String, Object>> res = controller.upload(file, UUID.randomUUID(), EntityType.DESAPARECIDO,
                FileCategory.PHOTO);

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    void download_ok_withSignature() {
        UUID id = UUID.randomUUID();
        when(signedUrlService.isValid(id, 100L, "sig")).thenReturn(true);
        when(downloadFileUseCase.execute(id)).thenReturn(new ByteArrayResource(new byte[] { 1, 2, 3 }));
        when(getFileMimeTypeUseCase.execute(id)).thenReturn("image/jpeg");

        ResponseEntity<Resource> res = controller.download(id, 100L, "sig");

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("image/jpeg", res.getHeaders().getContentType().toString());
    }

    @Test
    void download_invalidSignature_returns403() {
        UUID id = UUID.randomUUID();
        when(signedUrlService.isValid(id, 100L, "sig")).thenReturn(false);

        ResponseEntity<Resource> res = controller.download(id, 100L, "sig");

        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    void delete_admin_ok() {
        UUID id = UUID.randomUUID();
        when(securityHelper.isAdmin()).thenReturn(true);

        ResponseEntity<Void> res = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(deleteFileUseCase).execute(id);
    }

    @Test
    void delete_notAdmin_returns403() {
        UUID id = UUID.randomUUID();
        when(securityHelper.isAdmin()).thenReturn(false);

        ResponseEntity<Void> res = controller.delete(id);

        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }
}
