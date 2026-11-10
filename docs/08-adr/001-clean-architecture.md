# ADR 001: Clean Architecture como base

## Contexto
El sistema requiere mantenibilidad alta, pruebas unitarias extensibles y desacople entre dominio e infraestructura.

## Decision
Adoptar arquitectura limpia con capas de dominio, aplicacion e infraestructura, usando puertos y adaptadores.

## Consecuencias
Positivas:
- Mejor testabilidad y aislamiento de reglas de negocio.
- Menor acoplamiento con Spring/JPA.

Negativas:
- Mayor complejidad inicial y mas clases/interfaces.