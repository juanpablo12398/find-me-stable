package edu.utn.proyecto.common.validation.concreta.avistador.rules;
import edu.utn.proyecto.avistador.exception.AvistadorError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AvistadorRenaperExistsRule implements Rule<AvistadorRequestDTO> {

    private final IRepoDeRenaper renaper;

    public AvistadorRenaperExistsRule(IRepoDeRenaper renaper) {
        this.renaper = renaper;
    }

    @Override
    public void check(AvistadorRequestDTO dto) {
        // Validar que el DNI exista en el padrón
        if (dto.getDni() == null ||
                !renaper.findByDni(dto.getDni()).isPresent()) {
            throw DomainException.of(
                    AvistadorError.PADRON_NOT_FOUND.key,
                    AvistadorError.PADRON_NOT_FOUND.status
            );
        }
    }
}
