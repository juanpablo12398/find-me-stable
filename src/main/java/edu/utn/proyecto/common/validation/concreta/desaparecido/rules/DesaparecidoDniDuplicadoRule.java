package edu.utn.proyecto.common.validation.concreta.desaparecido.rules;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.desaparecido.exception.DesaparecidoError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class DesaparecidoDniDuplicadoRule implements Rule<DesaparecidoRequestDTO> {

    private final IRepoDeDesaparecidos repo;

    public DesaparecidoDniDuplicadoRule(IRepoDeDesaparecidos repo) {
        this.repo = repo;
    }

    @Override
    public void check(DesaparecidoRequestDTO dto) {
        // Validar que el DNI no esté duplicado
        if (dto.getDni() != null && repo.existsByDni(dto.getDni())) {
            throw DomainException.of(
                    DesaparecidoError.DNI_DUP.key,
                    DesaparecidoError.DNI_DUP.status
            );
        }
    }
}
