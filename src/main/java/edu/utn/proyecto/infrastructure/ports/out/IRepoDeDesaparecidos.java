package edu.utn.proyecto.infrastructure.ports.out;

import edu.utn.proyecto.domain.model.concreta.Desaparecido;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRepoDeDesaparecidos {
    Desaparecido save(Desaparecido desaparecido);
    List<Desaparecido> getDesaparecidos();
    List<Desaparecido> buscar(String search, String estado);
    List<Desaparecido> buscarPaginado(String search, String estado, String sort, int size, int offset);
    long contar(String search, String estado);
    List<Desaparecido> buscarPorDniExacto(String dni, String estado);
    List<Desaparecido> findByIds(Collection<UUID> ids);
    Optional<Desaparecido> findById(UUID id);
    Optional<Desaparecido> findByDni(String dni);
    boolean existsByDni(String dni);
}
