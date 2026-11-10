package edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.model.enums.EstadoDesaparecido;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.DesaparecidoEntity;
import org.springframework.stereotype.Component;

@Component
public class DesaparecidoPersistenceMapper {

    public DesaparecidoEntity domainToEntity(Desaparecido d) {
        DesaparecidoEntity e = new DesaparecidoEntity();
        e.setId(d.getId());
        e.setNombre(d.getNombre());
        e.setApellido(d.getApellido());
        e.setDni(d.getDni());
        e.setDescripcion(d.getDescripcion());
        e.setFotoFileId(d.getFotoFileId());
        e.setFechaDesaparicion(d.getFechaDesaparicion());
        e.setContactoFamiliar(d.getContactoFamiliar());
        e.setEmailContactoFamiliar(d.getEmailContactoFamiliar());
        e.setUltimaUbicacion(d.getUltimaUbicacion());
        e.setEstado(d.getEstado().name());
        return e;
    }

    public Desaparecido entityToDomain(DesaparecidoEntity e) {
        Desaparecido d = new Desaparecido(
                e.getNombre(),
                e.getApellido(),
                e.getDni(),
                e.getDescripcion(),
                e.getFotoFileId()
        );
        d.setId(e.getId());
        d.setFechaDesaparicion(e.getFechaDesaparicion());
        d.setContactoFamiliar(e.getContactoFamiliar());
        d.setEmailContactoFamiliar(e.getEmailContactoFamiliar());
        d.setUltimaUbicacion(e.getUltimaUbicacion());
        d.setEstado(EstadoDesaparecido.valueOf(e.getEstado()));
        return d;
    }
}