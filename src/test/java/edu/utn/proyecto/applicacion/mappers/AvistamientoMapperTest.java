package edu.utn.proyecto.applicacion.mappers;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.common.normalize.DateTimeNormalizer;
import edu.utn.proyecto.common.normalize.FileUrlHelper;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.common.security.HtmlSanitizer;
import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
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
class AvistamientoMapperTest {

    @Mock
    private TextNormalizer txtNorm;

    @Mock
    private DateTimeNormalizer dateNorm;

    @Mock
    private FileUrlHelper fileUrlHelper;

    @Mock
    private HtmlSanitizer htmlSanitizer;

    @InjectMocks
    private AvistamientoMapper mapper;

    @Test
    void debeNormalizarRequestInPlace() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setDescripcion("vi a la persona en la plaza");

        when(htmlSanitizer.sanitize("vi a la persona en la plaza")).thenReturn("vi a la persona en la plaza");
        when(txtNorm.sentenceCase("vi a la persona en la plaza"))
                .thenReturn("Vi a la persona en la plaza.");

        mapper.normalizeRequestInPlace(dto);

        assertThat(dto.getDescripcion()).isEqualTo("Vi a la persona en la plaza.");
    }

    @Test
    void debeMapearDeRequestADomain() {
        UUID avistadorId = UUID.randomUUID();
        UUID desaparecidoId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setAvistadorId(avistadorId.toString());
        dto.setDesaparecidoId(desaparecidoId.toString());
        dto.setLatitud(-34.0);
        dto.setLongitud(-58.0);
        dto.setDescripcion("desc");
        dto.setFechaAvistamiento(fecha);

        Avistamiento result = mapper.fromRequestToDomain(dto);

        assertThat(result).isNotNull();
        assertThat(result.getAvistadorId()).isEqualTo(avistadorId);
        assertThat(result.getDesaparecidoId()).isEqualTo(desaparecidoId);
        assertThat(result.getLatitud()).isEqualTo(-34.0);
        assertThat(result.getPublico()).isTrue();
    }

    @Test
    void debeMapearDeDomainToResponse() {
        Avistamiento a = new Avistamiento();
        a.setId(UUID.randomUUID());
        a.setAvistadorId(UUID.randomUUID());
        a.setDesaparecidoId(UUID.randomUUID());
        a.setLatitud(-34.0);
        a.setLongitud(-58.0);
        a.setFechaHora(LocalDateTime.now());
        a.setDescripcion("desc");
        a.setFotoFileId(UUID.randomUUID());
        a.setCreadoEn(LocalDateTime.now());

        AvistamientoResponseDTO result = mapper.fromDomainToResponse(a);

        assertThat(result).isNotNull();
        assertThat(result.getLatitud()).isEqualTo(-34.0);
        assertThat(result.getDescripcion()).isEqualTo("desc");
        assertThat(result.getFotoFileId()).isEqualTo(a.getFotoFileId());
    }

    @Test
    void debeMapearDeDomainAFrontBasic() {
        Avistamiento a = new Avistamiento();
        a.setId(UUID.randomUUID());
        a.setLatitud(-34.0);
        a.setLongitud(-58.0);
        a.setFechaHora(LocalDateTime.now());
        a.setDescripcion("desc");
        a.setFotoFileId(UUID.randomUUID());

        when(dateNorm.formatForDisplay(any())).thenReturn("1 Enero 2025");
        when(fileUrlHelper.toUrl(any())).thenReturn("http://path/to/img");

        AvistamientoFrontDTO result = mapper.fromDomainToFrontBasic(a);

        assertThat(result).isNotNull();
        assertThat(result.getLatitud()).isEqualTo(-34.0);
        assertThat(result.getFechaFormateada()).isEqualTo("1 Enero 2025");
        assertThat(result.getFotoUrl()).isEqualTo("http://path/to/img");
    }

    @Test
    void debeEnriquecerFrontDto() {
        AvistamientoFrontDTO front = new AvistamientoFrontDTO();

        Desaparecido desaparecido = new Desaparecido();
        desaparecido.setId(UUID.randomUUID());
        desaparecido.setNombre("Ana");
        desaparecido.setApellido("Perez");
        UUID fotoId = UUID.randomUUID();
        desaparecido.setFotoFileId(fotoId);

        Avistador avistador = new Avistador();
        avistador.setNombre("Carlos");

        when(fileUrlHelper.toUrl(fotoId)).thenReturn("http://path/to/desaparecido");

        mapper.enrichFrontDTO(front, desaparecido, avistador, "ALTA");

        assertThat(front.getDesaparecidoId()).isEqualTo(desaparecido.getId());
        assertThat(front.getDesaparecidoNombre()).isEqualTo("Ana");
        assertThat(front.getDesaparecidoApellido()).isEqualTo("Perez");
        assertThat(front.getDesaparecidoFoto()).isEqualTo("http://path/to/desaparecido");
        assertThat(front.getAvistadorNombre()).isEqualTo("Carlos");
        assertThat(front.getNivelUrgencia()).isEqualTo("ALTA");
    }

    @Test
    void debeMapearDeResponseAFront() {
        AvistamientoResponseDTO res = new AvistamientoResponseDTO();
        res.setId(UUID.randomUUID());
        res.setLatitud(-34.0);
        res.setLongitud(-58.0);
        res.setFechaHora(LocalDateTime.now());
        res.setDescripcion("desc");
        res.setFotoFileId(UUID.randomUUID());

        when(dateNorm.formatForDisplay(any())).thenReturn("1 Enero 2025");
        when(fileUrlHelper.toUrl(any())).thenReturn("http://path/to/img");

        AvistamientoFrontDTO result = mapper.fromResponseToFront(res);

        assertThat(result).isNotNull();
        assertThat(result.getLatitud()).isEqualTo(-34.0);
        assertThat(result.getFechaFormateada()).isEqualTo("1 Enero 2025");
        assertThat(result.getFotoUrl()).isEqualTo("http://path/to/img");
    }

    @Test
    void debeMapearListas() {
        Avistamiento a = new Avistamiento();
        a.setId(UUID.randomUUID());

        List<AvistamientoResponseDTO> responses = mapper.fromDomainListToResponseList(List.of(a));
        assertThat(responses).hasSize(1);
    }
}
