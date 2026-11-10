package edu.utn.proyecto.infrastructure.ports.out.jpa;

import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.DesaparecidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DesaparecidoJpaRepository extends JpaRepository<DesaparecidoEntity, UUID> {

    Optional<DesaparecidoEntity> findByDniHmac(String dniHmac);

    boolean existsByDniHmac(String dniHmac);

    @Query("""
    SELECT d FROM DesaparecidoEntity d
    WHERE (:estado IS NULL OR d.estado = :estado)
      AND (
            :search = ''
            OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.descripcion) LIKE LOWER(CONCAT('%', :search, '%'))
      )
""")
    List<DesaparecidoEntity> buscar(
            @Param("search") String search,
            @Param("estado") String estado);

    @Query("""
        SELECT d
        FROM DesaparecidoEntity d
        WHERE d.dniHmac = :dniHmac
          AND (:estado IS NULL OR d.estado = :estado)
    """)
    List<DesaparecidoEntity> buscarPorDniExacto(
            @Param("dniHmac") String dniHmac,
            @Param("estado") String estado
    );

    @Query(value = """
        SELECT d.*
        FROM desaparecidos d
        WHERE (:estado IS NULL OR d.estado = :estado)
          AND (
                :search = ''
                OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.descripcion) LIKE LOWER(CONCAT('%', :search, '%'))
          )
        ORDER BY
          CASE
            WHEN :sort = 'urgencia' THEN
              CASE
                WHEN d.fecha_desaparicion IS NULL THEN 3
                WHEN EXTRACT(EPOCH FROM (NOW() - d.fecha_desaparicion)) / 3600 <= 48 THEN 0
                WHEN EXTRACT(EPOCH FROM (NOW() - d.fecha_desaparicion)) / 3600 <= 168 THEN 1
                WHEN EXTRACT(EPOCH FROM (NOW() - d.fecha_desaparicion)) / 3600 <= 720 THEN 2
                ELSE 3
              END
            ELSE 0
          END ASC,
          CASE WHEN :sort = 'reciente' THEN d.fecha_desaparicion END DESC NULLS LAST,
          CASE WHEN :sort = 'antiguo' THEN d.fecha_desaparicion END ASC NULLS LAST,
          CASE WHEN :sort = 'nombre' THEN LOWER(d.nombre || ' ' || d.apellido) END ASC NULLS LAST,
          d.id ASC
        LIMIT :size OFFSET :offset
        """, nativeQuery = true)
    List<DesaparecidoEntity> buscarPaginado(
            @Param("search") String search,
            @Param("estado") String estado,
            @Param("sort") String sort,
            @Param("size") int size,
            @Param("offset") int offset);

    @Query(value = """
        SELECT COUNT(*)
        FROM desaparecidos d
        WHERE (:estado IS NULL OR d.estado = :estado)
          AND (
                :search = ''
                OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.descripcion) LIKE LOWER(CONCAT('%', :search, '%'))
          )
        """, nativeQuery = true)
    Long contar(
            @Param("search") String search,
            @Param("estado") String estado);
}
