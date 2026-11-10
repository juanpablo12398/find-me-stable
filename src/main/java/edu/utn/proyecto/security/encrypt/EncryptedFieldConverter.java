package edu.utn.proyecto.security.encrypt;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

/**
 * JPA AttributeConverter que cifra/descifra campos transparentemente.
 * Uso en entity:
 *   @Convert(converter = EncryptedFieldConverter.class)
 *   @Column(name = "telefono_enc")
 *   prívate String telefono;
 * JPA llama convertToDatabaseColumn al guardar → cifra.
 * JPA llama convertToEntityAttribute al leer → descifra.
 * La aplicación trabaja siempre con texto plano.
 */
@Converter(autoApply = false)
public class EncryptedFieldConverter implements AttributeConverter<String, String> {

    private static FieldEncryptor encryptor;

    public EncryptedFieldConverter(FieldEncryptor fieldEncryptor) {
        EncryptedFieldConverter.encryptor = fieldEncryptor;
    }

    public EncryptedFieldConverter() {}

    @Override
    public String convertToDatabaseColumn(String plaintext) {
        if (encryptor == null) return plaintext;
        return encryptor.encrypt(plaintext);
    }

    @Override
    public String convertToEntityAttribute(String ciphertext) {
        if (encryptor == null) return ciphertext;
        return encryptor.decrypt(ciphertext);
    }
}