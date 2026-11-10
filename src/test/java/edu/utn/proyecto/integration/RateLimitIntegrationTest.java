package edu.utn.proyecto.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "security.rate-limit.login.max-requests=2",
        "security.rate-limit.login.window-seconds=600",
        "security.rate-limit.registro.max-requests=2",
        "security.rate-limit.registro.window-seconds=3600"
})
class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_superaLimite_retorna429ConKeyYRetryAfter() throws Exception {
        String ip = "198.51.100.77";
        String body = "{\"dni\":\"00000001\",\"email\":\"test@test.com\"}";

        // Primer intento: pasa el filtro (puede fallar por credenciales, pero no por rate limit)
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Forwarded-For", ip))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isNotEqualTo(429));

        // Segundo intento: pasa el filtro
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Forwarded-For", ip))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isNotEqualTo(429));

        // Tercer intento: supera el límite → 429 con Retry-After y key de contrato
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Forwarded-For", ip))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.key").value("rate.limit.exceeded"));
    }

    @Test
    void registro_superaLimite_retorna429() throws Exception {
        String ip = "198.51.100.88";
        String body = "{\"dni\":\"00000002\",\"nombre\":\"Test\",\"apellido\":\"User\",\"edad\":25,\"email\":\"reg@test.com\"}";

        // Primer y segundo intento: pasan el filtro
        mockMvc.perform(post("/api/v1/avistadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Forwarded-For", ip))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isNotEqualTo(429));

        mockMvc.perform(post("/api/v1/avistadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Forwarded-For", ip))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isNotEqualTo(429));

        // Tercer intento: 429 con Retry-After
        mockMvc.perform(post("/api/v1/avistadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Forwarded-For", ip))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.key").value("rate.limit.exceeded"));
    }
}
