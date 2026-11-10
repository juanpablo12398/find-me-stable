package edu.utn.proyecto.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtCookieFilterTest {

    private JwtVerifier jwtVerifier;
    private JwtCookieFilter filter;

    @BeforeEach
    void setUp() {
        jwtVerifier = mock(JwtVerifier.class);
        filter = new JwtCookieFilter(jwtVerifier);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_skipsStaticAssets() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/js/app.js");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_noCookies_continuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_invalidCookie_continuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        request.setCookies(new Cookie("invalid_cookie", "some_value"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_validCookie_authenticatesUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        Cookie cookie = new Cookie(TokenService.COOKIE_NAME, "valid_token");
        request.setCookies(cookie);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        Claims claims = mock(Claims.class);
        when(jwtVerifier.parse("valid_token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("12345678");
        when(claims.get("rol", String.class)).thenReturn("ADMINISTRADOR");
        UUID expectedUuid = UUID.randomUUID();
        when(claims.get("id", String.class)).thenReturn(expectedUuid.toString());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getPrincipal()).isEqualTo("12345678");
        assertThat(auth.getDetails()).isEqualTo(expectedUuid);
        assertThat(auth.getAuthorities()).hasSize(1)
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    @Test
    void doFilterInternal_validCookie_throwsExceptionOnParse_continuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        Cookie cookie = new Cookie(TokenService.COOKIE_NAME, "bad_token");
        request.setCookies(cookie);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtVerifier.parse("bad_token")).thenThrow(new RuntimeException("invalid token"));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_tokenExpirado_securityContextQuedaVacio() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/desaparecidos");
        Cookie cookie = new Cookie(TokenService.COOKIE_NAME, "expired_token");
        request.setCookies(cookie);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // Simular que jjwt lanza ExpiredJwtException (subclase de RuntimeException)
        when(jwtVerifier.parse("expired_token"))
                .thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expirado"));

        filter.doFilter(request, response, chain);

        // El filtro captura la excepción silenciosamente y continúa la cadena
        verify(chain).doFilter(request, response);
        // El SecurityContext debe quedar sin autenticación
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_firmaInvalida_securityContextQuedaVacio() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/avistadores");
        Cookie cookie = new Cookie(TokenService.COOKIE_NAME, "tampered_token");
        request.setCookies(cookie);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtVerifier.parse("tampered_token"))
                .thenThrow(new io.jsonwebtoken.security.SignatureException("Firma inválida"));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_sinToken_endpointPublico_continuesChainSinAutenticacion() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // Endpoint público sin cookie de sesión
        request.setRequestURI("/api/auth/login");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
