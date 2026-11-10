package edu.utn.proyecto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AvistamientoIntegrationTest {

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
    void crearAvistamiento_IntegradoCompleto() throws Exception {
        UUID avistadorId = UUID.randomUUID();
        UUID desaparecidoId = UUID.randomUUID();

        AvistamientoRequestDTO request = new AvistamientoRequestDTO();
        request.setAvistadorId(avistadorId.toString());
        request.setDesaparecidoId(desaparecidoId.toString());
        request.setLatitud(-34.603684);
        request.setLongitud(-58.381555);
        request.setDescripcion("Lo vi pasar rápido cerca del parque, llevaba una campera roja.");

        // Mock repo dependencies for rules
        when(repoDeAvistadores.findById(avistadorId)).thenReturn(Optional.of(new Avistador()));
        when(repoDeDesaparecidos.findById(desaparecidoId)).thenReturn(Optional.of(new Desaparecido()));

        Avistamiento guardado = new Avistamiento();
        guardado.setId(UUID.randomUUID());
        guardado.setFechaHora(LocalDateTime.now());
        when(repoDeAvistamientos.save(any())).thenReturn(guardado);

        mockMvc.perform(post("/api/avistamientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllAvistamientos_RetornaLista() throws Exception {
        Avistamiento mockAvistamiento = new Avistamiento();
        mockAvistamiento.setId(UUID.randomUUID());
        mockAvistamiento.setFechaHora(LocalDateTime.now());

        org.springframework.data.domain.Page<Avistamiento> page = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(mockAvistamiento));
        when(repoDeAvistamientos.findPublicosPaginado(any())).thenReturn(page);

        mockMvc.perform(get("/api/avistamientos"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerParaMapa_RetornaLista() throws Exception {
        mockMvc.perform(get("/api/avistamientos/mapa"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorDesaparecido_RetornaLista() throws Exception {
        UUID desaparecidoId = UUID.randomUUID();
        mockMvc.perform(get("/api/avistamientos/desaparecido/{desaparecidoId}", desaparecidoId))
                .andExpect(status().isOk());
    }
}

