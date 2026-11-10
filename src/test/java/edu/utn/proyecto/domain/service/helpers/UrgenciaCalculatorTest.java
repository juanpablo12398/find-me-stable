package edu.utn.proyecto.domain.service.helpers;

import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UrgenciaCalculatorTest {

    private final UrgenciaCalculator calculator = new UrgenciaCalculator();

    @Test
    void fechaDesaparicionHaceUnaHora_devuelveCritico() {
        NivelUrgencia result = calculator.calcular(LocalDateTime.now().minusHours(1));
        assertThat(result).isEqualTo(NivelUrgencia.CRITICO);
    }

    @Test
    void fechaDesaparicionHaceTresDias_devuelveAlto() {
        NivelUrgencia result = calculator.calcular(LocalDateTime.now().minusDays(3));
        assertThat(result).isEqualTo(NivelUrgencia.ALTO);
    }

    @Test
    void fechaDesaparicionHaceQuinceDias_devuelveMedio() {
        NivelUrgencia result = calculator.calcular(LocalDateTime.now().minusDays(15));
        assertThat(result).isEqualTo(NivelUrgencia.MEDIO);
    }

    @Test
    void fechaDesaparicionHaceSesentaDias_devuelveBajo() {
        NivelUrgencia result = calculator.calcular(LocalDateTime.now().minusDays(60));
        assertThat(result).isEqualTo(NivelUrgencia.BAJO);
    }

    @Test
    void fechaDesaparicionNull_devuelveBajo() {
        NivelUrgencia result = calculator.calcular(null);
        assertThat(result).isEqualTo(NivelUrgencia.BAJO);
    }
}
