package edu.utn.proyecto.applicacion.validation.file;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FileUploadPolicy implements Validator<FileUploadDTO> {

    private final List<Rule<FileUploadDTO>> rules;

    public FileUploadPolicy(List<Rule<FileUploadDTO>> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(FileUploadDTO dto) {
        // Spring inyecta automáticamente por @Order:
        // 1. FileNotEmptyRule
        // 2. FileSizeRule
        // 3. FileExtensionRule
        // 4. FileMagicBytesRule
        for (var rule : rules) {
            rule.check(dto);
        }
    }
}