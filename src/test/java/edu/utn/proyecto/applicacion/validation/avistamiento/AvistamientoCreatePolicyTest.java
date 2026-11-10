package edu.utn.proyecto.applicacion.validation.avistamiento;

import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AvistamientoCreatePolicyTest {

    @Mock
    private IRepoDeAvistadores repoAvistadores;

    @Mock
    private IRepoDeDesaparecidos repoDesaparecidos;

    @Mock
    private Rule<AvistamientoRequestDTO> rule1;

    @Mock
    private Rule<AvistamientoRequestDTO> rule2;

    @Test
    void validate_ejecutaTodasLasReglas() {
        // Arrange
        List<Rule<AvistamientoRequestDTO>> rules = List.of(rule1, rule2);
        AvistamientoCreatePolicy policy = new AvistamientoCreatePolicy(repoAvistadores, repoDesaparecidos, rules);
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();

        // Act
        policy.validate(dto);

        // Assert
        verify(rule1).check(dto);
        verify(rule2).check(dto);
    }
}
