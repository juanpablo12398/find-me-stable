package edu.utn.proyecto.applicacion.validation.desaparecido;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.desaparecido.exception.DesaparecidoError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DesaparecidoCreatePolicy implements Validator<DesaparecidoRequestDTO> {

    private final IRepoDeDesaparecidos repo;
    private final List<Rule<DesaparecidoRequestDTO>> rules;

    public DesaparecidoCreatePolicy(IRepoDeDesaparecidos repo,
                                    List<Rule<DesaparecidoRequestDTO>> rules) {
        this.repo = repo;
        this.rules = rules;
    }

    @Override
    public void validate(DesaparecidoRequestDTO dto) {

        // Spring inyecta:
        // - DesaparecidoLengthRule
        // - DesaparecidoDniDuplicadoRule
        for (var rule : rules) {
            rule.check(dto);
        }
    }
}
