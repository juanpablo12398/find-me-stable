package edu.utn.proyecto.applicacion.validation.avistador;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvistadorRegistrationPolicy implements Validator<AvistadorRequestDTO> {

    private final IRepoDeRenaper renaper;
    private final IRepoDeAvistadores repo;
    private final List<Rule<AvistadorRequestDTO>> rules;

    public AvistadorRegistrationPolicy(IRepoDeRenaper renaper,
                                       IRepoDeAvistadores repo,
                                       List<Rule<AvistadorRequestDTO>> rules) {
        this.renaper = renaper;
        this.repo = repo;
        this.rules = rules;
    }

    @Override
    public void validate(AvistadorRequestDTO dto) {

        // Spring inyecta automáticamente:
        // - AvistadorAgeRule (edad >= 18)
        // - AvistadorRenaperExistsRule (existe en RENAPER)
        // - AvistadorRenaperMatchRule (datos coinciden con RENAPER)
        // - AvistadorDniDuplicadoRule (DNI no duplicado)
        for (var rule : rules) {
            rule.check(dto);
        }
    }
}