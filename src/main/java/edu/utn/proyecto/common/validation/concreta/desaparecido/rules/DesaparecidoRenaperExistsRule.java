package edu.utn.proyecto.common.validation.concreta.desaparecido.rules;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.desaparecido.exception.DesaparecidoError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DesaparecidoRenaperExistsRule implements Rule<DesaparecidoRequestDTO> {

    private final IRepoDeRenaper renaper;

    public DesaparecidoRenaperExistsRule(IRepoDeRenaper renaper) {
        this.renaper = renaper;
    }

    @Override
    public void check(DesaparecidoRequestDTO dto) {
        // Validar que el DNI exista en el padrón
        if (dto.getDni() == null ||
                !renaper.findByDni(dto.getDni()).isPresent()) {
            throw DomainException.of(
                    DesaparecidoError.RENAPER_NOT_FOUND.key,
                    DesaparecidoError.RENAPER_NOT_FOUND.status
            );
        }
    }
}