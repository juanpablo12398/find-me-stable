package edu.utn.proyecto.common.normalize;
import org.springframework.stereotype.Component;

@Component
public class DniNormalizer {
    public String normalize(String dni) {
        if (dni == null) return null;
        return dni.replaceAll("\\D", ""); // quita puntos/espacios
    }
}
