package edu.utn.proyecto.domain.model.enums;
import java.util.Map;

public enum NivelExposicion {
    PUBLICO,
    RESTRINGIDO,
    SECRETO;

    private static final Map<Rol, NivelExposicion> ROL_A_NIVEL = Map.of(
            Rol.NO_REGISTRADO, PUBLICO,
            Rol.USUARIO, RESTRINGIDO,
            Rol.ADMINISTRADOR, SECRETO
    );

    public static NivelExposicion fromRol(Rol rol) {
        return ROL_A_NIVEL.get(rol);
    }
}