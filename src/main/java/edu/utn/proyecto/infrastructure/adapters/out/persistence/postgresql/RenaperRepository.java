package edu.utn.proyecto.infrastructure.adapters.out.persistence.postgresql;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import edu.utn.proyecto.infrastructure.ports.out.jpa.RenaperPersonaJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * LIMITACIÓN ACADÉMICA — RENAPER SIMULADO
 *
 * Esta implementación consulta una tabla local {@code renaper_personas} precargada
 * con datos de prueba. NO está conectada al servicio oficial del RENAPER argentino.
 *
 * En producción real, reemplazar esta clase por un cliente HTTP que consuma
 * la API oficial del RENAPER (Registro Nacional de las Personas) del
 * Ministerio del Interior de la República Argentina, previa gestión del
 * convenio institucional correspondiente.
 */
@Repository
public class RenaperRepository implements IRepoDeRenaper {
    private final RenaperPersonaJpaRepository jpa;
    public RenaperRepository(RenaperPersonaJpaRepository jpa) {
        this.jpa = jpa;
    }
    @Override
    public Optional<RenaperPersonaEntity> findByDni(String dni) {
        return jpa.findByDni(dni);
    }
}