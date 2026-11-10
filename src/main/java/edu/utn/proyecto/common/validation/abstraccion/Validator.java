package edu.utn.proyecto.common.validation.abstraccion;

public interface Validator<T> {
    void validate(T target);
}
