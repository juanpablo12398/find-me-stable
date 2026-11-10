package edu.utn.proyecto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeAvistadores;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AvistadorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IRepoDeAvistadores repoDeAvistadores;

    @MockBean
    private IRepoDeRenaper repoDeRenaper;

    @Test
    void crearAvistador_Valido_Devuelve201() throws Exception {
        AvistadorRequestDTO request = new AvistadorRequestDTO();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setDni("12345678");
        request.setEmail("juanperez@example.com");
        request.setEdad(30);
        request.setDireccion("Calle Falsa 123");

        RenaperPersonaEntity renaperMock = new RenaperPersonaEntity();
        renaperMock.setDni("12345678");
        renaperMock.setNombre("Juan");
        renaperMock.setApellido("Perez");
        when(repoDeRenaper.findByDni(any())).thenReturn(Optional.of(renaperMock));

        Avistador savedMock = new Avistador();
        savedMock.setId(UUID.randomUUID());
        savedMock.setNombre("Juan");
        savedMock.setApellido("Perez");
        when(repoDeAvistadores.save(any())).thenReturn(savedMock);

        mockMvc.perform(post("/api/avistadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

