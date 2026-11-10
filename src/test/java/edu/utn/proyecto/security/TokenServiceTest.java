package edu.utn.proyecto.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private final String SECRET = "AUTO_GENERADO_SECRETO_TEST_MUY_LARGO_SUPERA_32_BYTES";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "SECRET", SECRET);
        ReflectionTestUtils.setField(tokenService, "cookieSecure", false);
        ReflectionTestUtils.setField(tokenService, "cookieMaxAge", 3600);
    }

    @Test
    void testGenerateToken() {
        String token = tokenService.generate("avistador1", "11222333", "test@test.com", "Juan Perez", "USUARIO");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        // El token viene en 3 partes: header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testWriteCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String fakeJwt = "a.fake.jwt";

        tokenService.writeCookie(response, fakeJwt);

        Cookie cookie = response.getCookie(TokenService.COOKIE_NAME);
        assertNotNull(cookie);
        assertEquals(fakeJwt, cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.getSecure()); // Definido por mock property
        assertEquals(3600, cookie.getMaxAge());
    }

    @Test
    void testClearCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        tokenService.clearCookie(response);

        Cookie cookie = response.getCookie(TokenService.COOKIE_NAME);
        assertNotNull(cookie);
        assertEquals("", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertEquals(0, cookie.getMaxAge());
    }
}
