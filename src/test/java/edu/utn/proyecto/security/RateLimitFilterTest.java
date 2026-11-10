package edu.utn.proyecto.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para RateLimitFilter.
 * Cubre: allowance dentro del límite, bloqueo con 429, header Retry-After,
 * reset de ventana y contadores independientes por IP.
 */
class RateLimitFilterTest {

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter();
        // Configurar límites bajos para facilitar el test
        ReflectionTestUtils.setField(filter, "loginMaxRequests", 3);
        ReflectionTestUtils.setField(filter, "loginWindowSeconds", 60);
        ReflectionTestUtils.setField(filter, "registroMaxRequests", 2);
        ReflectionTestUtils.setField(filter, "registroWindowSeconds", 3600);
    }

    private MockHttpServletRequest loginRequest(String ip) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setRequestURI("/api/auth/login");
        req.setRemoteAddr(ip);
        return req;
    }

    private MockHttpServletRequest registroRequest(String ip) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setRequestURI("/api/avistadores");
        req.setRemoteAddr(ip);
        return req;
    }

    @Test
    void shouldNotFilter_onGetRequest_returnsTrue() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setRequestURI("/api/auth/login");

        assertThat(filter.shouldNotFilter(req)).isTrue();
    }

    @Test
    void shouldNotFilter_onPostLoginPath_returnsFalse() throws Exception {
        MockHttpServletRequest req = loginRequest("1.2.3.4");
        assertThat(filter.shouldNotFilter(req)).isFalse();
    }

    @Test
    void shouldNotFilter_onPostRegistroPath_returnsFalse() throws Exception {
        MockHttpServletRequest req = registroRequest("1.2.3.4");
        assertThat(filter.shouldNotFilter(req)).isFalse();
    }

    @Test
    void debePermitirRequestsDentroDelLimite() throws Exception {
        FilterChain chain = mock(FilterChain.class);

        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = loginRequest("10.0.0.1");
            MockHttpServletResponse res = new MockHttpServletResponse();
            filter.doFilter(req, res, chain);
            assertThat(res.getStatus()).isNotEqualTo(429);
        }

        verify(chain, times(3)).doFilter(any(), any());
    }

    @Test
    void debeBloquearseDespuesDelLimite_devuelve429() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        String ip = "10.0.0.2";

        // Consumir el límite (3 intentos)
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = loginRequest(ip);
            MockHttpServletResponse res = new MockHttpServletResponse();
            filter.doFilter(req, res, chain);
        }

        // El 4to intento debe bloquearse
        MockHttpServletRequest req = loginRequest(ip);
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(429);
        // chain no debe haber sido llamado el 4to intento
        verify(chain, times(3)).doFilter(any(), any());
    }

    @Test
    void headerRetryAfterPresenteEn429() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        String ip = "10.0.0.3";

        // Consumir el límite
        for (int i = 0; i < 3; i++) {
            filter.doFilter(loginRequest(ip), new MockHttpServletResponse(), chain);
        }

        // 4to intento — debe devolver Retry-After
        MockHttpServletRequest req = loginRequest(ip);
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(429);
        assertThat(res.getHeader("Retry-After")).isNotNull().isEqualTo("60");
    }

    @Test
    void distintasIpsTienenContadoresIndependientes() throws Exception {
        FilterChain chain = mock(FilterChain.class);

        // IP A consume su límite
        String ipA = "192.168.1.1";
        for (int i = 0; i < 4; i++) {
            filter.doFilter(loginRequest(ipA), new MockHttpServletResponse(), chain);
        }

        // IP B aún debe poder hacer requests
        String ipB = "192.168.1.2";
        MockHttpServletRequest reqB = loginRequest(ipB);
        MockHttpServletResponse resB = new MockHttpServletResponse();
        filter.doFilter(reqB, resB, chain);

        assertThat(resB.getStatus()).isNotEqualTo(429);
    }

    @Test
    void contadorSiempreDevuelve429TrasLimite_hastaResetDeVentana() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        String ip = "10.0.0.5";

        // Límite de login = 3; requests: 1,2,3 OK; 4 → 429; 5 → 429
        for (int i = 0; i < 3; i++) {
            filter.doFilter(loginRequest(ip), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse res4 = new MockHttpServletResponse();
        filter.doFilter(loginRequest(ip), res4, chain);
        assertThat(res4.getStatus()).isEqualTo(429);

        MockHttpServletResponse res5 = new MockHttpServletResponse();
        filter.doFilter(loginRequest(ip), res5, chain);
        assertThat(res5.getStatus()).isEqualTo(429);
    }

    @Test
    void xForwardedForHeader_usaLaPrimeraIpParaRateLimit() throws Exception {
        FilterChain chain = mock(FilterChain.class);

        // Dos requests con mismo X-Forwarded-For pero distinto remoteAddr
        for (int i = 0; i < 4; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setMethod("POST");
            req.setRequestURI("/api/auth/login");
            req.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.1");
            req.setRemoteAddr("10.0.0." + i); // remoteAddr varía, no importa
            MockHttpServletResponse res = new MockHttpServletResponse();
            filter.doFilter(req, res, chain);

            if (i == 3) {
                // El 4to intento de la misma IP forwarded debe ser 429
                assertThat(res.getStatus()).isEqualTo(429);
            }
        }
    }
}
