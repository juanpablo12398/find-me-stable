package edu.utn.proyecto.domain.service.avistador;
import edu.utn.proyecto.applicacion.dtos.AvistadorResponseDTO;
import edu.utn.proyecto.applicacion.mappers.AvistadorMapper;
import edu.utn.proyecto.applicacion.validation.avistador.AvistadorRegistrationPolicy;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.domain.service.abstraccion.IAvistadorService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvistadorService implements IAvistadorService {

    private final IRepoDeAvistadores repo;
    private final AvistadorMapper mapper;
    private final Validator<AvistadorRequestDTO> registrationPolicy;

    public AvistadorService(IRepoDeAvistadores repo,
                            AvistadorMapper mapper,
                            AvistadorRegistrationPolicy registrationPolicy) {
        this.repo = repo;
        this.mapper = mapper;
        this.registrationPolicy = registrationPolicy;
    }

    @Transactional
    public AvistadorResponseDTO registrar(AvistadorRequestDTO dto) {
        mapper.normalizeRequestInPlace(dto);
        registrationPolicy.validate(dto);
        var domain = mapper.fromRequestToDomain(dto);
        var saved  = repo.save(domain);
        return mapper.fromDomainToResponse(saved);
    }
}