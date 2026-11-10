package edu.utn.proyecto.domain.service.helpers.abstraccion;

public interface FileHasher {
    String sha256(byte[] data);
}