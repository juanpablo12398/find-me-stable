package edu.utn.proyecto.common.validation.concreta.desaparecido.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesaparecidoRenaperExistsRuleTest {

    @Mock
    private IRepoDeRenaper renaper;

    @InjectMocks
    private DesaparecidoRenaperExistsRule rule;

    @Test
    void check_renaperExists_ok() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setDni("12345678");

        when(renaper.findByDni("12345678")).thenReturn(Optional.of(new RenaperPersonaEntity()));

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_renaperNotFound_lanzaExcepcion() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setDni("12345678");

        when(renaper.findByDni("12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("desaparecido.renaper.notfound");
    }

    @Test
    void check_dniNull_lanzaExcepcion() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setDni(null);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("desaparecido.renaper.notfound");
    }
}
