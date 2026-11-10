package edu.utn.proyecto.infrastructure.ports.out.jpa;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.AvistadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AvistadorJpaRepository extends JpaRepository<AvistadorEntity, UUID> {
    Optional<AvistadorEntity> findByDniHmac(String dniHmac);
    Optional<AvistadorEntity> findByEmailHmac(String emailHmac);
    boolean existsByDniHmac(String dniHmac);
    Optional<AvistadorEntity> findById(UUID uuid);
}