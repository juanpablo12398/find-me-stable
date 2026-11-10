package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;

import edu.utn.proyecto.common.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WktPolygonFormatRuleTest {

    private final WktPolygonFormatRule rule = new WktPolygonFormatRule();

    @Test
    void debeFallarCuandoEsNuloOVacio() {
        assertThatThrownBy(() -> rule.check(null))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> org.assertj.core.api.Assertions.assertThat(((DomainException) ex).getKey())
                        .isEqualTo("avistamiento.poligono.wkt.required"));

        assertThatThrownBy(() -> rule.check("   "))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> org.assertj.core.api.Assertions.assertThat(((DomainException) ex).getKey())
                        .isEqualTo("avistamiento.poligono.wkt.required"));
    }

    @Test
    void debeFallarCuandoNoEsPolygonValido() {
        assertThatThrownBy(() -> rule.check("POINT(1 1)"))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> org.assertj.core.api.Assertions.assertThat(((DomainException) ex).getKey())
                        .isEqualTo("avistamiento.poligono.wkt.invalid"));
    }

    @Test
    void debeFallarCuandoExcedeMaximo() {
        String huge = "POLYGON((" + "1 1,".repeat(3000) + "1 1))";

        assertThatThrownBy(() -> rule.check(huge))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> org.assertj.core.api.Assertions.assertThat(((DomainException) ex).getKey())
                        .isEqualTo("avistamiento.poligono.wkt.too_large"));
    }

    @Test
    void debeAceptarPolygonValido() {
        assertThatCode(() -> rule.check("POLYGON((0 0, 1 0, 1 1, 0 0))"))
                .doesNotThrowAnyException();
    }
}
