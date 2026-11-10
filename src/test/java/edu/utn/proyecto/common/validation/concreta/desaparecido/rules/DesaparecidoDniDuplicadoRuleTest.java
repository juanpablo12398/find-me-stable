package edu.utn.proyecto.common.validation.concreta.desaparecido.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesaparecidoDniDuplicadoRuleTest {

    @Mock
    private IRepoDeDesaparecidos repo;

    @InjectMocks
    private DesaparecidoDniDuplicadoRule rule;

    @Test
    void check_dniNoDuplicado_noLanzaExcepcion() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setDni("12345678");

        when(repo.existsByDni("12345678")).thenReturn(false);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_dniDuplicado_lanzaExcepcion() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setDni("12345678");

        when(repo.existsByDni("12345678")).thenReturn(true);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("desaparecido.dni.duplicado");
    }
}
