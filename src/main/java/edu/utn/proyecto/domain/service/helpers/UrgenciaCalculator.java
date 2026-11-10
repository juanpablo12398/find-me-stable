package edu.utn.proyecto.domain.service.helpers;
import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class UrgenciaCalculator {

    public NivelUrgencia calcular(LocalDateTime fechaDesaparicion) {
        if (fechaDesaparicion == null) {
            return NivelUrgencia.BAJO;
        }

        long horasTranscurridas = ChronoUnit.HOURS.between(fechaDesaparicion, LocalDateTime.now());

        if (horasTranscurridas <= 48) {
            return NivelUrgencia.CRITICO;
        } else if (horasTranscurridas <= 168) { // 7 días
            return NivelUrgencia.ALTO;
        } else if (horasTranscurridas <= 720) { // 30 días
            return NivelUrgencia.MEDIO;
        } else {
            return NivelUrgencia.BAJO;
        }
    }
}
