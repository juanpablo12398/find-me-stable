package edu.utn.proyecto.common.validation.concreta.file.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class FileMagicBytesRuleTest {

    private FileMagicBytesRule rule;

    @BeforeEach
    void setUp() {
        rule = new FileMagicBytesRule();
    }

    @Test
    void check_magicBytesMatch_ok() throws IOException {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenReturn(new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x01, 0x02 });

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_magicBytesMismatch_lanzaExcepcion() throws IOException {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenReturn(new byte[] { 0x00, 0x00, 0x00, 0x01, 0x02 });

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.magic_bytes_mismatch");
    }

    @Test
    void check_fileCorruptedLength_lanzaExcepcion() throws IOException {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenReturn(new byte[] { (byte) 0xFF });

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.corrupted");
    }
}
