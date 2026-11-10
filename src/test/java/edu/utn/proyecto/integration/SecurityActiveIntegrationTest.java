package edu.utn.proyecto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class SecurityActiveIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IRepoDeAvistadores repoDeAvistadores;

    @MockBean
    private IRepoDeRenaper repoDeRenaper;

    @Test
    void authLogin_conFiltrosActivos_siguePermitido() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("security@test.com");
        request.setDni("12345678");

        RenaperPersonaEntity renaperMock = new RenaperPersonaEntity();
        renaperMock.setDni("12345678");
        when(repoDeRenaper.findByDni("12345678")).thenReturn(Optional.of(renaperMock));

        Avistador avistadorMock = new Avistador();
        avistadorMock.setId(UUID.randomUUID());
        avistadorMock.setEmail("security@test.com");
        avistadorMock.setDni("12345678");
        avistadorMock.setNombre("Security User");
        avistadorMock.setRol(Rol.USUARIO);
        when(repoDeAvistadores.findByDni("12345678")).thenReturn(Optional.of(avistadorMock));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("FM_TOKEN"));
    }

    @Test
    void filesUpload_sinSesion_debeQuedarProtegido() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(file)
                        .param("personId", UUID.randomUUID().toString())
                        .param("entityType", "DESAPARECIDO")
                        .param("category", "PHOTO"))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isIn(401, 403));
    }

    @Test
    void endpointPrivado_sinSesion_debeQuedarProtegido() throws Exception {
        mockMvc.perform(post("/api/v1/desaparecidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isIn(401, 403));
    }

    @Test
    void filesDownload_publico_noDebeSerBloqueadoPorSeguridad() throws Exception {
        mockMvc.perform(get("/api/v1/files/{fileId}", UUID.randomUUID()))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isNotIn(401, 403));
    }
}
