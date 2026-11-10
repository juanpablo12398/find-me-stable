package edu.utn.proyecto.common.normalize;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SortNormalizerTest {

    private final SortNormalizer sortNormalizer = new SortNormalizer(new TextNormalizer());

    @Test
    void normalizeSort_debeRetornarUrgenciaSiEsNullOVacio() {
        assertThat(sortNormalizer.normalizeSort(null)).isEqualTo("urgencia");
        assertThat(sortNormalizer.normalizeSort("   ")).isEqualTo("urgencia");
    }

    @Test
    void normalizeSort_debeNormalizarValoresValidos() {
        assertThat(sortNormalizer.normalizeSort("RECiente")).isEqualTo("reciente");
        assertThat(sortNormalizer.normalizeSort("NOMBRE")).isEqualTo("nombre");
        assertThat(sortNormalizer.normalizeSort("urgencia")).isEqualTo("urgencia");
    }

    @Test
    void normalizeSort_debeFallbackAUrgenciaEnValorDesconocido() {
        assertThat(sortNormalizer.normalizeSort("random")).isEqualTo("urgencia");
    }
}
