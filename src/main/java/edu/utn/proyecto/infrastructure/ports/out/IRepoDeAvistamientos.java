package edu.utn.proyecto.infrastructure.ports.out;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRepoDeAvistamientos {
    Avistamiento save(Avistamiento avistamiento);
    Optional<Avistamiento> findById(UUID id);
    List<Avistamiento> findAll();
    List<Avistamiento> findByDesaparecidoId(UUID desaparecidoId);
    List<Avistamiento> findUltimaUbicacion(UUID desaparecidoId);
    List<Avistamiento> findByAvistadorId(UUID avistadorId);
    List<Avistamiento> findPublicos();
    Page<Avistamiento> findPublicosPaginado(Pageable pageable);
    List<Avistamiento> findInBounds(Double latMin, Double latMax, Double lngMin, Double lngMax);
    List<Avistamiento> findRecientes(LocalDateTime desde);
    void deleteById(UUID id);
    List<Avistamiento> findWithinRadius(Double lat, Double lng, Double radioKm);
    List<Avistamiento> findInPolygon(String polygonWKT);
    List<Avistamiento> findNearestN(Double lat, Double lng, Integer limite);
    List<Avistamiento> findByDesaparecidoWithinRadius(UUID desaparecidoId, Double lat, Double lng, Double radioKm);
    Long countInBounds(Double latMin, Double latMax, Double lngMin, Double lngMax);

    List<Avistamiento> findVisiblesParaUsuario(UUID avistadorId);
    List<Avistamiento> findInBoundsVisibles(Double latMin, Double latMax, Double lngMin, Double lngMax, UUID avistadorId);
    List<Avistamiento> findWithinRadiusVisibles(Double lat, Double lng, Double radioKm, UUID avistadorId);
    List<Avistamiento> findInPolygonVisibles(String polygonWKT, UUID avistadorId);

    List<Avistamiento> findInBoundsAll(Double latMin, Double latMax, Double lngMin, Double lngMax);
    List<Avistamiento> findWithinRadiusAll(Double lat, Double lng, Double radioKm);
    List<Avistamiento> findInPolygonAll(String polygonWKT);
}