package edu.utn.proyecto.infrastructure.ports.out;

import edu.utn.proyecto.domain.model.concreta.Avistador;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRepoDeAvistadores {
    Avistador save(Avistador avistador);
    Optional<Avistador> findByDni(String dni);
    Optional<Avistador> findById(UUID id);
    List<Avistador> findByIds(Collection<UUID> ids);
    boolean existsByDni(String dni);
}
