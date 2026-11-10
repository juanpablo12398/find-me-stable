package edu.utn.proyecto.domain.service.auth;
import edu.utn.proyecto.applicacion.mappers.LoginMapper;
import edu.utn.proyecto.applicacion.mappers.TokenMapper;
import edu.utn.proyecto.applicacion.validation.auth.LoginPolicy;
import edu.utn.proyecto.common.validation.abstraccion.Validator;
import edu.utn.proyecto.domain.service.abstraccion.IAuthService;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.utn.proyecto.applicacion.validation.auth.TokenPolicy;

@Service
public class AuthService implements IAuthService {

    private final Validator<LoginRequestDTO> loginPolicy;
    private final TokenPolicy tokenPolicy;
    private final LoginMapper loginMapper;
    private final TokenMapper tokenMapper;
    private final IRepoDeRenaper renaper;
    private final IRepoDeAvistadores repoAvistadores;

    public AuthService(LoginPolicy loginPolicy,
                       TokenPolicy tokenPolicy,
                       LoginMapper loginMapper,
                       TokenMapper tokenMapper,
                       IRepoDeRenaper renaper,
                       IRepoDeAvistadores repoAvistadores) {
        this.loginPolicy     = loginPolicy;
        this.tokenPolicy     = tokenPolicy;
        this.loginMapper     = loginMapper;
        this.tokenMapper     = tokenMapper;
        this.renaper         = renaper;
        this.repoAvistadores = repoAvistadores;
    }

    @Transactional(readOnly = true)
    public SessionUserDTO login(LoginRequestDTO dto) {
        loginMapper.normalizeRequestInPlace(dto);
        loginPolicy.validate(dto);
        var avistador = repoAvistadores.findByDni(dto.getDni()).get();
        String resolvedNombre =
                (avistador.getNombre() != null && !avistador.getNombre().isBlank())
                        ? avistador.getNombre()
                        : renaper.findByDni(dto.getDni())
                        .map(RenaperPersonaEntity::getNombre)
                        .orElse(null);
        return loginMapper.fromLoginRequestToSession(dto, avistador.getId(), resolvedNombre, avistador.getRol().name());
    }

    @Transactional(readOnly = true)
    public SessionUserDTO currentUser(TokenRequestDTO dto) {
        tokenPolicy.validate(dto);
        return tokenMapper.toSessionUser(dto.getClaims());
    }
}