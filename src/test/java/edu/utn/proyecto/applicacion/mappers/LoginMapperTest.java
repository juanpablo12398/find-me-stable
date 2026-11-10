package edu.utn.proyecto.applicacion.mappers;

import edu.utn.proyecto.common.normalize.DniNormalizer;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LoginMapperTest {

    @Mock
    private DniNormalizer dniNorm;

    @Mock
    private TextNormalizer txtNorm;

    @InjectMocks
    private LoginMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void normalizeRequestInPlace_success() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setDni(" 12.345.678 ");
        request.setEmail(" TEST@EXAMPLE.COM ");

        when(dniNorm.normalize(" 12.345.678 ")).thenReturn("12345678");
        when(txtNorm.normalize(" TEST@EXAMPLE.COM ")).thenReturn("TEST@EXAMPLE.COM");

        mapper.normalizeRequestInPlace(request);

        assertThat(request.getDni()).isEqualTo("12345678");
        assertThat(request.getEmail()).isEqualTo("TEST@EXAMPLE.COM");
    }

    @Test
    void normalizeRequestInPlace_withNullEmail() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setDni(" 12.345.678 ");
        request.setEmail(null);

        when(dniNorm.normalize(" 12.345.678 ")).thenReturn("12345678");

        mapper.normalizeRequestInPlace(request);

        assertThat(request.getDni()).isEqualTo("12345678");
        assertThat(request.getEmail()).isEmpty();
    }

    @Test
    void fromLoginRequestToSession_mapsCorrectly() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setDni("12345678");
        request.setEmail("test@test.com");

        UUID avistadorId = UUID.randomUUID();
        String nombre = "Juan Perez";
        String rol = "ADMINISTRADOR";

        SessionUserDTO session = mapper.fromLoginRequestToSession(request, avistadorId, nombre, rol);

        assertThat(session).isNotNull();
        assertThat(session.getDni()).isEqualTo("12345678");
        assertThat(session.getEmail()).isEqualTo("test@test.com");
        assertThat(session.getId()).isEqualTo(avistadorId);
        assertThat(session.getNombre()).isEqualTo(nombre);
        assertThat(session.getRol()).isEqualTo(rol);
    }
}
