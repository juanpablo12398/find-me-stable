package edu.utn.proyecto.domain.service.helpers;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ExifStripper {

    private static final String FORMAT_JPEG = "jpg";
    private static final String FORMAT_PNG = "png";

    /**
     * Remueve EXIF de una imagen si es JPEG o PNG.
     * Si no es imagen o falla el procesamiento, retorna los bytes originales.
     *
     * @param bytes    contenido del archivo
     * @param mimeType tipo MIME del archivo
     * @return bytes sin EXIF, o los originales si no aplica
     */
    public byte[] strip(byte[] bytes, String mimeType) {
        if (mimeType == null) return bytes;

        String format = resolveFormat(mimeType);
        if (format == null) return bytes;

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) return bytes;
            // Re-renderizar sin metadatos
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, format, out);
            return out.toByteArray();
        } catch (IOException e) {
            // Si falla, devolvemos el original (mejor que perder la foto)
            return bytes;
        }
    }

    private String resolveFormat(String mimeType) {
        return switch (mimeType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> FORMAT_JPEG;
            case "image/png" -> FORMAT_PNG;
            default -> null;
        };
    }
}