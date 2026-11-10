package edu.utn.proyecto.common.validation.concreta.desaparecido.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.desaparecido.exception.DesaparecidoError;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Valida los campos de contacto del familiar del desaparecido.
 * Reglas:
 * - Al menos uno de los dos (contactoFamiliar o emailContactoFamiliar) debe estar presente.
 * - Si contactoFamiliar está presente: formato teléfono argentino (10-13 dígitos, con/sin +54).
 * - Si emailContactoFamiliar está presente: formato email RFC 5322 simplificado.
 */
@Component
@Order(4)
public class DesaparecidoContactoFormatRule implements Rule<DesaparecidoRequestDTO> {

    // Acepta: 1154321098 (10d), 541154321098 (12d), +541154321098 (13d con +)
    private static final String TELEFONO_ARG_REGEX = "^(\\+?54)?[0-9]{10,11}$";

    // Email simplificado RFC 5322: algo@algo.algo
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    @Override
    public void check(DesaparecidoRequestDTO dto) {
        boolean tieneContacto = dto.getContactoFamiliar() != null && !dto.getContactoFamiliar().isBlank();
        boolean tieneEmail    = dto.getEmailContactoFamiliar() != null && !dto.getEmailContactoFamiliar().isBlank();

        if (!tieneContacto && !tieneEmail) {
            throw DomainException.of(
                    DesaparecidoError.CONTACTO_REQUERIDO.key,
                    DesaparecidoError.CONTACTO_REQUERIDO.status);
        }

        if (tieneContacto) {
            String telefono = dto.getContactoFamiliar().replaceAll("\\s", "");
            if (!telefono.matches(TELEFONO_ARG_REGEX)) {
                throw DomainException.of(
                        DesaparecidoError.CONTACTO_TELEFONO_INVALIDO.key,
                        DesaparecidoError.CONTACTO_TELEFONO_INVALIDO.status);
            }
        }

        if (tieneEmail) {
            if (!dto.getEmailContactoFamiliar().matches(EMAIL_REGEX)) {
                throw DomainException.of(
                        DesaparecidoError.CONTACTO_EMAIL_INVALIDO.key,
                        DesaparecidoError.CONTACTO_EMAIL_INVALIDO.status);
            }
        }
    }
}
