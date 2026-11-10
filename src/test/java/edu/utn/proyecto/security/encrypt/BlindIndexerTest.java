package edu.utn.proyecto.security.encrypt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class BlindIndexerTest {

    private BlindIndexer blindIndexer;

    @BeforeEach
    void setUp() {
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < 32; i++) keyBytes[i] = (byte) (i + 50);
        blindIndexer = new BlindIndexer(Base64.getEncoder().encodeToString(keyBytes));
    }

    @Test
    void index_nulo_retornaNull() {
        assertNull(blindIndexer.index(null));
    }

    @Test
    void index_esDeterminista_mismaDniSiempreMismoHmac() {
        String hmac1 = blindIndexer.index("12345678");
        String hmac2 = blindIndexer.index("12345678");

        assertThat(hmac1).isNotNull().isEqualTo(hmac2);
    }

    @Test
    void index_diferentesEntradasProducenDiferentesHmacs() {
        String hmac1 = blindIndexer.index("12345678");
        String hmac2 = blindIndexer.index("87654321");

        assertThat(hmac1).isNotEqualTo(hmac2);
    }

    @Test
    void index_salidaEs64CaracteresHexadecimales() {
        String hmac = blindIndexer.index("12345678");

        assertThat(hmac)
                .hasSize(64)
                .matches("[0-9a-f]{64}")
                .as("El output debe ser hex SHA-256 en minúsculas");
    }

    @Test
    void index_normalizaLowercaseYTrim_mismoDni() {
        // BlindIndexer normalizes: lowercase + trim
        // Para DNIs numéricos el resultado es idéntico
        String hmac1 = blindIndexer.index("12345678");
        String hmac2 = blindIndexer.index("  12345678  ");

        assertThat(hmac1).isEqualTo(hmac2);
    }

    @Test
    void index_normalizaLowercase_paraEmail() {
        String hmac1 = blindIndexer.index("USUARIO@TEST.COM");
        String hmac2 = blindIndexer.index("usuario@test.com");
        String hmac3 = blindIndexer.index("  usuario@test.com  ");

        assertThat(hmac1).isEqualTo(hmac2).isEqualTo(hmac3);
    }
}
