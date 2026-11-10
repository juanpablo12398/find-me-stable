package edu.utn.proyecto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeRenaper;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.RenaperPersonaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import edu.utn.proyecto.domain.model.enums.EstadoDesaparecido;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ¡Esta es tu primera Prueba de Integración Real!
 * 
 * A diferencia de las pruebas unitarias que vimos antes (donde comprobamos qué
 * hace
 * cada tuerca por separado aislándola con `Mockito`), una prueba de Integración
 * en
 * Spring Boot se encarga de "Despertar" casi t0do el backend y hacerlo
 * funcionar junto.
 * 
 * Aquí vamos a:
 * 1. Levantar el contexto de Spring (Controladores, Filtros JWT, Casos de Uso,
 * Validadores).
 * 2. Usar MockMvc para simular el protocolo HTTP (Reemplaza a un Postman real).
 * 3. Enviar un JSON de solicitud.
 * 4. Demostrar cómo tu ecosistema (Validation, UseCase) responde en conjunto
 * sin
 * golpear directamente a una base de datos real (Mocking del repositorio para
 * evitar fallar por BD).
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Desactivamos los filtros de seguridad solo para probar el flujo REST simple
@ActiveProfiles("test")
class DesaparecidoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Con @MockBean le decimos a Spring que SI levante todos los servicios y
    // controladores reales,
    // pero que intercepte y finja la capa de base de datos para no requerir
    // conexión a PostgreSQL.
    @MockBean
    private IRepoDeDesaparecidos repoDeDesaparecidos;

    @MockBean
    private IRepoDeRenaper repoDeRenaper;

    @Test
    void crearDesaparecido_IntegradoCompleto_DesdeControllerHastaRepo() throws Exception {
        // 1. Preparación de los datos que enviará el supuesto Frontend
        DesaparecidoRequestDTO request = new DesaparecidoRequestDTO();
        request.setNombre("Carlos");
        request.setApellido("Vazquez");
        request.setEdad(35);
        request.setDni("35123456");
        request.setDescripcion("Integracion test descripcion");
        request.setContactoFamiliar("1154321098");
        request.setEmailContactoFamiliar("contacto@familia.com");
        request.setUltimaUbicacion("Cerca del obelisco");
        request.setFechaDesaparicion(LocalDateTime.now().minusDays(2));

        // 2. Preparación del "falso" guardado de Base de Datos.
        // Si t0do el circuito de validación, Mappers y Servicios funciona, llegará al
        // repositorio.
        Desaparecido entidadGuardada = new Desaparecido();
        entidadGuardada.setId(UUID.randomUUID());
        entidadGuardada.setNombre("Carlos");
        entidadGuardada.setEstado(EstadoDesaparecido.BUSCANDO);
        when(repoDeDesaparecidos.save(any())).thenReturn(entidadGuardada);

        RenaperPersonaEntity renaperMock = new RenaperPersonaEntity();
        renaperMock.setDni("35123456");
        when(repoDeRenaper.findByDni(any())).thenReturn(Optional.of(renaperMock));

        // 3. Ejecución y Variante de Comprobación: Lanzamos el Request HTTP
        mockMvc.perform(post("/api/desaparecidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // 4. Afirmaciones
                .andExpect(status().isCreated()); // Nuestro código espera retornar un 201 CREATED
    }

    @Test
    void crearDesaparecido_FallaValidacion_DescripcionCorta() throws Exception {
        DesaparecidoRequestDTO request = new DesaparecidoRequestDTO();
        request.setNombre("Carlos");
        request.setApellido("Vazquez");
        request.setEdad(35);
        request.setDni("35123456");
        request.setDescripcion("muy corta"); // Trigger DesaparecidoLengthRule
        request.setContactoFamiliar("1154321098");
        request.setUltimaUbicacion("Cerca del obelisco");
        request.setFechaDesaparicion(LocalDateTime.now().minusDays(2));

        RenaperPersonaEntity renaperMock = new RenaperPersonaEntity();
        renaperMock.setDni("35123456");
        when(repoDeRenaper.findByDni(any())).thenReturn(Optional.of(renaperMock));

        mockMvc.perform(post("/api/desaparecidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity()); // asumiendo validaciones en el UseCase o Controller
    }

    @Test
    void getAllDesaparecidos_RetornaLista() throws Exception {
        // En este mock el repositorio ya provee la entidad mapeada a dominio
        Desaparecido desaparecidoMock = new Desaparecido();
        desaparecidoMock.setId(UUID.randomUUID());
        desaparecidoMock.setNombre("Marta");
        desaparecidoMock.setApellido("Gomez");
        desaparecidoMock.setEstado(EstadoDesaparecido.BUSCANDO);

        when(repoDeDesaparecidos.buscar(any(), any())).thenReturn(Collections.singletonList(desaparecidoMock));

        mockMvc.perform(get("/api/v1/desaparecidos")
                .param("search", "Marta"))
                .andExpect(status().isOk());
    }
}
