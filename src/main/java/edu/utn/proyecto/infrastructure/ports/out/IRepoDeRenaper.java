package edu.utn.proyecto.infrastructure.ports.out;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import java.util.Optional;

public interface IRepoDeRenaper {
    Optional<RenaperPersonaEntity> findByDni(String dni);
}