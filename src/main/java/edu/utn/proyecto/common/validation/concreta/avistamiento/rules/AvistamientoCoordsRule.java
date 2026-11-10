package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;
import edu.utn.proyecto.avistamiento.exception.AvistamientoError;
import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.common.validation.abstraccion.Rule;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AvistamientoCoordsRule implements Rule<AvistamientoRequestDTO> {

    // Constantes de validación WGS84
    private static final double LAT_MIN = -90.0,  LAT_MAX = 90.0;
    private static final double LNG_MIN = -180.0, LNG_MAX = 180.0;

    @Override
    public void check(AvistamientoRequestDTO dto) {
        validateNumber("latitud", dto.getLatitud(), LAT_MIN, LAT_MAX);
        validateNumber("longitud", dto.getLongitud(), LNG_MIN, LNG_MAX);
    }

    private void validateNumber(String field, Double value, double min, double max) {
        if (value == null || value.isNaN() || value.isInfinite() || value < min || value > max) {
            throw DomainException.of(
                    AvistamientoError.COORDS_INVALID.key,
                    AvistamientoError.COORDS_INVALID.status
            );
        }
    }
}
