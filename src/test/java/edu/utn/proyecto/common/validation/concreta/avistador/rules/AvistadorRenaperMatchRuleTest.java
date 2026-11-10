package edu.utn.proyecto.common.validation.concreta.avistador.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
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
class AvistadorRenaperMatchRuleTest {

    @Mock
    private IRepoDeRenaper renaper;

    @InjectMocks
    private AvistadorRenaperMatchRule rule;

    @Test
    void check_renaperMatch_ok() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setDni("12345678");
        dto.setNombre("JUAN");
        dto.setApellido("PEREZ");

        RenaperPersonaEntity entity = new RenaperPersonaEntity();
        entity.setNombre("JUAN");
        entity.setApellido("PEREZ");

        when(renaper.findByDni("12345678")).thenReturn(Optional.of(entity));

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_renaperNoMatchNombre_lanzaExcepcion() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setDni("12345678");
        dto.setNombre("JUAN");
        dto.setApellido("PEREZ");

        RenaperPersonaEntity entity = new RenaperPersonaEntity();
        entity.setNombre("PEDRO");
        entity.setApellido("PEREZ");

        when(renaper.findByDni("12345678")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistador.renaper.nomatch");
    }

    @Test
    void check_renaperNoMatchApellido_lanzaExcepcion() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setDni("12345678");
        dto.setNombre("JUAN");
        dto.setApellido("PEREZ");

        RenaperPersonaEntity entity = new RenaperPersonaEntity();
        entity.setNombre("JUAN");
        entity.setApellido("OTRO");

        when(renaper.findByDni("12345678")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistador.renaper.nomatch");
    }
}
