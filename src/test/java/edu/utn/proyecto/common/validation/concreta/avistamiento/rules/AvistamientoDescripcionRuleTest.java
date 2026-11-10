package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvistamientoDescripcionRuleTest {

    private AvistamientoDescripcionRule rule;

    @BeforeEach
    void setUp() {
        rule = new AvistamientoDescripcionRule();
    }

    @Test
    void check_larga_ok() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setDescripcion("Esta es una descripcion con mas de 20 caracteres para pasar.");
        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_corta_lanzaExcepcion() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setDescripcion("Muy corta.");
        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistamiento.descripcion.corta");
    }

    @Test
    void check_nula_lanzaExcepcion() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setDescripcion(null);
        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistamiento.descripcion.corta");
    }
}
