package edu.utn.proyecto.common.validation.concreta.desaparecido.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesaparecidoContactoFormatRuleTest {

    private final DesaparecidoContactoFormatRule rule = new DesaparecidoContactoFormatRule();

    @Test
    void conTelefonoValido_pasa() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setContactoFamiliar("1154321098");

        assertThatNoException().isThrownBy(() -> rule.check(dto));
    }

    @Test
    void conTelefonoConCodigoPais_pasa() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setContactoFamiliar("+541154321098");

        assertThatNoException().isThrownBy(() -> rule.check(dto));
    }

    @Test
    void conEmailValido_pasa() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setEmailContactoFamiliar("familiar@example.com");

        assertThatNoException().isThrownBy(() -> rule.check(dto));
    }

    @Test
    void conAmbosValidos_pasa() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setContactoFamiliar("1154321098");
        dto.setEmailContactoFamiliar("familiar@example.com");

        assertThatNoException().isThrownBy(() -> rule.check(dto));
    }

    @Test
    void sinNinguno_lanzaContactoRequerido() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("desaparecido.contacto.requerido");
    }

    @Test
    void conTelefonoInvalido_lanzaTelefonoInvalido() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setContactoFamiliar("abc-no-es-telefono");

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("desaparecido.contacto.telefono.invalido");
    }

    @Test
    void conEmailInvalido_lanzaEmailInvalido() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO();
        dto.setEmailContactoFamiliar("no-es-un-email");

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("desaparecido.contacto.email.invalido");
    }
}
