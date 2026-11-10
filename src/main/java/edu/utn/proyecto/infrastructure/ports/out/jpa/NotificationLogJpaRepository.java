package edu.utn.proyecto.infrastructure.ports.out.jpa;

import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogEntity, UUID> {
}
