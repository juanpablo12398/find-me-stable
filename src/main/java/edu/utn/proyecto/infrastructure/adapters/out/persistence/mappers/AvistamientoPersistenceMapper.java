package edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers;

import edu.utn.proyecto.infrastructure.normalize.GeometryNormalizer;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.AvistamientoEntity;
import org.springframework.stereotype.Component;

@Component
public class AvistamientoPersistenceMapper {

    private final GeometryNormalizer geomNorm;

    public AvistamientoPersistenceMapper(GeometryNormalizer geomNorm) {
        this.geomNorm = geomNorm;
    }

    public Avistamiento toDomain(AvistamientoEntity entity) {
        Avistamiento domain = new Avistamiento();
        domain.setId(entity.getId());
        domain.setAvistadorId(entity.getAvistadorId());
        domain.setDesaparecidoId(entity.getDesaparecidoId());
        domain.setLatitud(geomNorm.fromPointToLatitud(entity.getUbicacion()));
        domain.setLongitud(geomNorm.fromPointToLongitud(entity.getUbicacion()));
        domain.setFechaHora(entity.getFechaHora());
        domain.setDescripcion(entity.getDescripcion());
        domain.setFotoFileId(entity.getFotoFileId());
        domain.setVerificado(entity.getVerificado());
        domain.setPublico(entity.getPublico());
        domain.setCreadoEn(entity.getCreadoEn());
        return domain;
    }

    public AvistamientoEntity toEntity(Avistamiento domain) {
        AvistamientoEntity entity = new AvistamientoEntity();
        entity.setId(domain.getId());
        entity.setAvistadorId(domain.getAvistadorId());
        entity.setDesaparecidoId(domain.getDesaparecidoId());
        entity.setUbicacion(geomNorm.toPoint(domain.getLatitud(), domain.getLongitud()));
        entity.setFechaHora(domain.getFechaHora());
        entity.setDescripcion(domain.getDescripcion());
        entity.setFotoFileId(domain.getFotoFileId());
        entity.setVerificado(domain.getVerificado());
        entity.setPublico(domain.getPublico());
        entity.setCreadoEn(domain.getCreadoEn());
        return entity;
    }
}