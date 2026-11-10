package edu.utn.proyecto.domain.service.helpers;

import edu.utn.proyecto.common.normalize.SortNormalizer;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesaparecidoSortTest {

    @Mock
    private SortNormalizer sortNormalizer;

    @Test
    void getComparator_debeOrdenarPorNombre() {
        when(sortNormalizer.normalizeSort("nombre")).thenReturn("nombre");
        DesaparecidoSort sort = new DesaparecidoSort(sortNormalizer);

        DesaparecidoFrontDTO zoe = dto("Zoe", "Zulu", "BAJO");
        DesaparecidoFrontDTO ana = dto("Ana", "Alfa", "CRITICO");
        List<DesaparecidoFrontDTO> lista = new ArrayList<>(List.of(zoe, ana));

        lista.sort(sort.getComparator("nombre"));

        assertThat(lista).extracting(DesaparecidoFrontDTO::getNombre).containsExactly("Ana", "Zoe");
    }

    @Test
    void getComparator_debeFallbackAUrgenciaSiSortDesconocido() {
        when(sortNormalizer.normalizeSort("x")).thenReturn("desconocido");
        DesaparecidoSort sort = new DesaparecidoSort(sortNormalizer);

        DesaparecidoFrontDTO alto = dto("Carlos", "Uno", "ALTO");
        DesaparecidoFrontDTO critico = dto("Bruno", "Dos", "CRITICO");
        List<DesaparecidoFrontDTO> lista = new ArrayList<>(List.of(alto, critico));

        lista.sort(sort.getComparator("x"));

        assertThat(lista).extracting(DesaparecidoFrontDTO::getNivelUrgencia).containsExactly("CRITICO", "ALTO");
    }

    private DesaparecidoFrontDTO dto(String nombre, String apellido, String urgencia) {
        return new DesaparecidoFrontDTO(
                UUID.randomUUID(),
                nombre,
                apellido,
                null,
                null,
                null,
                "10/03/2026 10:00",
                "buscando",
                null,
                null,
                urgencia,
                0);
    }
}
