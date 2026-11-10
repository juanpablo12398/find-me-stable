package edu.utn.proyecto.common.validation.concreta.avistador.rules;
import edu.utn.proyecto.avistador.exception.AvistadorError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class AvistadorDniDuplicadoRule implements Rule<AvistadorRequestDTO> {

    private final IRepoDeAvistadores repo;

    public AvistadorDniDuplicadoRule(IRepoDeAvistadores repo) {
        this.repo = repo;
    }

    @Override
    public void check(AvistadorRequestDTO dto) {
        // Validar que el DNI no esté duplicado
        if (dto.getDni() != null && repo.existsByDni(dto.getDni())) {
            throw DomainException.of(
                    AvistadorError.DNI_DUP.key,
                    AvistadorError.DNI_DUP.status
            );
        }
    }
}
