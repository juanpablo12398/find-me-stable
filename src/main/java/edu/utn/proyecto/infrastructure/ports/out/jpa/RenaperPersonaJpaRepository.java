package edu.utn.proyecto.infrastructure.ports.out.jpa;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RenaperPersonaJpaRepository extends JpaRepository<RenaperPersonaEntity, String> {
    Optional<RenaperPersonaEntity> findByDni(String dni);
}
