package edu.utn.proyecto.common.normalize;
import org.springframework.stereotype.Component;

@Component
public class SortNormalizer {

    private final TextNormalizer txtNorm;

    public SortNormalizer(TextNormalizer txtNorm) {
        this.txtNorm = txtNorm;
    }

    public String normalizeSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return "urgencia";
        }
        String key = txtNorm.lowerAllCaps(sort);
        return switch (key) {
            case "nombre", "reciente", "antiguo", "urgencia" -> key;
            default -> "urgencia";
        };
    }
}
