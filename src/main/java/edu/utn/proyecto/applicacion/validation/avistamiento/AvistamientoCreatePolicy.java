package edu.utn.proyecto.applicacion.validation.avistamiento;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AvistamientoCreatePolicy implements Validator<AvistamientoRequestDTO> {

    private final IRepoDeAvistadores repoAvistadores;
    private final IRepoDeDesaparecidos repoDesaparecidos;
    private final List<Rule<AvistamientoRequestDTO>> rules;

    public AvistamientoCreatePolicy(
            IRepoDeAvistadores repoAvistadores,
            IRepoDeDesaparecidos repoDesaparecidos,
            List<Rule<AvistamientoRequestDTO>> rules) {
        this.repoAvistadores = repoAvistadores;
        this.repoDesaparecidos = repoDesaparecidos;
        this.rules = rules;
    }

    @Override
    public void validate(AvistamientoRequestDTO dto) {

        // Spring inyecta:
        // - AvistamientoCoordsRule
        // - AvistamientoDescripcionRule
        // - AvistadorExistsRule
        // - DesaparecidoExistsRule
        for (var rule : rules) {
            rule.check(dto);
        }
    }
}
