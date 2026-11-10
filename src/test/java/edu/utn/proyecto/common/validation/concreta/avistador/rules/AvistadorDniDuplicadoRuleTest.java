package edu.utn.proyecto.common.validation.concreta.avistador.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvistadorDniDuplicadoRuleTest {

    @Mock
    private IRepoDeAvistadores repo;

    @InjectMocks
    private AvistadorDniDuplicadoRule rule;

    @Test
    void check_dniNoDuplicado_noLanzaExcepcion() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setDni("12345678");

        when(repo.existsByDni("12345678")).thenReturn(false);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_dniDuplicado_lanzaExcepcion() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setDni("12345678");

        when(repo.existsByDni("12345678")).thenReturn(true);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistador.dni.duplicado");
    }
}
