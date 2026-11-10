package edu.utn.proyecto.infrastructure.adapters.out.rest.filesystem;

import edu.utn.proyecto.common.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para LocalFileSystemBinaryStorageAdapter.
 * Cubre: path traversal, guardado y lectura normal, borrado.
 */
class LocalFileSystemBinaryStorageAdapterTest {

    @TempDir
    Path tempDir;

    private LocalFileSystemBinaryStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LocalFileSystemBinaryStorageAdapter(tempDir.toString());
    }

    // ── Path traversal ────────────────────────────────────────────────────

    @Test
    void saveAtomically_pathTraversal_dosPuntos_lanzaDomainException() {
        byte[] datos = "contenido malicioso".getBytes();
        UUID fileId = UUID.randomUUID();

        assertThatThrownBy(() -> adapter.saveAtomically("../../etc/passwd", fileId, datos))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    DomainException de = (DomainException) ex;
                    assertThat(de.getKey()).isEqualTo("file.path_traversal");
                    assertThat(de.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }

    @Test
    void loadAsResource_pathTraversal_dosPuntos_lanzaDomainException() {
        assertThatThrownBy(() -> adapter.loadAsResource("../../etc/shadow"))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    DomainException de = (DomainException) ex;
                    assertThat(de.getKey()).isEqualTo("file.path_traversal");
                    assertThat(de.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }

    @Test
    void deleteIfExists_pathTraversal_dosPuntos_lanzaDomainException() {
        assertThatThrownBy(() -> adapter.deleteIfExists("../../../tmp/archivo"))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    DomainException de = (DomainException) ex;
                    assertThat(de.getKey()).isEqualTo("file.path_traversal");
                    assertThat(de.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }

    @Test
    void saveAtomically_pathTraversal_codificacionUrl_lanzaDomainException() {
        // Secuencia de traversal con separadores de path alternativos
        byte[] datos = "malicioso".getBytes();
        UUID fileId = UUID.randomUUID();

        assertThatThrownBy(() -> adapter.saveAtomically("subfolder/../../etc/passwd", fileId, datos))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> assertThat(((DomainException) ex).getKey()).isEqualTo("file.path_traversal"));
    }

    // ── Operaciones normales ──────────────────────────────────────────────

    @Test
    void saveAtomically_rutaLegitima_guardaCorrectamente() throws Exception {
        byte[] datos = "hola mundo".getBytes();
        UUID fileId = UUID.randomUUID();
        String storageKey = "fotos/" + fileId + ".jpg";

        // No debe lanzar excepción
        adapter.saveAtomically(storageKey, fileId, datos);

        // Verificar que el archivo existe y tiene el contenido correcto
        Path archivo = tempDir.resolve(storageKey);
        assertThat(archivo).exists();
        assertThat(java.nio.file.Files.readAllBytes(archivo)).isEqualTo(datos);
    }

    @Test
    void loadAsResource_archivoNoExiste_lanzaDomainExceptionNotFound() {
        assertThatThrownBy(() -> adapter.loadAsResource("no/existe.jpg"))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    DomainException de = (DomainException) ex;
                    assertThat(de.getKey()).isEqualTo("file.not_found");
                    assertThat(de.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    @Test
    void deleteIfExists_archivoExistente_loElimina() throws Exception {
        byte[] datos = "contenido".getBytes();
        UUID fileId = UUID.randomUUID();
        String storageKey = "test/" + fileId + ".jpg";

        adapter.saveAtomically(storageKey, fileId, datos);
        Path archivo = tempDir.resolve(storageKey);
        assertThat(archivo).exists();

        adapter.deleteIfExists(storageKey);
        assertThat(archivo).doesNotExist();
    }

    @Test
    void deleteIfExists_archivoInexistente_noLanzaExcepcion() {
        // No debe lanzar excepción si el archivo no existe
        org.assertj.core.api.Assertions.assertThatNoException()
                .isThrownBy(() -> adapter.deleteIfExists("no/existe.jpg"));
    }
}
