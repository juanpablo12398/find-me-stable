package edu.utn.proyecto.domain.service.helpers;

import edu.utn.proyecto.common.normalize.ExposicionNormalizer;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExposicionFilterTest {

    @Mock
    private SecurityContextHelper securityHelper;

    private ExposicionFilter exposicionFilter;

    @BeforeEach
    void setUp() {
        exposicionFilter = new ExposicionFilter(securityHelper, new ExposicionNormalizer());
    }

    @Test
    void rolNoRegistrado_aplicaMascaraPublica() {
        when(securityHelper.getCurrentRol()).thenReturn(Rol.NO_REGISTRADO);

        DesaparecidoFrontDTO desaparecido = buildDesaparecido();
        AvistamientoFrontDTO avistamiento = buildAvistamiento();

        DesaparecidoFrontDTO d = exposicionFilter.filtrarDesaparecidos(List.of(desaparecido)).get(0);
        AvistamientoFrontDTO a = exposicionFilter.filtrarAvistamientos(List.of(avistamiento)).get(0);

        assertThat(d.getNombre()).isEqualTo("C.");
        assertThat(d.getApellido()).isEqualTo("V.");
        assertThat(d.getDni()).isNull();
        assertThat(d.getFotoBloqueada()).isTrue();

        assertThat(a.getLatitud()).isEqualTo(-34.6);
        assertThat(a.getLongitud()).isEqualTo(-58.38);
        assertThat(a.getFotoBloqueada()).isTrue();
    }

    @Test
    void rolUsuario_muestraNombreCompletoOcultaDniYNoBloqueaFoto() {
        when(securityHelper.getCurrentRol()).thenReturn(Rol.USUARIO);

        DesaparecidoFrontDTO d = exposicionFilter.filtrarDesaparecidos(List.of(buildDesaparecido())).get(0);

        assertThat(d.getNombre()).isEqualTo("Carlos");
        assertThat(d.getApellido()).isEqualTo("Vazquez");
        assertThat(d.getDni()).isNull();
        assertThat(d.getFotoBloqueada()).isFalse();
    }

    @Test
    void rolAdministrador_muestraTodoSinBloqueos() {
        when(securityHelper.getCurrentRol()).thenReturn(Rol.ADMINISTRADOR);

        DesaparecidoFrontDTO d = exposicionFilter.filtrarDesaparecidos(List.of(buildDesaparecido())).get(0);
        AvistamientoFrontDTO a = exposicionFilter.filtrarAvistamientos(List.of(buildAvistamiento())).get(0);

        assertThat(d.getNombre()).isEqualTo("Carlos");
        assertThat(d.getDni()).isEqualTo("35123456");
        assertThat(d.getFotoBloqueada()).isFalse();

        assertThat(a.getDescripcion()).isEqualTo("Descripcion completa del avistamiento");
        assertThat(a.getAvistadorNombre()).isEqualTo("Juan Perez");
        assertThat(a.getFotoBloqueada()).isFalse();
        assertThat(a.getDesaparecidoFotoBloqueada()).isFalse();
    }

    private static DesaparecidoFrontDTO buildDesaparecido() {
        return new DesaparecidoFrontDTO(
                UUID.randomUUID(),
                "Carlos",
                "Vazquez",
                "35123456",
                "Descripcion extensa",
                "/api/files/test",
                "hoy",
                "buscando",
                "familia@test.com",
                "Calle 123, CABA",
                "ALTO",
                2
        );
    }

    private static AvistamientoFrontDTO buildAvistamiento() {
        AvistamientoFrontDTO dto = new AvistamientoFrontDTO();
        dto.setLatitud(-34.603684);
        dto.setLongitud(-58.381555);
        dto.setDescripcion("Descripcion completa del avistamiento");
        dto.setFotoBloqueada(false);
        dto.setAvistadorNombre("Juan Perez");
        dto.setDesaparecidoNombre("Carlos");
        dto.setDesaparecidoApellido("Vazquez");
        dto.setDesaparecidoFotoBloqueada(false);
        return dto;
    }
}
