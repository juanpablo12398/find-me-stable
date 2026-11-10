package edu.utn.proyecto.applicacion.mappers;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.common.normalize.DateTimeNormalizer;
import edu.utn.proyecto.common.normalize.DniNormalizer;
import edu.utn.proyecto.common.normalize.FileUrlHelper;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.common.security.HtmlSanitizer;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.model.enums.EstadoDesaparecido;
import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesaparecidoMapperTest {

    @Mock
    private TextNormalizer txtNorm;

    @Mock
    private DniNormalizer dniNorm;

    @Mock
    private DateTimeNormalizer dateNorm;

    @Mock
    private FileUrlHelper fileUrlHelper;

    @Mock
    private HtmlSanitizer htmlSanitizer;

    @InjectMocks
    private DesaparecidoMapper mapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void debeNormalizarRequestInPlace() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO("juan", "perez", 30, " 12345678 ", null, "desc", null,
                "contacto", "ubicacion");

        when(txtNorm.upperNoAccents("juan")).thenReturn("JUAN");
        when(txtNorm.upperNoAccents("perez")).thenReturn("PEREZ");
        when(dniNorm.normalize(" 12345678 ")).thenReturn("12345678");
        when(txtNorm.normalize("desc")).thenReturn("desc");
        when(htmlSanitizer.sanitize("desc")).thenReturn("desc");
        when(txtNorm.normalize("contacto")).thenReturn("contacto");
        when(htmlSanitizer.sanitize("contacto")).thenReturn("contacto");
        when(txtNorm.normalize((String) null)).thenReturn(null);
        when(htmlSanitizer.sanitize(null)).thenReturn(null);
        when(txtNorm.normalize("ubicacion")).thenReturn("ubicacion");
        when(htmlSanitizer.sanitize("ubicacion")).thenReturn("ubicacion");

        mapper.normalizeRequestInPlace(dto);

        assertThat(dto.getNombre()).isEqualTo("JUAN");
        assertThat(dto.getApellido()).isEqualTo("PEREZ");
        assertThat(dto.getDni()).isEqualTo("12345678");
    }

    @Test
    void debeMapearDeRequestADomain() {
        DesaparecidoRequestDTO dto = new DesaparecidoRequestDTO("JUAN", "PEREZ", 30, "12345678", null, "desc", null,
                "contacto", "ubicacion");
        Desaparecido result = mapper.fromRequestToDomain(dto);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("JUAN");
        assertThat(result.getDni()).isEqualTo("12345678");
    }

    @Test
    void debeMapearDeDomainAResponse() {
        Desaparecido d = new Desaparecido("JUAN", "PEREZ", "12345678", "desc", null, LocalDateTime.now(),
                EstadoDesaparecido.BUSCANDO, "contacto", "ubicacion");
        d.setId(UUID.randomUUID());

        DesaparecidoResponseDTO result = mapper.fromDomainToResponse(d);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("JUAN");
        assertThat(result.getEstado()).isEqualTo("BUSCANDO");
    }

    @Test
    void debeMapearDeResponseAFront() {
        DesaparecidoResponseDTO responseDTO = new DesaparecidoResponseDTO(UUID.randomUUID(), "JUAN", "PEREZ",
                "encryptedDNI", LocalDateTime.now(), "desc", UUID.randomUUID(), "BUSCANDO", "contacto", "ubicacion");

        when(dateNorm.formatForDisplay(any())).thenReturn("1 Enero 2025");
        when(txtNorm.upperNoAccentsAllCaps(NivelUrgencia.ALTO)).thenReturn("ALTO");
        when(txtNorm.lowerAllCaps("BUSCANDO")).thenReturn("buscando");
        when(fileUrlHelper.toUrl(any())).thenReturn("http://path/to/img");

        DesaparecidoFrontDTO result = mapper.fromResponseToFront(responseDTO, NivelUrgencia.ALTO);

        assertThat(result).isNotNull();
        assertThat(result.getDni()).isNull(); // Should be null by privacy policy
        assertThat(result.getNivelUrgencia()).isEqualTo("ALTO");
        assertThat(result.getEstado()).isEqualTo("buscando");
        assertThat(result.getFotoUrl()).isEqualTo("http://path/to/img");
    }
}
