package edu.utn.proyecto.infrastructure.ports.out.jpa;

import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.AvistamientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AvistamientoJpaRepository extends JpaRepository<AvistamientoEntity, UUID> {

    List<AvistamientoEntity> findByDesaparecidoId(UUID desaparecidoId);
    List<AvistamientoEntity> findByAvistadorId(UUID avistadorId);
    List<AvistamientoEntity> findByPublicoTrue();
    Page<AvistamientoEntity> findByPublicoTrueOrderByFechaHoraDesc(Pageable pageable);

    // =============================================
    // Queries con política de visibilidad
    // Patrón: publicos + privados del usuario actual
    // Si avistadorId es null → solo públicos
    // =============================================

    @Query(value = """
            SELECT * FROM avistamientos a
            WHERE a.publico = true
               OR a.avistador_id = CAST(:avistadorId AS UUID)
            ORDER BY a.fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findVisiblesParaUsuario(@Param("avistadorId") UUID avistadorId);

    @Query("SELECT a FROM AvistamientoEntity a WHERE a.fechaHora >= :desde AND a.publico = true ORDER BY a.fechaHora DESC")
    List<AvistamientoEntity> findRecientes(@Param("desde") LocalDateTime desde);

    List<AvistamientoEntity> findByDesaparecidoIdOrderByFechaHoraDesc(UUID desaparecidoId);
    
    List<AvistamientoEntity> findTop1ByDesaparecidoIdOrderByFechaHoraDesc(UUID desaparecidoId);

    List<AvistamientoEntity> findByAvistadorIdOrderByFechaHoraDesc(UUID avistadorId);

    // =========================
    // Consultas espaciales (PostGIS)
    // =========================

    @Query(value = """
            SELECT *
            FROM avistamientos a
            WHERE a.ubicacion IS NOT NULL
              AND a.publico = true
              AND ST_Within(
                    a.ubicacion,
                    ST_MakeEnvelope(:lngMin, :latMin, :lngMax, :latMax, 4326)
                  )
            ORDER BY a.fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findInBounds(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lngMin") Double lngMin,
            @Param("lngMax") Double lngMax
    );

    @Query(value = """
            SELECT *
            FROM avistamientos a
            WHERE a.ubicacion IS NOT NULL
              AND (a.publico = true OR a.avistador_id = CAST(:avistadorId AS UUID))
              AND ST_Within(
                    a.ubicacion,
                    ST_MakeEnvelope(:lngMin, :latMin, :lngMax, :latMax, 4326)
                  )
            ORDER BY a.fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findInBoundsVisibles(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lngMin") Double lngMin,
            @Param("lngMax") Double lngMax,
            @Param("avistadorId") UUID avistadorId
    );

    @Query(value = """
            SELECT a.*,
                   ST_Distance(a.ubicacion::geography,
                              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) as distancia
            FROM avistamientos a
            WHERE a.ubicacion IS NOT NULL
            AND (a.publico = true OR a.avistador_id = CAST(:avistadorId AS UUID))
            AND ST_DWithin(
                a.ubicacion::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                :radioMetros
            )
            ORDER BY distancia
            """, nativeQuery = true)
    List<AvistamientoEntity> findWithinRadiusVisibles(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radioMetros") Double radioMetros,
            @Param("avistadorId") UUID avistadorId
    );

    @Query(value = """
            SELECT a.*,
                   ST_Distance(a.ubicacion::geography,
                              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) as distancia
            FROM avistamientos a
            WHERE a.ubicacion IS NOT NULL
            AND ST_DWithin(
                a.ubicacion::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                :radioMetros
            )
            ORDER BY distancia
            """, nativeQuery = true)
    List<AvistamientoEntity> findWithinRadius(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radioMetros") Double radioMetros
    );

    @Query(value = """
            SELECT * FROM avistamientos
            WHERE ubicacion IS NOT NULL
            AND (publico = true OR avistador_id = CAST(:avistadorId AS UUID))
            AND ST_Within(
                ubicacion,
                ST_GeomFromText(:polygonWKT, 4326)
            )
            ORDER BY fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findInPolygonVisibles(
            @Param("polygonWKT") String polygonWKT,
            @Param("avistadorId") UUID avistadorId
    );

    @Query(value = """
            SELECT * FROM avistamientos
            WHERE ubicacion IS NOT NULL
            AND ST_Within(
                ubicacion,
                ST_GeomFromText(:polygonWKT, 4326)
            )
            ORDER BY fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findInPolygon(@Param("polygonWKT") String polygonWKT);

    @Query(value = """
            SELECT * FROM avistamientos
            WHERE ubicacion IS NOT NULL
            ORDER BY ubicacion <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            LIMIT :limite
            """, nativeQuery = true)
    List<AvistamientoEntity> findNearestN(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("limite") Integer limite
    );

    @Query(value = """
            SELECT * FROM avistamientos
            WHERE desaparecido_id = :desaparecidoId
            AND ubicacion IS NOT NULL
            AND ST_DWithin(
                ubicacion::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                :radioMetros
            )
            ORDER BY fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findByDesaparecidoWithinRadius(
            @Param("desaparecidoId") UUID desaparecidoId,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radioMetros") Double radioMetros
    );

    @Query(value = """
            SELECT COUNT(*) FROM avistamientos
            WHERE ubicacion IS NOT NULL
            AND ST_Within(
                ubicacion,
                ST_MakeEnvelope(:lngMin, :latMin, :lngMax, :latMax, 4326)
            )
            """, nativeQuery = true)
    Long countInBounds(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lngMin") Double lngMin,
            @Param("lngMax") Double lngMax
    );

    @Query(value = """
            SELECT
                ST_Y(ST_SnapToGrid(ubicacion, :precision)) as lat,
                ST_X(ST_SnapToGrid(ubicacion, :precision)) as lng,
                COUNT(*) as cantidad
            FROM avistamientos
            WHERE ubicacion IS NOT NULL
            GROUP BY ST_SnapToGrid(ubicacion, :precision)
            HAVING COUNT(*) >= :minCantidad
            """, nativeQuery = true)
    List<Object[]> findClusters(
            @Param("precision") Double precision,
            @Param("minCantidad") Integer minCantidad
    );

    // =============================================
    // Queries para ADMIN (ve T0DO sin filtro)
    // =============================================

    @Query(value = """
            SELECT * FROM avistamientos a
            ORDER BY a.fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findAllOrdered();

    @Query(value = """
            SELECT *
            FROM avistamientos a
            WHERE a.ubicacion IS NOT NULL
              AND ST_Within(
                    a.ubicacion,
                    ST_MakeEnvelope(:lngMin, :latMin, :lngMax, :latMax, 4326)
                  )
            ORDER BY a.fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findInBoundsAll(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lngMin") Double lngMin,
            @Param("lngMax") Double lngMax
    );

    @Query(value = """
            SELECT a.*,
                   ST_Distance(a.ubicacion::geography,
                              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) as distancia
            FROM avistamientos a
            WHERE a.ubicacion IS NOT NULL
            AND ST_DWithin(
                a.ubicacion::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                :radioMetros
            )
            ORDER BY distancia
            """, nativeQuery = true)
    List<AvistamientoEntity> findWithinRadiusAll(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radioMetros") Double radioMetros
    );

    @Query(value = """
            SELECT * FROM avistamientos
            WHERE ubicacion IS NOT NULL
            AND ST_Within(
                ubicacion,
                ST_GeomFromText(:polygonWKT, 4326)
            )
            ORDER BY fecha_hora DESC
            """, nativeQuery = true)
    List<AvistamientoEntity> findInPolygonAll(@Param("polygonWKT") String polygonWKT);
}