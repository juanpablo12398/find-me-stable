package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;
import edu.utn.proyecto.avistamiento.exception.AvistamientoError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AvistamientoDescripcionRule implements Rule<AvistamientoRequestDTO> {

    private static final int MIN_LENGTH = 20;

    @Override
    public void check(AvistamientoRequestDTO dto) {
        if (dto.getDescripcion() == null ||
                dto.getDescripcion().trim().length() < MIN_LENGTH) {
            throw DomainException.of(
                    AvistamientoError.DESC_SHORT.key,
                    AvistamientoError.DESC_SHORT.status
            );
        }
    }
}
