package edu.utn.proyecto.domain.service.helpers;

import edu.utn.proyecto.domain.model.concreta.Avistamiento;
import edu.utn.proyecto.domain.model.enums.Rol;
import edu.utn.proyecto.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisibilidadResolverTest {

    @Mock
    private SecurityContextHelper securityHelper;

    private VisibilidadResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new VisibilidadResolver(securityHelper);
    }

    @Test
    void administrador_invocaQueryAdmin() {
        @SuppressWarnings("unchecked")
        Supplier<List<Avistamiento>> queryAdmin = mock(Supplier.class);
        @SuppressWarnings("unchecked")
        Function<UUID, List<Avistamiento>> queryUsuario = mock(Function.class);
        @SuppressWarnings("unchecked")
        Supplier<List<Avistamiento>> queryPublico = mock(Supplier.class);

        List<Avistamiento> expected = List.of(new Avistamiento());

        when(securityHelper.getCurrentRol()).thenReturn(Rol.ADMINISTRADOR);
        when(queryAdmin.get()).thenReturn(expected);

        List<Avistamiento> result = resolver.resolver(queryAdmin, queryUsuario, queryPublico);

        assertThat(result).isEqualTo(expected);
        verify(queryAdmin).get();
        verifyNoInteractions(queryUsuario, queryPublico);
    }

    @Test
    void usuario_invocaQueryUsuarioConSuUuid() {
        @SuppressWarnings("unchecked")
        Supplier<List<Avistamiento>> queryAdmin = mock(Supplier.class);
        @SuppressWarnings("unchecked")
        Function<UUID, List<Avistamiento>> queryUsuario = mock(Function.class);
        @SuppressWarnings("unchecked")
        Supplier<List<Avistamiento>> queryPublico = mock(Supplier.class);

        UUID userId = UUID.randomUUID();
        List<Avistamiento> expected = List.of(new Avistamiento());

        when(securityHelper.getCurrentRol()).thenReturn(Rol.USUARIO);
        when(securityHelper.getCurrentUserId()).thenReturn(Optional.of(userId));
        when(queryUsuario.apply(userId)).thenReturn(expected);

        List<Avistamiento> result = resolver.resolver(queryAdmin, queryUsuario, queryPublico);

        assertThat(result).isEqualTo(expected);
        verify(queryUsuario).apply(userId);
        verifyNoInteractions(queryAdmin, queryPublico);
    }

    @Test
    void noRegistrado_invocaQueryPublico() {
        @SuppressWarnings("unchecked")
        Supplier<List<Avistamiento>> queryAdmin = mock(Supplier.class);
        @SuppressWarnings("unchecked")
        Function<UUID, List<Avistamiento>> queryUsuario = mock(Function.class);
        @SuppressWarnings("unchecked")
        Supplier<List<Avistamiento>> queryPublico = mock(Supplier.class);

        List<Avistamiento> expected = List.of(new Avistamiento());

        when(securityHelper.getCurrentRol()).thenReturn(Rol.NO_REGISTRADO);
        when(queryPublico.get()).thenReturn(expected);

        List<Avistamiento> result = resolver.resolver(queryAdmin, queryUsuario, queryPublico);

        assertThat(result).isEqualTo(expected);
        verify(queryPublico).get();
        verifyNoInteractions(queryAdmin, queryUsuario);
    }

    @Test
    void filtrarEnMemoria_respetaReglasPorRol() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Avistamiento publico = buildAvistamiento(true, otherUserId);
        Avistamiento privadoPropio = buildAvistamiento(false, userId);
        Avistamiento privadoAjeno = buildAvistamiento(false, otherUserId);
        List<Avistamiento> input = List.of(publico, privadoPropio, privadoAjeno);

        when(securityHelper.getCurrentRol()).thenReturn(Rol.ADMINISTRADOR);
        List<Avistamiento> adminResult = resolver.filtrarEnMemoria(input);
        assertThat(adminResult).hasSize(3);

        when(securityHelper.getCurrentRol()).thenReturn(Rol.USUARIO);
        when(securityHelper.getCurrentUserId()).thenReturn(Optional.of(userId));
        List<Avistamiento> userResult = resolver.filtrarEnMemoria(input);
        assertThat(userResult).containsExactlyInAnyOrder(publico, privadoPropio);

        when(securityHelper.getCurrentRol()).thenReturn(Rol.NO_REGISTRADO);
        when(securityHelper.getCurrentUserId()).thenReturn(Optional.empty());
        List<Avistamiento> publicResult = resolver.filtrarEnMemoria(input);
        assertThat(publicResult).containsExactly(publico);
    }

    private static Avistamiento buildAvistamiento(boolean publico, UUID avistadorId) {
        Avistamiento a = new Avistamiento();
        a.setId(UUID.randomUUID());
        a.setPublico(publico);
        a.setAvistadorId(avistadorId);
        return a;
    }
}
