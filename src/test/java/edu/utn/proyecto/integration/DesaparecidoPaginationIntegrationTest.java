package edu.utn.proyecto.integration;

import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.model.enums.EstadoDesaparecido;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DesaparecidoPaginationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRepoDeDesaparecidos repoDeDesaparecidos;

    @MockBean
    private IRepoDeRenaper repoDeRenaper;

    @Test
    void getDesaparecidos_conPaginacion_retornaMaximoSize() throws Exception {
        when(repoDeDesaparecidos.buscarPaginado(anyString(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(buildDesaparecidos(5));
        when(repoDeDesaparecidos.contar(anyString(), any())).thenReturn(12L);

        mockMvc.perform(get("/api/v1/desaparecidos")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void getDesaparecidos_paginaMuyAlta_retornaListaVacia() throws Exception {
        when(repoDeDesaparecidos.buscarPaginado(anyString(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());
        when(repoDeDesaparecidos.contar(anyString(), any())).thenReturn(10L);

        mockMvc.perform(get("/api/v1/desaparecidos")
                        .param("page", "999")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    void getDesaparecidos_filtrosCombinados_retornaResultadoSinError() throws Exception {
        when(repoDeDesaparecidos.buscarPaginado(anyString(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(buildDesaparecidos(3));
        when(repoDeDesaparecidos.contar(anyString(), any())).thenReturn(3L);

        mockMvc.perform(get("/api/v1/desaparecidos")
                        .param("search", "Carlos")
                        .param("estado", "BUSCANDO")
                        .param("sort", "urgencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    private static List<Desaparecido> buildDesaparecidos(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Desaparecido d = new Desaparecido(
                            "Carlos" + i,
                            "Vazquez" + i,
                            String.format("35%06d", i),
                            "Descripcion de prueba suficientemente larga " + i,
                            null,
                            LocalDateTime.now().minusDays(i + 1L),
                            EstadoDesaparecido.BUSCANDO,
                            "contacto@test.com",
                            "Calle " + i + ", CABA");
                    d.setId(UUID.randomUUID());
                    return d;
                })
                .toList();
    }
}
