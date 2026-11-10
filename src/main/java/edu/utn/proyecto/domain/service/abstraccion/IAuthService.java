package edu.utn.proyecto.domain.service.abstraccion;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.TokenRequestDTO;

public interface IAuthService {
    SessionUserDTO login(LoginRequestDTO dto);
    SessionUserDTO currentUser(TokenRequestDTO dto);
}
