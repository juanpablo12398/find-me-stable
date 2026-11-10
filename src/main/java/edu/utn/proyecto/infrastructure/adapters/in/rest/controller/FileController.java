package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.usecase.file.DeleteFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.DownloadFileUseCase;
import edu.utn.proyecto.applicacion.usecase.file.GetFileMimeTypeUseCase;
import edu.utn.proyecto.applicacion.usecase.file.UploadFileUseCase;
import edu.utn.proyecto.domain.model.concreta.FileMetadata;
import edu.utn.proyecto.domain.model.enums.EntityType;
import edu.utn.proyecto.domain.model.enums.FileCategory;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.FileUploadDTO;
import edu.utn.proyecto.security.SecurityContextHelper;
import edu.utn.proyecto.security.SignedUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Archivos", description = "Subida y descarga de fotos. Subida requiere USUARIO o ADMIN; eliminación requiere ADMIN.")
@RestController
@RequestMapping({"/api/v1/files", "/api/files"})
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;
    private final DownloadFileUseCase downloadFileUseCase;
    private final GetFileMimeTypeUseCase getFileMimeTypeUseCase;
    private final DeleteFileUseCase deleteFileUseCase;
    private final SecurityContextHelper securityHelper;
    private final SignedUrlService signedUrlService;

    public FileController(UploadFileUseCase uploadFileUseCase,
                          DownloadFileUseCase downloadFileUseCase,
                          GetFileMimeTypeUseCase getFileMimeTypeUseCase,
                          DeleteFileUseCase deleteFileUseCase,
                          SecurityContextHelper securityHelper,
                          SignedUrlService signedUrlService) {
        this.uploadFileUseCase = uploadFileUseCase;
        this.downloadFileUseCase = downloadFileUseCase;
        this.getFileMimeTypeUseCase = getFileMimeTypeUseCase;
        this.deleteFileUseCase = deleteFileUseCase;
        this.securityHelper = securityHelper;
        this.signedUrlService = signedUrlService;
    }

    @Operation(summary = "Subir archivo 🔒", description = "Sube una foto asociada a un desaparecido o avistamiento. Requiere sesión activa. Protegido por SecurityConfig + verificación interna de securityHelper.isAuthenticated(). Máx 10 MB.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Archivo subido"),
            @ApiResponse(responseCode = "400", description = "Tipo de archivo no permitido o tamaño excedido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("personId") UUID personId,
            @RequestParam("entityType") EntityType entityType,
            @RequestParam(value = "category", defaultValue = "PHOTO") FileCategory category) {

        if (!securityHelper.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Debe iniciar sesion para subir archivos"));
        }

        FileUploadDTO dto = new FileUploadDTO(file, personId, entityType, category);
        FileMetadata metadata = uploadFileUseCase.execute(dto);

        String signedPath = signedUrlService.generateSignedPath(metadata.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "fileId", metadata.getId(),
                        "downloadUrl", signedPath,
                        "mimeType", metadata.getMimeType(),
                        "sizeBytes", metadata.getSizeBytes()
                ));
    }

    @Operation(summary = "Descargar archivo", description = "Descarga un archivo por su ID. Acceso público. Si incluye ?expires=&sig= valida la URL firmada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Archivo descargado"),
            @ApiResponse(responseCode = "403", description = "URL firmada inválida o expirada"),
            @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    })
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> download(
            @PathVariable UUID fileId,
            @RequestParam(required = false) Long expires,
            @RequestParam(required = false) String sig) {
        if (expires != null && sig != null) {
            if (!signedUrlService.isValid(fileId, expires, sig)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        Resource resource = downloadFileUseCase.execute(fileId);
        String mimeType = getFileMimeTypeUseCase.execute(fileId);
        String cacheControl = (expires != null) ? "max-age=60, private" : "max-age=86400";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CACHE_CONTROL, cacheControl)
                .body(resource);
    }

    @Operation(summary = "Eliminar archivo 🔒", description = "Elimina un archivo. Requiere rol ADMINISTRADOR. Enforcement doble: SecurityConfig hasRole('ADMINISTRADOR') en URL + verificación interna de securityHelper.isAdmin().")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Archivo eliminado"),
            @ApiResponse(responseCode = "403", description = "Solo ADMINISTRADOR puede eliminar archivos"),
            @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(@PathVariable UUID fileId) {
        if (!securityHelper.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        deleteFileUseCase.execute(fileId);
        return ResponseEntity.noContent().build();
    }
}