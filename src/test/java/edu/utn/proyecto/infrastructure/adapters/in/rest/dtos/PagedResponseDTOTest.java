package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PagedResponseDTOTest {

    @Test
    void of_conTotalCero_debeMarcarLastTrue() {
        PagedResponseDTO<String> dto = PagedResponseDTO.of(List.of(), 0, 12, 0);

        assertThat(dto.getTotalPages()).isZero();
        assertThat(dto.isLast()).isTrue();
    }

    @Test
    void of_enPaginaIntermedia_debeMarcarLastFalse() {
        PagedResponseDTO<String> dto = PagedResponseDTO.of(List.of("a"), 1, 12, 25);

        assertThat(dto.getTotalPages()).isEqualTo(3);
        assertThat(dto.isLast()).isFalse();
    }

    @Test
    void of_enUltimaPagina_debeMarcarLastTrue() {
        PagedResponseDTO<String> dto = PagedResponseDTO.of(List.of("a"), 2, 12, 25);

        assertThat(dto.getTotalPages()).isEqualTo(3);
        assertThat(dto.isLast()).isTrue();
    }
}
