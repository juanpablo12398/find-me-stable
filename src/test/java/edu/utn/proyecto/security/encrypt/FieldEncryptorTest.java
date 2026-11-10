package edu.utn.proyecto.security.encrypt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class FieldEncryptorTest {

    private FieldEncryptor encryptor;

    @BeforeEach
    void setUp() {
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < 32; i++) {
            keyBytes[i] = (byte) i;
        }
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        encryptor = new FieldEncryptor(base64Key);
    }

    @Test
    void testEncryptDecryptSuccessfully() {
        String originalText = "12345678";
        String encrypted = encryptor.encrypt(originalText);

        assertNotNull(encrypted, "El texto cifrado no debe ser nulo");
        assertNotEquals(originalText, encrypted, "El texto cifrado debe ser diferente al original");

        String decrypted = encryptor.decrypt(encrypted);
        assertEquals(originalText, decrypted, "El texto descifrado debe coincidir con el original");
    }

    @Test
    void testEncryptNullReturnsNull() {
        assertNull(encryptor.encrypt(null), "Si recibe null, debe retornar null");
    }

    @Test
    void testDecryptNullReturnsNull() {
        assertNull(encryptor.decrypt(null), "Si recibe null, debe retornar null");
    }

    @Test
    void testDifferentEncryptionsForSameTextDueToRandomIV() {
        String originalText = "InformacionSensible123";
        String encrypted1 = encryptor.encrypt(originalText);
        String encrypted2 = encryptor.encrypt(originalText);

        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2, "Múltiples cifrados deben generar textos diferentes");

        assertEquals(originalText, encryptor.decrypt(encrypted1));
        assertEquals(originalText, encryptor.decrypt(encrypted2));
    }

    @Test
    void testDescifradoConCiphertextManipuladoLanzaExcepcion() {
        // AES-GCM tiene tag de autenticación: cualquier manipulación debe fallar
        String encrypted = encryptor.encrypt("dato-sensible");
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        // Modificar el último byte (parte del tag de autenticación)
        decoded[decoded.length - 1] ^= 0xFF;
        String tampered = Base64.getEncoder().encodeToString(decoded);

        assertThrows(RuntimeException.class, () -> encryptor.decrypt(tampered),
                "AES-GCM debe detectar manipulación del ciphertext");
    }

    @Test
    void testDescifradoConClaveDiferenteLanzaExcepcion() {
        // Cifrar con clave A, intentar descifrar con clave B → fallo de autenticación
        byte[] otherKeyBytes = new byte[32];
        for (int i = 0; i < 32; i++) otherKeyBytes[i] = (byte) (i + 100);
        FieldEncryptor otherEncryptor = new FieldEncryptor(Base64.getEncoder().encodeToString(otherKeyBytes));

        String encrypted = encryptor.encrypt("dato-privado");

        assertThrows(RuntimeException.class, () -> otherEncryptor.decrypt(encrypted),
                "Descifrar con clave incorrecta debe lanzar excepción");
    }

    @Test
    void testCadenaVaciaSeEncriptaYDescriptaCorrectamente() {
        String empty = "";
        String encrypted = encryptor.encrypt(empty);

        assertNotNull(encrypted);
        assertNotEquals(empty, encrypted);
        assertEquals(empty, encryptor.decrypt(encrypted));
    }

    @Test
    void testCaracteresEspecialesUTF8() {
        String special = "DNI: 12.345.678 - José María Ñoño";
        String encrypted = encryptor.encrypt(special);

        assertNotNull(encrypted);
        assertEquals(special, encryptor.decrypt(encrypted));
    }
}
