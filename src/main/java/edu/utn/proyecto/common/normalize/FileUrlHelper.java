package edu.utn.proyecto.common.normalize;
import edu.utn.proyecto.security.SignedUrlService;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Genera URLs de acceso a archivos.
 * Usado por los mappers para armar fotoUrl en los DTOs de front.
 *
 * Las URLs son firmadas con HMAC y tienen expiración,
 * así los links compartidos dejan de funcionar después de N segundos.
 */
@Component
public class FileUrlHelper {

    private final SignedUrlService signedUrlService;

    public FileUrlHelper(SignedUrlService signedUrlService) {
        this.signedUrlService = signedUrlService;
    }

    public String toUrl(UUID fileId) {
        if (fileId == null) return null;
        return signedUrlService.generateSignedPath(fileId);
    }
}