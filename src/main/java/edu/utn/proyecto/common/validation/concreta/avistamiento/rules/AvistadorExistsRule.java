package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;
import edu.utn.proyecto.avistamiento.exception.AvistamientoError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Order(3)
public class AvistadorExistsRule implements Rule<AvistamientoRequestDTO> {

    private final IRepoDeAvistadores repo;

    public AvistadorExistsRule(IRepoDeAvistadores repo) {
        this.repo = repo;
    }

    @Override
    public void check(AvistamientoRequestDTO dto) {
        if (dto.getAvistadorId() == null ||
                !repo.findById(UUID.fromString(dto.getAvistadorId())).isPresent()) {
            throw DomainException.of(
                    AvistamientoError.AVISTADOR_NOT_FOUND.key,
                    AvistamientoError.AVISTADOR_NOT_FOUND.status
            );
        }
    }
}
