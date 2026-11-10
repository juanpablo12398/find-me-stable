package edu.utn.proyecto.common.normalize;
import edu.utn.proyecto.domain.model.enums.EstadoDesaparecido;
import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.Locale;

@Component
public class TextNormalizer {

    public String upperNoAccents(String s) {
        if (s == null || s.isBlank()) return null;

        String sinAcentos = removeAccents(trimmed(s));
        return firstUpper(sinAcentos);
    }

    public String sentenceCase(String s) {
        if (s == null || s.isBlank()) return null;

        String trimmed = trimmed(s);

        // Primera letra mayúscula, resto minúsculas
        return firstUpper(trimmed);
    }

    public String normalize(String s) {
        if (s == null || s.isBlank()) return "";
        return trimmed(s);
    }

    public String normalizeUpperOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return trimmed(s).toUpperCase(Locale.ROOT);
    }

    public String upperNoAccentsAllCaps(NivelUrgencia s) {
        if (s == null) return null;

        String t = trimmed(s.name());
        String sinAcentos = removeAccents(t);
        return toUpperCase(sinAcentos);
    }

    public String lowerAllCaps(String estado) {
        if (estado == null) return null;

        String t = trimmed(estado);
        return t.toLowerCase(Locale.ROOT);
    }

    private String trimmed(String s) {
        return s.trim();
    }

    private String firstUpper(String s) {
        if (s == null || s.isBlank()) return null;

        String t = trimmed(s);
        return t.substring(0, 1).toUpperCase(Locale.ROOT) +
                t.substring(1).toLowerCase(Locale.ROOT);
    }

    private String removeAccents(String s) {
        // Normaliza a NFD y elimina marcas diacríticas (acentos)
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }

    private String toUpperCase(String s) {
        // Locale.ROOT evita comportamientos raros según configuración regional
        return s.toUpperCase(Locale.ROOT);
    }

}
