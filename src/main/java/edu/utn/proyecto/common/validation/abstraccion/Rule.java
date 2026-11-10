package edu.utn.proyecto.common.validation.abstraccion;

public interface Rule<T> {
    void check(T target);
}
