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

class FileNotEmptyRuleTest {

    private FileNotEmptyRule rule;

    @BeforeEach
    void setUp() {
        rule = new FileNotEmptyRule();
    }

    @Test
    void check_fileNotEmpty_ok() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_fileEmpty_lanzaExcepcion() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(true);

        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(multipartFile);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.empty");
    }

    @Test
    void check_fileNull_lanzaExcepcion() {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFile(null);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("file.empty");
    }
}
