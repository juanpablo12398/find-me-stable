package edu.utn.proyecto.common.validation.concreta.desaparecido.rules;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.desaparecido.exception.DesaparecidoError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class DesaparecidoLengthRule implements Rule<DesaparecidoRequestDTO> {
    @Override public void check(DesaparecidoRequestDTO dto) {
        if (dto.getDescripcion() == null || dto.getDescripcion().length() < 20) {
            throw DomainException.of(
                    DesaparecidoError.DESC_SHORT.key,
                    DesaparecidoError.DESC_SHORT.status
            );
        }
    }
}
