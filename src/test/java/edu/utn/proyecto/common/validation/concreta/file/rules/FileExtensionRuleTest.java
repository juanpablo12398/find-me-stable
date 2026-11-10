package edu.utn.proyecto.common.validation.concreta.file.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class FileExtensionRuleTest {

    private FileExtensionRule rule;

    @BeforeEach
    void setUp() {
        rule = new FileExtensionRule();
    }

    @Test
    void check_extensionValid_ok() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_noExtension_lanzaExcepcion() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("image");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.no_extension");
    }

    @Test
    void check_extensionNotAllowed_lanzaExcepcion() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("file.exe");
        when(multipartFile.getContentType()).thenReturn("application/octet-stream");

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.type_not_allowed");
    }

    @Test
    void check_mimeMismatch_lanzaExcepcion() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.mime_mismatch");
    }
}
