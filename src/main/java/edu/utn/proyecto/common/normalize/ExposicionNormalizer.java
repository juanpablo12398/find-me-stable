package edu.utn.proyecto.common.normalize;
import org.springframework.stereotype.Component;

@Component
public class ExposicionNormalizer {

    public String inicializar(String texto) {
        if (texto == null || texto.isBlank()) return null;
        return texto.trim().substring(0, 1).toUpperCase() + ".";
    }

    public String aproximarUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) return null;
        String[] partes = ubicacion.split(",");
        return partes[partes.length - 1].trim();
    }

    public Double redondearCoordenada(Double valor, int decimales) {
        if (valor == null) return null;
        double factor = Math.pow(10, decimales);
        return Math.round(valor * factor) / factor;
    }
}