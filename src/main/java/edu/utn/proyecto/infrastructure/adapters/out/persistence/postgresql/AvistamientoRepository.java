package edu.utn.proyecto.infrastructure.adapters.out.persistence.postgresql;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers.AvistamientoPersistenceMapper;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistamientos;
import edu.utn.proyecto.infrastructure.ports.out.jpa.AvistamientoJpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class AvistamientoRepository implements IRepoDeAvistamientos {

    private final AvistamientoJpaRepository jpa;
    private final AvistamientoPersistenceMapper mapper;

    public AvistamientoRepository(AvistamientoJpaRepository jpa,
                                      AvistamientoPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Avistamiento save(Avistamiento avistamiento) {
        var saved = jpa.save(mapper.toEntity(avistamiento));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Avistamiento> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Avistamiento> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findByDesaparecidoId(UUID desaparecidoId) {
        return jpa.findByDesaparecidoIdOrderByFechaHoraDesc(desaparecidoId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findUltimaUbicacion(UUID desaparecidoId) {
        return jpa.findTop1ByDesaparecidoIdOrderByFechaHoraDesc(desaparecidoId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findByAvistadorId(UUID avistadorId) {
        return jpa.findByAvistadorId(avistadorId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findPublicos() {
        return jpa.findByPublicoTrue().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public org.springframework.data.domain.Page<Avistamiento> findPublicosPaginado(org.springframework.data.domain.Pageable pageable) {
        return jpa.findByPublicoTrueOrderByFechaHoraDesc(pageable).map(mapper::toDomain);
    }

    @Override
    public List<Avistamiento> findRecientes(LocalDateTime desde) {
        return jpa.findRecientes(desde).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Avistamiento> findInBounds(Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return jpa.findInBounds(latMin, latMax, lngMin, lngMax).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findWithinRadius(Double lat, Double lng, Double radioKm) {
        return jpa.findWithinRadius(lat, lng, radioKm * 1000.0).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findInPolygon(String polygonWKT) {
        return jpa.findInPolygon(polygonWKT).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findNearestN(Double lat, Double lng, Integer limite) {
        return jpa.findNearestN(lat, lng, limite).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findByDesaparecidoWithinRadius(UUID desaparecidoId, Double lat, Double lng, Double radioKm) {
        return jpa.findByDesaparecidoWithinRadius(desaparecidoId, lat, lng, radioKm * 1000.0).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Long countInBounds(Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return jpa.countInBounds(latMin, latMax, lngMin, lngMax);
    }

    // =============================================
    // Visibilidad: públicos + privados del usuario
    // =============================================

    @Override
    public List<Avistamiento> findVisiblesParaUsuario(UUID avistadorId) {
        return jpa.findVisiblesParaUsuario(avistadorId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findInBoundsVisibles(Double latMin, Double latMax, Double lngMin, Double lngMax, UUID avistadorId) {
        return jpa.findInBoundsVisibles(latMin, latMax, lngMin, lngMax, avistadorId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findWithinRadiusVisibles(Double lat, Double lng, Double radioKm, UUID avistadorId) {
        return jpa.findWithinRadiusVisibles(lat, lng, radioKm * 1000.0, avistadorId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findInPolygonVisibles(String polygonWKT, UUID avistadorId) {
        return jpa.findInPolygonVisibles(polygonWKT, avistadorId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    // =============================================
    // Admin: ve completo sin filtro
    // =============================================

    @Override
    public List<Avistamiento> findInBoundsAll(Double latMin, Double latMax, Double lngMin, Double lngMax) {
        return jpa.findInBoundsAll(latMin, latMax, lngMin, lngMax).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findWithinRadiusAll(Double lat, Double lng, Double radioKm) {
        return jpa.findWithinRadiusAll(lat, lng, radioKm * 1000.0).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Avistamiento> findInPolygonAll(String polygonWKT) {
        return jpa.findInPolygonAll(polygonWKT).stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}