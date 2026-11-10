package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvistamientoCoordsRuleTest {

    private AvistamientoCoordsRule rule;

    @BeforeEach
    void setUp() {
        rule = new AvistamientoCoordsRule();
    }

    @Test
    void check_coordenadasValidas_noLanzaExcepcion() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setLatitud(-34.6037);
        dto.setLongitud(-58.3816);

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_latitudFueraDeRango_lanzaExcepcion() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setLatitud(100.0);
        dto.setLongitud(-58.3816);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistamiento.coords.invalidas");
    }

    @Test
    void check_longitudFueraDeRango_lanzaExcepcion() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setLatitud(-34.6037);
        dto.setLongitud(-200.0);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistamiento.coords.invalidas");
    }

    @Test
    void check_coordenadasNulas_lanzaExcepcion() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setLatitud(null);
        dto.setLongitud(-58.3816);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistamiento.coords.invalidas");
    }
}
