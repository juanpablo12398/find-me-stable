package edu.utn.proyecto.common.validation.concreta.avistador.rules;
import edu.utn.proyecto.avistador.exception.AvistadorError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class AvistadorRenaperMatchRule implements Rule<AvistadorRequestDTO> {

    private final IRepoDeRenaper renaper;

    public AvistadorRenaperMatchRule(IRepoDeRenaper renaper) {
        this.renaper = renaper;
    }

    @Override
    public void check(AvistadorRequestDTO dto) {
        RenaperPersonaEntity persona = renaper.findByDni(dto.getDni())
                .orElse(null);

        if (!dto.getNombre().equals(persona.getNombre()) || !dto.getApellido().equals(persona.getApellido())) {
            throw DomainException.of(
                    AvistadorError.PADRON_NO_MATCH.key,
                    AvistadorError.PADRON_NO_MATCH.status
            );
        }
    }
}