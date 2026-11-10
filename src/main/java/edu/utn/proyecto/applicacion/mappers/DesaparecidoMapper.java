package edu.utn.proyecto.applicacion.mappers;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.common.normalize.DateTimeNormalizer;
import edu.utn.proyecto.common.normalize.DniNormalizer;
import edu.utn.proyecto.common.normalize.FileUrlHelper;
import edu.utn.proyecto.common.normalize.TextNormalizer;
import edu.utn.proyecto.common.security.HtmlSanitizer;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.domain.model.enums.NivelUrgencia;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DesaparecidoMapper {

    private final TextNormalizer txtNorm;
    private final DniNormalizer dniNorm;
    private final DateTimeNormalizer dateNorm;
    private final FileUrlHelper fileUrlHelper;
    private final HtmlSanitizer htmlSanitizer;

    public DesaparecidoMapper(TextNormalizer txtNorm,
            DniNormalizer dniNorm,
            DateTimeNormalizer dateNorm,
            FileUrlHelper fileUrlHelper,
            HtmlSanitizer htmlSanitizer) {
        this.txtNorm = txtNorm;
        this.dniNorm = dniNorm;
        this.dateNorm = dateNorm;
        this.fileUrlHelper = fileUrlHelper;
        this.htmlSanitizer = htmlSanitizer;
    }

    public void normalizeRequestInPlace(DesaparecidoRequestDTO dto) {
        dto.setNombre(txtNorm.upperNoAccents(dto.getNombre()));
        dto.setApellido(txtNorm.upperNoAccents(dto.getApellido()));
        dto.setDni(dniNorm.normalize(dto.getDni()));
        dto.setDescripcion(htmlSanitizer.sanitize(txtNorm.normalize(dto.getDescripcion())));
        dto.setContactoFamiliar(htmlSanitizer.sanitize(txtNorm.normalize(dto.getContactoFamiliar())));
        dto.setEmailContactoFamiliar(htmlSanitizer.sanitize(txtNorm.normalize(dto.getEmailContactoFamiliar())));
        dto.setUltimaUbicacion(htmlSanitizer.sanitize(txtNorm.normalize(dto.getUltimaUbicacion())));
    }

    public Desaparecido fromRequestToDomain(DesaparecidoRequestDTO dto) {
        Desaparecido d = new Desaparecido(
                dto.getNombre(),
                dto.getApellido(),
                dto.getDni(),
                dto.getDescripcion(),
                dto.getFotoFileId(),
                dto.getFechaDesaparicion(),
                null,
                dto.getContactoFamiliar(),
                dto.getUltimaUbicacion());
        d.setEmailContactoFamiliar(dto.getEmailContactoFamiliar());
        return d;
    }

    public List<Desaparecido> fromRequestToDomain(List<DesaparecidoRequestDTO> dtos) {
        return dtos.stream().map(this::fromRequestToDomain).collect(Collectors.toList());
    }

    public DesaparecidoResponseDTO fromDomainToResponse(Desaparecido d) {
        DesaparecidoResponseDTO dto = new DesaparecidoResponseDTO(
                d.getId(),
                d.getNombre(),
                d.getApellido(),
                d.getDni(),
                d.getFechaDesaparicion(),
                d.getDescripcion(),
                d.getFotoFileId(),
                d.getEstado().name(),
                d.getContactoFamiliar(),
                d.getUltimaUbicacion());
        dto.setEmailContactoFamiliar(d.getEmailContactoFamiliar());
        return dto;
    }

    public List<DesaparecidoResponseDTO> fromDomainToResponse(List<Desaparecido> lista) {
        return lista.stream().map(this::fromDomainToResponse).collect(Collectors.toList());
    }

    public DesaparecidoFrontDTO fromResponseToFront(DesaparecidoResponseDTO dto, NivelUrgencia nivelUrgencia) {
        String fechaFormateada = dateNorm.formatForDisplay(dto.getFechaDesaparicion());
        String nivelUrgenciaFormateado = txtNorm.upperNoAccentsAllCaps(nivelUrgencia);
        String estadoFormateado = txtNorm.lowerAllCaps(dto.getEstado());

        return new DesaparecidoFrontDTO(
                dto.getId(),
                dto.getNombre(),
                dto.getApellido(),
                null,
                dto.getDescripcion(),
                fileUrlHelper.toUrl(dto.getFotoFileId()),
                fechaFormateada,
                estadoFormateado,
                dto.getContactoFamiliar(),
                dto.getUltimaUbicacion(),
                nivelUrgenciaFormateado,
                nivelUrgencia.getPrioridad());
    }

    public List<DesaparecidoFrontDTO> fromResponseListToFrontList(
            List<DesaparecidoResponseDTO> lista,
            java.util.function.Function<DesaparecidoResponseDTO, NivelUrgencia> urgenciaCalculator) {
        return lista.stream()
                .map(dto -> fromResponseToFront(dto, urgenciaCalculator.apply(dto)))
                .collect(Collectors.toList());
    }
}
