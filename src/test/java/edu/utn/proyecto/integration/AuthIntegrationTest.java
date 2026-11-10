package edu.utn.proyecto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utn.proyecto.applicacion.usecase.auth.GetCurrentUserUseCase;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IRepoDeAvistadores repoDeAvistadores;

    @MockBean
    private IRepoDeRenaper repoDeRenaper;

    @MockBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @Test
    void loginAvistador_ConCredencialesValidas_RetornaCookieJWT() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@email.com");
        request.setDni("12345678");

        RenaperPersonaEntity renaperMock = new RenaperPersonaEntity();
        renaperMock.setDni("12345678");
        when(repoDeRenaper.findByDni("12345678")).thenReturn(Optional.of(renaperMock));

        Avistador avistadorMock = new Avistador();
        avistadorMock.setId(UUID.randomUUID());
        avistadorMock.setEmail("test@email.com");
        avistadorMock.setDni("12345678");
        avistadorMock.setNombre("Test Nombre");
        avistadorMock.setRol(Rol.USUARIO);

        when(repoDeAvistadores.findByDni("12345678")).thenReturn(Optional.of(avistadorMock));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("FM_TOKEN"));
    }

    @Test
    void loginAvistador_ConUsuarioNoRegistrado_Retorna404ConKeyContrato() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@email.com");
        request.setDni("87654321");

        RenaperPersonaEntity renaperMock = new RenaperPersonaEntity();
        renaperMock.setDni("87654321");
        when(repoDeRenaper.findByDni("87654321")).thenReturn(Optional.of(renaperMock));
        when(repoDeAvistadores.findByDni("87654321")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.key").value("auth.login.user.notfound"));
    }

    @Test
    void loginAvistador_EmailNoCoincide_Retorna422ConKeyContrato() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("otro@email.com");
        request.setDni("33334444");

        RenaperPersonaEntity renaperMock = new RenaperPersonaEntity();
        renaperMock.setDni("33334444");
        when(repoDeRenaper.findByDni("33334444")).thenReturn(Optional.of(renaperMock));

        Avistador avistadorMock = new Avistador();
        avistadorMock.setId(UUID.randomUUID());
        avistadorMock.setEmail("test@email.com");
        avistadorMock.setDni("33334444");
        avistadorMock.setNombre("Test Nombre");
        avistadorMock.setRol(Rol.USUARIO);
        when(repoDeAvistadores.findByDni("33334444")).thenReturn(Optional.of(avistadorMock));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.key").value("auth.login.email.mismatch"));
    }

    @Test
    void logout_CualquierEstado_RetornaNoContentYBorraCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("FM_TOKEN", 0));
    }

    @Test
    void getCurrentUser_ConTokenValido_RetornaUsuario() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk());
    }
}
