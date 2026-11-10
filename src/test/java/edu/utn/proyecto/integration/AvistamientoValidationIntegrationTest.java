package edu.utn.proyecto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistamientos;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AvistamientoValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IRepoDeAvistamientos repoDeAvistamientos;

    @MockBean
    private IRepoDeDesaparecidos repoDeDesaparecidos;

    @MockBean
    private IRepoDeAvistadores repoDeAvistadores;

    @Test
    void crearAvistamiento_latitudInvalida_retorna422ConKeyEsperada() throws Exception {
        AvistamientoRequestDTO request = buildValidRequest();
        request.setLatitud(999.0);

        mockMvc.perform(post("/api/v1/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.key").value("avistamiento.coords.invalidas"));
    }

    @Test
    void crearAvistamiento_descripcionMuyCorta_retorna422ConKeyEsperada() throws Exception {
        AvistamientoRequestDTO request = buildValidRequest();
        request.setDescripcion("corta");

        mockMvc.perform(post("/api/v1/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.key").value("avistamiento.descripcion.corta"));
    }

    @Test
    void buscarEnPoligono_wktNoPoligono_retorna422ConKeyEsperada() throws Exception {
        mockMvc.perform(post("/api/v1/avistamientos/poligono")
                        .param("wkt", "POINT(1 1)"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.key").value("avistamiento.poligono.wkt.invalid"));
    }

    private static AvistamientoRequestDTO buildValidRequest() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setAvistadorId(UUID.randomUUID().toString());
        dto.setDesaparecidoId(UUID.randomUUID().toString());
        dto.setLatitud(-34.603684);
        dto.setLongitud(-58.381555);
        dto.setDescripcion("Lo vi caminar por la plaza y parecia desorientado.");
        dto.setPublico(true);
        return dto;
    }
}
