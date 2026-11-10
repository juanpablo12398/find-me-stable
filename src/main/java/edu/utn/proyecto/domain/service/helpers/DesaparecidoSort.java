package edu.utn.proyecto.domain.service.helpers;
import edu.utn.proyecto.common.normalize.SortNormalizer;
import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DesaparecidoSort {
    private final SortNormalizer sortNorm;

    DesaparecidoSort(SortNormalizer sortNorm) {
        this.sortNorm = sortNorm;
    }

    private static final Map<String, Comparator<DesaparecidoFrontDTO>> COMPARATORS = Map.of(
        "nombre", Comparator.comparing(
            d -> (d.getNombre() + " " + d.getApellido()),
            String.CASE_INSENSITIVE_ORDER),
        "reciente", Comparator.comparing(
            DesaparecidoFrontDTO::getFechaFormateada,
            Comparator.nullsLast(Comparator.reverseOrder())),
        "antiguo", Comparator.comparing(
            DesaparecidoFrontDTO::getFechaFormateada,
            Comparator.nullsLast(Comparator.naturalOrder())),
        "urgencia", Comparator.comparingInt(
            d -> NivelUrgencia.valueOf(d.getNivelUrgencia()).getPrioridad()));

    public Comparator<DesaparecidoFrontDTO> getComparator(String sort) {
        return COMPARATORS.getOrDefault(sortNorm.normalizeSort(sort), COMPARATORS.get("urgencia"));
    }
}
