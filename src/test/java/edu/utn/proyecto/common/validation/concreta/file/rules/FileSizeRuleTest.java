package edu.utn.proyecto.common.validation.concreta.file.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class FileSizeRuleTest {

    private FileSizeRule rule;

    @BeforeEach
    void setUp() {
        rule = new FileSizeRule();
        ReflectionTestUtils.setField(rule, "maxSizeMb", 10);
    }

    @Test
    void check_sizeLessThanMax_ok() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getSize()).thenReturn(5 * 1024 * 1024L); // 5MB

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_sizeGreaterThanMax_lanzaExcepcion() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getSize()).thenReturn(15 * 1024 * 1024L); // 15MB

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.too_large");
    }
}
