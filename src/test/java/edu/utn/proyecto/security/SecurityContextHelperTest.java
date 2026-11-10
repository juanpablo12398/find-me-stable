package edu.utn.proyecto.security;

import edu.utn.proyecto.domain.model.enums.Rol;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityContextHelperTest {

    private SecurityContextHelper helper;

    @BeforeEach
    void setUp() {
        helper = new SecurityContextHelper();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentDni_returnEmpty_whenNotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThat(helper.getCurrentDni()).isEmpty();
    }

    @Test
    void getCurrentDni_returnEmpty_whenAnonymousUser() {
        Authentication auth = new UsernamePasswordAuthenticationToken("anonymousUser", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.getCurrentDni()).isEmpty();
    }

    @Test
    void getCurrentDni_returnDni() {
        Authentication auth = new UsernamePasswordAuthenticationToken("12345678", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.getCurrentDni()).isPresent().contains("12345678");
    }

    @Test
    void getCurrentUserId_returnEmpty_whenNotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThat(helper.getCurrentUserId()).isEmpty();
    }

    @Test
    void getCurrentUserId_returnEmpty_whenDetailsNotUUID() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        auth.setDetails("StringDetail");
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.getCurrentUserId()).isEmpty();
    }

    @Test
    void getCurrentUserId_returnUUID() {
        UUID expectedId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        auth.setDetails(expectedId);
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.getCurrentUserId()).isPresent().contains(expectedId);
    }

    @Test
    void getCurrentRol_returnNoRegistrado_whenNotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThat(helper.getCurrentRol()).isEqualTo(Rol.NO_REGISTRADO);
    }

    @Test
    void getCurrentRol_returnNoRegistrado_whenNoAuthorities() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.getCurrentRol()).isEqualTo(Rol.NO_REGISTRADO);
    }

    @Test
    void getCurrentRol_returnRol() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, 
            List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.getCurrentRol()).isEqualTo(Rol.ADMINISTRADOR);
    }

    @Test
    void getCurrentRol_returnNoRegistrado_whenInvalidRol() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, 
            List.of(new SimpleGrantedAuthority("ROLE_INVALIDO")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.getCurrentRol()).isEqualTo(Rol.NO_REGISTRADO);
    }

    @Test
    void isAuthenticated_returnTrue_whenDniPresent() {
        Authentication auth = new UsernamePasswordAuthenticationToken("12345678", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.isAuthenticated()).isTrue();
    }

    @Test
    void isAuthenticated_returnFalse_whenNoDni() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThat(helper.isAuthenticated()).isFalse();
    }

    @Test
    void isAdmin_returnTrue() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, 
            List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThat(helper.isAdmin()).isTrue();
    }
}
