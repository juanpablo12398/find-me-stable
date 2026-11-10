package edu.utn.proyecto.common.validation.concreta.avistador.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvistadorAgeRuleTest {

    private AvistadorAgeRule rule;

    @BeforeEach
    void setUp() {
        rule = new AvistadorAgeRule();
    }

    @Test
    void check_mayorDeEdad_ok() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setEdad(20);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_menorDeEdad_lanzaExcepcion() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setEdad(17);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistador.edad.menor");
    }

    @Test
    void check_edadNull_noLanzaExcepcion() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setEdad(null);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }
}
