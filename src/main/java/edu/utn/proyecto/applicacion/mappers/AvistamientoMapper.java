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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AvistamientoMapper {

    private final TextNormalizer txtNorm;
    private final DateTimeNormalizer dateNorm;
    private final FileUrlHelper fileUrlHelper;
    private final HtmlSanitizer htmlSanitizer;

    public AvistamientoMapper(TextNormalizer txtNorm,
                              DateTimeNormalizer dateNorm,
                              FileUrlHelper fileUrlHelper,
                              HtmlSanitizer htmlSanitizer) {
        this.txtNorm = txtNorm;
        this.dateNorm = dateNorm;
        this.fileUrlHelper = fileUrlHelper;
        this.htmlSanitizer = htmlSanitizer;
    }

    public void normalizeRequestInPlace(AvistamientoRequestDTO dto) {
        dto.setDescripcion(txtNorm.sentenceCase(htmlSanitizer.sanitize(dto.getDescripcion())));
    }

    public Avistamiento fromRequestToDomain(AvistamientoRequestDTO dto) {
        Avistamiento a = new Avistamiento(
                UUID.fromString(dto.getAvistadorId()),
                UUID.fromString(dto.getDesaparecidoId()),
                dto.getLatitud(),
                dto.getLongitud(),
                dto.getDescripcion(),
                dto.getFechaAvistamiento()
        );
        a.setFotoFileId(dto.getFotoFileId());
        a.setPublico(dto.getPublico() != null ? dto.getPublico() : true);
        return a;
    }

    public AvistamientoFrontDTO fromDomainToFrontBasic(Avistamiento a) {
        AvistamientoFrontDTO dto = new AvistamientoFrontDTO();
        dto.setId(a.getId());
        dto.setLatitud(a.getLatitud());
        dto.setLongitud(a.getLongitud());
        dto.setFechaFormateada(dateNorm.formatForDisplay(a.getFechaHora()));
        dto.setDescripcion(a.getDescripcion());
        dto.setFotoUrl(fileUrlHelper.toUrl(a.getFotoFileId()));
        dto.setVerificado(a.getVerificado());
        return dto;
    }

    public void enrichFrontDTO(AvistamientoFrontDTO front,
                               Desaparecido desaparecido,
                               Avistador avistador,
                               String nivelUrgencia) {
        front.setDesaparecidoId(desaparecido.getId());
        front.setDesaparecidoNombre(desaparecido.getNombre());
        front.setDesaparecidoApellido(desaparecido.getApellido());
        front.setDesaparecidoFoto(fileUrlHelper.toUrl(desaparecido.getFotoFileId()));
        front.setAvistadorNombre(avistador.getNombre());
        front.setNivelUrgencia(nivelUrgencia);
    }

    public AvistamientoResponseDTO fromDomainToResponse(Avistamiento a) {
        AvistamientoResponseDTO dto = new AvistamientoResponseDTO();
        dto.setId(a.getId());
        dto.setAvistadorId(a.getAvistadorId());
        dto.setDesaparecidoId(a.getDesaparecidoId());
        dto.setLatitud(a.getLatitud());
        dto.setLongitud(a.getLongitud());
        dto.setFechaHora(a.getFechaHora());
        dto.setDescripcion(a.getDescripcion());
        dto.setFotoFileId(a.getFotoFileId());
        dto.setVerificado(a.getVerificado());
        dto.setPublico(a.getPublico());
        dto.setCreadoEn(a.getCreadoEn());
        return dto;
    }

    public List<AvistamientoResponseDTO> fromDomainListToResponseList(List<Avistamiento> lista) {
        return lista.stream().map(this::fromDomainToResponse).collect(Collectors.toList());
    }

    public AvistamientoFrontDTO fromResponseToFront(AvistamientoResponseDTO res) {
        AvistamientoFrontDTO dto = new AvistamientoFrontDTO();
        dto.setId(res.getId());
        dto.setLatitud(res.getLatitud());
        dto.setLongitud(res.getLongitud());
        dto.setFechaFormateada(dateNorm.formatForDisplay(res.getFechaHora()));
        dto.setDescripcion(res.getDescripcion());
        dto.setFotoUrl(fileUrlHelper.toUrl(res.getFotoFileId()));
        dto.setVerificado(res.getVerificado());
        return dto;
    }

    public List<AvistamientoFrontDTO> fromResponseToFrontList(List<AvistamientoResponseDTO> lista) {
        return lista.stream().map(this::fromResponseToFront).collect(Collectors.toList());
    }
}
