package edu.utn.proyecto.applicacion.mappers;

import edu.utn.proyecto.applicacion.dtos.AvistadorResponseDTO;
import edu.utn.proyecto.common.normalize.DniNormalizer;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvistadorMapperTest {

    @Mock
    private TextNormalizer txtNorm;

    @Mock
    private DniNormalizer dniNorm;

    @InjectMocks
    private AvistadorMapper mapper;

    @Test
    void debeNormalizarRequestInPlace() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setDni(" 12345678 ");
        dto.setNombre("juan");
        dto.setApellido("perez");
        dto.setDireccion("dir");
        dto.setEmail("email");
        dto.setTelefono("tel");

        when(txtNorm.upperNoAccents("juan")).thenReturn("JUAN");
        when(txtNorm.upperNoAccents("perez")).thenReturn("PEREZ");
        when(dniNorm.normalize(" 12345678 ")).thenReturn("12345678");
        when(txtNorm.normalize("dir")).thenReturn("dir");
        when(txtNorm.normalize("email")).thenReturn("email");
        when(txtNorm.normalize("tel")).thenReturn("tel");

        mapper.normalizeRequestInPlace(dto);

        assertThat(dto.getNombre()).isEqualTo("JUAN");
        assertThat(dto.getApellido()).isEqualTo("PEREZ");
        assertThat(dto.getDni()).isEqualTo("12345678");
    }

    @Test
    void debeMapearDeRequestADomain() {
        AvistadorRequestDTO dto = new AvistadorRequestDTO();
        dto.setDni("12345678");
        dto.setNombre("JUAN");
        dto.setApellido("PEREZ");
        dto.setEdad(30);

        Avistador result = mapper.fromRequestToDomain(dto);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("JUAN");
        assertThat(result.getDni()).isEqualTo("12345678");
        assertThat(result.getEdad()).isEqualTo(30);
        assertThat(result.getCreadoEn()).isNotNull();
    }

    @Test
    void debeMapearDeDomainAResponse() {
        Avistador d = new Avistador();
        d.setId(UUID.randomUUID());
        d.setDni("12345678");
        d.setNombre("JUAN");
        d.setApellido("PEREZ");
        d.setRol(Rol.USUARIO);
        d.setCreadoEn(LocalDateTime.now());

        AvistadorResponseDTO result = mapper.fromDomainToResponse(d);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("JUAN");
        assertThat(result.getRol()).isEqualTo("USUARIO");
        assertThat(result.getDni()).isEqualTo("12345678");
    }
}
