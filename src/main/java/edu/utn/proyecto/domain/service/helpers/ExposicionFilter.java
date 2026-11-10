package edu.utn.proyecto.domain.service.helpers;
import edu.utn.proyecto.common.normalize.ExposicionNormalizer;
import edu.utn.proyecto.domain.model.enums.NivelExposicion;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.security.SecurityContextHelper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class ExposicionFilter {

    private final SecurityContextHelper securityHelper;
    private final Map<NivelExposicion, Consumer<DesaparecidoFrontDTO>> desaparecidoFilters;
    private final Map<NivelExposicion, Consumer<AvistamientoFrontDTO>> avistamientoFilters;

    public ExposicionFilter(SecurityContextHelper securityHelper,
                            ExposicionNormalizer normalizer) {
        this.securityHelper = securityHelper;

        this.desaparecidoFilters = Map.of(
                NivelExposicion.PUBLICO, dto -> {
                    dto.setNombre(normalizer.inicializar(dto.getNombre()));
                    dto.setApellido(normalizer.inicializar(dto.getApellido()));
                    dto.setDni(null);
                    dto.setDescripcion(null);
                    dto.setFotoBloqueada(true);
                    dto.setContactoFamiliar(null);
                    dto.setUltimaUbicacion(normalizer.aproximarUbicacion(dto.getUltimaUbicacion()));
                },
                NivelExposicion.RESTRINGIDO, dto -> {
                    dto.setDni(null);
                    dto.setFotoBloqueada(false);
                    dto.setContactoFamiliar(null);
                },
                NivelExposicion.SECRETO, dto -> {
                    dto.setFotoBloqueada(false);
                }
        );

        this.avistamientoFilters = Map.of(
                NivelExposicion.PUBLICO, dto -> {
                    dto.setLatitud(normalizer.redondearCoordenada(dto.getLatitud(), 2));
                    dto.setLongitud(normalizer.redondearCoordenada(dto.getLongitud(), 2));
                    dto.setDescripcion(null);
                    dto.setFotoBloqueada(true);
                    dto.setAvistadorNombre(null);
                    dto.setDesaparecidoNombre(normalizer.inicializar(dto.getDesaparecidoNombre()));
                    dto.setDesaparecidoApellido(normalizer.inicializar(dto.getDesaparecidoApellido()));
                    dto.setDesaparecidoFotoBloqueada(true);
                },
                NivelExposicion.RESTRINGIDO, dto -> {
                    dto.setFotoBloqueada(false);
                    dto.setAvistadorNombre(null);
                    dto.setDesaparecidoFotoBloqueada(false);
                },
                NivelExposicion.SECRETO, dto -> {
                    dto.setFotoBloqueada(false);
                    dto.setDesaparecidoFotoBloqueada(false);
                }
        );
    }

    public List<DesaparecidoFrontDTO> filtrarDesaparecidos(List<DesaparecidoFrontDTO> lista) {
        Consumer<DesaparecidoFrontDTO> filtro = desaparecidoFilters.get(
                NivelExposicion.fromRol(securityHelper.getCurrentRol())
        );
        return lista.stream().peek(filtro).toList();
    }

    public List<AvistamientoFrontDTO> filtrarAvistamientos(List<AvistamientoFrontDTO> lista) {
        Consumer<AvistamientoFrontDTO> filtro = avistamientoFilters.get(
                NivelExposicion.fromRol(securityHelper.getCurrentRol())
        );
        return lista.stream().peek(filtro).toList();
    }
}