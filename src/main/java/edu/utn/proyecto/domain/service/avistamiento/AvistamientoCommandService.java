package edu.utn.proyecto.domain.service.avistamiento;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.applicacion.mappers.AvistamientoMapper;
import edu.utn.proyecto.applicacion.validation.avistamiento.AvistamientoCreatePolicy;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.domain.service.abstraccion.IAvistamientoCommandService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.INotificadorAvistamiento;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistamientos;
import edu.utn.proyecto.security.SecurityContextHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvistamientoCommandService implements IAvistamientoCommandService {

    private final IRepoDeAvistamientos repoAvistamientos;
    private final AvistamientoMapper mapper;
    private final Validator<AvistamientoRequestDTO> createPolicy;
    private final SecurityContextHelper securityHelper;

    private final INotificadorAvistamiento notificador;

    public AvistamientoCommandService(
            IRepoDeAvistamientos repo,
            AvistamientoMapper mapper,
            AvistamientoCreatePolicy createPolicy,
            SecurityContextHelper securityHelper,
            INotificadorAvistamiento notificador) {
        this.repoAvistamientos = repo;
        this.mapper = mapper;
        this.createPolicy = createPolicy;
        this.securityHelper = securityHelper;
        this.notificador = notificador;
    }

    @Transactional
    public AvistamientoResponseDTO crearAvistamiento(AvistamientoRequestDTO dto) {
        mapper.normalizeRequestInPlace(dto);
        createPolicy.validate(dto);
        Avistamiento avistamiento = mapper.fromRequestToDomain(dto);
        if (!securityHelper.isAuthenticated()) {
            avistamiento.setPublico(true);
        }
        var saved = repoAvistamientos.save(avistamiento);
        AvistamientoResponseDTO responseDTO = mapper.fromDomainToResponse(saved);
        notificador.notificarNuevoAvistamiento(responseDTO);
        return responseDTO;
    }
}
