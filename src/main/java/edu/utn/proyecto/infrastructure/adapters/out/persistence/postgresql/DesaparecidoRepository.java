package edu.utn.proyecto.infrastructure.adapters.out.persistence.postgresql;

import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.DesaparecidoEntity;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers.DesaparecidoPersistenceMapper;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import edu.utn.proyecto.infrastructure.ports.out.jpa.DesaparecidoJpaRepository;
import edu.utn.proyecto.security.encrypt.BlindIndexer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DesaparecidoRepository implements IRepoDeDesaparecidos {

    private final DesaparecidoJpaRepository jpa;
    private final DesaparecidoPersistenceMapper mapper;
    private final BlindIndexer blindIndexer;

    public DesaparecidoRepository(DesaparecidoJpaRepository jpa,
                                      DesaparecidoPersistenceMapper mapper,
                                      BlindIndexer blindIndexer) {
        this.jpa = jpa;
        this.mapper = mapper;
        this.blindIndexer = blindIndexer;
    }

    @Override
    public Desaparecido save(Desaparecido desaparecido) {
        DesaparecidoEntity entity = mapper.domainToEntity(desaparecido);

        // Generar blind index para DNI antes de persistir
        if (desaparecido.getDni() != null) {
            entity.setDniHmac(blindIndexer.index(desaparecido.getDni()));
        }
        // Generar blind index para email de contacto familiar (si está presente)
        if (desaparecido.getEmailContactoFamiliar() != null) {
            entity.setEmailContactoFamiliarHmac(blindIndexer.index(desaparecido.getEmailContactoFamiliar()));
        }

        DesaparecidoEntity saved = jpa.save(entity);
        return mapper.entityToDomain(saved);
    }

    @Override
    public List<Desaparecido> getDesaparecidos() {
        return jpa.findAll().stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<Desaparecido> findByDni(String dni) {
        String hmac = blindIndexer.index(dni);
        return jpa.findByDniHmac(hmac).map(mapper::entityToDomain);
    }

    @Override
    public boolean existsByDni(String dni) {
        String hmac = blindIndexer.index(dni);
        return jpa.existsByDniHmac(hmac);
    }

    @Override
    public List<Desaparecido> buscar(String search, String estado) {
        return jpa.buscar(search, estado).stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Desaparecido> buscarPaginado(String search, String estado, String sort, int size, int offset) {
        return jpa.buscarPaginado(search, estado, sort, size, offset).stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    public long contar(String search, String estado) {
        Long total = jpa.contar(search, estado);
        return total != null ? total : 0L;
    }

    @Override
    public Optional<Desaparecido> findById(UUID id) {
        return jpa.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public List<Desaparecido> buscarPorDniExacto(String dni, String estado) {
        String hmac = blindIndexer.index(dni);
        return jpa.buscarPorDniExacto(hmac, estado).stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Desaparecido> findByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpa.findAllById(ids).stream()
                .map(mapper::entityToDomain)
                .toList();
    }

}
