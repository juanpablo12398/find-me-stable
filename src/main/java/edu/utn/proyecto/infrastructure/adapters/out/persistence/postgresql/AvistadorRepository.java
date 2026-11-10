package edu.utn.proyecto.infrastructure.adapters.out.persistence.postgresql;

import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.AvistadorEntity;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers.AvistadorPersistenceMapper;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.jpa.AvistadorJpaRepository;
import edu.utn.proyecto.security.encrypt.BlindIndexer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AvistadorRepository implements IRepoDeAvistadores {

    private final AvistadorJpaRepository jpa;
    private final AvistadorPersistenceMapper mapper;
    private final BlindIndexer blindIndexer;

    public AvistadorRepository(AvistadorJpaRepository jpa,
                                    AvistadorPersistenceMapper mapper,
                                    BlindIndexer blindIndexer) {
        this.jpa = jpa;
        this.mapper = mapper;
        this.blindIndexer = blindIndexer;
    }

    @Override
    public Avistador save(Avistador avistador) {
        AvistadorEntity entity = mapper.domainToEntity(avistador);

        // Generar blind indexes antes de persistir
        entity.setDniHmac(blindIndexer.index(avistador.getDni()));
        if (avistador.getEmail() != null) {
            entity.setEmailHmac(blindIndexer.index(avistador.getEmail()));
        }

        AvistadorEntity saved = jpa.save(entity);
        return mapper.entityToDomain(saved);
    }

    @Override
    public Optional<Avistador> findByDni(String dni) {
        String hmac = blindIndexer.index(dni);
        return jpa.findByDniHmac(hmac).map(mapper::entityToDomain);
    }

    @Override
    public Optional<Avistador> findById(UUID id) {
        return jpa.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public List<Avistador> findByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpa.findAllById(ids).stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    public boolean existsByDni(String dni) {
        String hmac = blindIndexer.index(dni);
        return jpa.existsByDniHmac(hmac);
    }
}
